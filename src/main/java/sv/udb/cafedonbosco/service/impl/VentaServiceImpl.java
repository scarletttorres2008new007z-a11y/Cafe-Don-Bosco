package sv.udb.cafedonbosco.service.impl;

import sv.udb.cafedonbosco.dao.InventarioDAO;
import sv.udb.cafedonbosco.dao.ProductoDAO;
import sv.udb.cafedonbosco.dao.VentaDAO;
import sv.udb.cafedonbosco.dao.impl.InventarioDAOImpl;
import sv.udb.cafedonbosco.dao.impl.ProductoDAOImpl;
import sv.udb.cafedonbosco.dao.impl.VentaDAOImpl;
import sv.udb.cafedonbosco.dto.request.CarritoItemRequestDTO;
import sv.udb.cafedonbosco.dto.request.CheckoutRequestDTO;
import sv.udb.cafedonbosco.dto.request.VentaPresencialRequestDTO;
import sv.udb.cafedonbosco.dto.response.DetalleVentaResponseDTO;
import sv.udb.cafedonbosco.dto.response.VentaResponseDTO;
import sv.udb.cafedonbosco.exception.ErrorInternoException;
import sv.udb.cafedonbosco.exception.RecursoNoEncontradoException;
import sv.udb.cafedonbosco.exception.StockInsuficienteException;
import sv.udb.cafedonbosco.exception.ValidacionException;
import sv.udb.cafedonbosco.model.CarritoItem;
import sv.udb.cafedonbosco.model.Carrito;
import sv.udb.cafedonbosco.model.DetalleVenta;
import sv.udb.cafedonbosco.model.EstadoVenta;
import sv.udb.cafedonbosco.model.Producto;
import sv.udb.cafedonbosco.model.TipoVenta;
import sv.udb.cafedonbosco.model.Venta;
import sv.udb.cafedonbosco.service.VentaService;
import sv.udb.cafedonbosco.util.ConexionBD;
import sv.udb.cafedonbosco.util.Constantes;
import sv.udb.cafedonbosco.util.ValidacionUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class VentaServiceImpl implements VentaService {

    private static final Set<String> METODOS_PAGO_VALIDOS = Set.of("TARJETA", "TRANSFERENCIA", "CONTRA_ENTREGA", "EFECTIVO");

    private final VentaDAO ventaDAO;
    private final ProductoDAO productoDAO;
    private final InventarioDAO inventarioDAO;

    public VentaServiceImpl() {
        this.ventaDAO = new VentaDAOImpl();
        this.productoDAO = new ProductoDAOImpl();
        this.inventarioDAO = new InventarioDAOImpl();
    }

    @Override
    public VentaResponseDTO procesarCheckoutWeb(Carrito carrito, CheckoutRequestDTO datos, Integer usuarioId) {
        if (carrito == null || carrito.estaVacio()) {
            throw new ValidacionException("El carrito esta vacio.");
        }
        validarDatosCheckout(datos);

        Venta venta = new Venta();
        venta.setUsuarioId(usuarioId);
        venta.setTipoVenta(TipoVenta.WEB);
        venta.setEstado(EstadoVenta.PENDIENTE);
        venta.setMetodoPago(datos.getMetodoPago());
        venta.setEstadoPago(Constantes.ESTADO_PAGO_PENDIENTE);
        venta.setTipoEntrega(datos.getTipoEntrega());
        venta.setNombreCliente(datos.getNombreCompleto().trim());
        venta.setCorreoCliente(datos.getCorreo().trim().toLowerCase());
        venta.setTelefonoCliente(datos.getTelefono().trim());
        venta.setDireccionCliente(datos.getDireccion());
        venta.setNotas(datos.getNotas());

        List<DetalleVenta> detalles = new ArrayList<>();
        for (CarritoItem item : carrito.getItems().values()) {
            detalles.add(new DetalleVenta(item.getProductoId(), item.getNombreProducto(), item.getCantidad(), null, null));
        }

        Venta registrada = registrarConTransaccion(venta, detalles);
        carrito.vaciar();
        return aResponseDTO(registrada);
    }

    @Override
    public VentaResponseDTO registrarVentaPresencial(VentaPresencialRequestDTO datos, int usuarioAdminId) {
        if (datos == null || datos.getItems() == null || datos.getItems().isEmpty()) {
            throw new ValidacionException("La venta debe incluir al menos un producto.");
        }
        String metodoPago = datos.getMetodoPago() == null ? "EFECTIVO" : datos.getMetodoPago().toUpperCase();
        if (!METODOS_PAGO_VALIDOS.contains(metodoPago)) {
            throw new ValidacionException("Metodo de pago no valido.");
        }

        Venta venta = new Venta();
        venta.setUsuarioId(usuarioAdminId);
        venta.setTipoVenta(TipoVenta.PRESENCIAL);
        venta.setEstado(EstadoVenta.COMPLETADA);
        venta.setMetodoPago(metodoPago);
        venta.setEstadoPago(Constantes.ESTADO_PAGO_APROBADO);
        venta.setTipoEntrega(Constantes.ENTREGA_RECOGER);

        List<DetalleVenta> detalles = new ArrayList<>();
        for (CarritoItemRequestDTO item : datos.getItems()) {
            if (!ValidacionUtil.esCantidadValida(item.getCantidad()) || item.getProductoId() == null) {
                throw new ValidacionException("Cada producto de la venta necesita un id y una cantidad valida.");
            }
            detalles.add(new DetalleVenta(item.getProductoId(), null, item.getCantidad(), null, null));
        }

        Venta registrada = registrarConTransaccion(venta, detalles);
        return aResponseDTO(registrada);
    }

    /**
     * Nucleo transaccional compartido por la venta web y la presencial:
     * vuelve a leer el precio y el stock vigentes de cada producto (nunca
     * se confia en lo que traiga la sesion o la solicitud), descuenta el
     * inventario de forma atomica y registra la cabecera y el detalle en
     * una unica transaccion JDBC.
     */
    private Venta registrarConTransaccion(Venta venta, List<DetalleVenta> detallesSolicitados) {
        Connection conexion = null;
        try {
            conexion = ConexionBD.obtenerConexion();
            conexion.setAutoCommit(false);

            BigDecimal subtotal = BigDecimal.ZERO;
            List<DetalleVenta> detallesFinales = new ArrayList<>();

            for (DetalleVenta solicitado : detallesSolicitados) {
                Producto producto = productoDAO.buscarPorId(solicitado.getProductoId());
                if (producto == null || !Boolean.TRUE.equals(producto.getActivo())) {
                    throw new RecursoNoEncontradoException("Uno de los productos ya no esta disponible.");
                }

                boolean descontado = inventarioDAO.descontarStock(conexion, producto.getId(), solicitado.getCantidad());
                if (!descontado) {
                    throw new StockInsuficienteException(producto.getNombre());
                }

                BigDecimal precioVigente = producto.getPrecio();
                BigDecimal subtotalLinea = precioVigente.multiply(BigDecimal.valueOf(solicitado.getCantidad()));
                subtotal = subtotal.add(subtotalLinea);

                detallesFinales.add(new DetalleVenta(
                        producto.getId(), producto.getNombre(), solicitado.getCantidad(), precioVigente, subtotalLinea
                ));
            }

            BigDecimal envio = BigDecimal.ZERO;
            venta.setSubtotal(subtotal);
            venta.setEnvio(envio);
            venta.setTotal(subtotal.add(envio));
            venta.setTokenTicket(generarTokenTicket());

            ventaDAO.crear(conexion, venta);
            for (DetalleVenta detalle : detallesFinales) {
                ventaDAO.crearDetalle(conexion, detalle, venta.getId());
            }
            venta.setDetalles(detallesFinales);

            conexion.commit();
            return venta;
        } catch (SQLException e) {
            revertir(conexion);
            throw new ErrorInternoException("Error al registrar la venta", e);
        } catch (RuntimeException e) {
            revertir(conexion);
            throw e;
        } finally {
            cerrar(conexion);
        }
    }

    @Override
    public VentaResponseDTO obtenerPorToken(String token) {
        if (!ValidacionUtil.esTextoValido(token)) {
            throw new RecursoNoEncontradoException("Ticket no encontrado.");
        }
        Venta venta = ventaDAO.buscarPorToken(token);
        if (venta == null) {
            throw new RecursoNoEncontradoException("Ticket no encontrado.");
        }
        return aResponseDTO(venta);
    }

    @Override
    public VentaResponseDTO obtenerPorId(int id) {
        Venta venta = ventaDAO.buscarPorId(id);
        if (venta == null) {
            throw new RecursoNoEncontradoException("La venta solicitada no existe.");
        }
        return aResponseDTO(venta);
    }

    @Override
    public List<VentaResponseDTO> listarHistorial(TipoVenta tipoVenta, int limite) {
        List<VentaResponseDTO> resultado = new ArrayList<>();
        for (Venta venta : ventaDAO.listarHistorial(tipoVenta, limite)) {
            VentaResponseDTO dto = aResponseDTO(venta);
            dto.setTokenTicket(null);
            resultado.add(dto);
        }
        return resultado;
    }

    private void validarDatosCheckout(CheckoutRequestDTO datos) {
        if (datos == null || !ValidacionUtil.esTextoValido(datos.getNombreCompleto(), 150)) {
            throw new ValidacionException("El nombre completo es obligatorio.");
        }
        if (!ValidacionUtil.esCorreoValido(datos.getCorreo())) {
            throw new ValidacionException("El correo electronico no es valido.");
        }
        if (!ValidacionUtil.esTelefonoValido(datos.getTelefono())) {
            throw new ValidacionException("El telefono no es valido.");
        }
        if (!Constantes.ENTREGA_RECOGER.equalsIgnoreCase(datos.getTipoEntrega())
                && !Constantes.ENTREGA_DOMICILIO.equalsIgnoreCase(datos.getTipoEntrega())) {
            throw new ValidacionException("El tipo de entrega debe ser RECOGER o DOMICILIO.");
        }
        if (Constantes.ENTREGA_DOMICILIO.equalsIgnoreCase(datos.getTipoEntrega())
                && !ValidacionUtil.esTextoValido(datos.getDireccion(), 255)) {
            throw new ValidacionException("La direccion es obligatoria para la entrega a domicilio.");
        }
        if (datos.getMetodoPago() == null || !METODOS_PAGO_VALIDOS.contains(datos.getMetodoPago().toUpperCase())) {
            throw new ValidacionException("Selecciona un metodo de pago valido.");
        }
        datos.setTipoEntrega(datos.getTipoEntrega().toUpperCase());
        datos.setMetodoPago(datos.getMetodoPago().toUpperCase());
    }

    private String generarTokenTicket() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private VentaResponseDTO aResponseDTO(Venta venta) {
        VentaResponseDTO dto = new VentaResponseDTO();
        dto.setId(venta.getId());
        dto.setTipoVenta(venta.getTipoVenta());
        dto.setEstado(venta.getEstado());
        dto.setSubtotal(venta.getSubtotal());
        dto.setEnvio(venta.getEnvio());
        dto.setTotal(venta.getTotal());
        dto.setMetodoPago(venta.getMetodoPago());
        dto.setEstadoPago(venta.getEstadoPago());
        dto.setTipoEntrega(venta.getTipoEntrega());
        dto.setNombreCliente(venta.getNombreCliente());
        dto.setFecha(venta.getFecha());
        dto.setTokenTicket(venta.getTokenTicket());

        List<DetalleVentaResponseDTO> detalles = new ArrayList<>();
        for (DetalleVenta detalle : venta.getDetalles()) {
            detalles.add(new DetalleVentaResponseDTO(
                    detalle.getProductoId(), detalle.getNombreProducto(), detalle.getCantidad(),
                    detalle.getPrecioUnitario(), detalle.getSubtotal()
            ));
        }
        dto.setDetalles(detalles);
        return dto;
    }

    private void revertir(Connection conexion) {
        if (conexion != null) {
            try {
                conexion.rollback();
            } catch (SQLException ignorada) {
                // La conexion se cerrara de todas formas en el bloque finally.
            }
        }
    }

    private void cerrar(Connection conexion) {
        if (conexion != null) {
            try {
                conexion.close();
            } catch (SQLException ignorada) {
                // No hay una accion util adicional si el cierre falla.
            }
        }
    }
}
