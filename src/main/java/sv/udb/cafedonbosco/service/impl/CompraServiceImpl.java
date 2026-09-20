package sv.udb.cafedonbosco.service.impl;

import sv.udb.cafedonbosco.dao.CompraDAO;
import sv.udb.cafedonbosco.dao.InventarioDAO;
import sv.udb.cafedonbosco.dao.ProductoDAO;
import sv.udb.cafedonbosco.dao.impl.CompraDAOImpl;
import sv.udb.cafedonbosco.dao.impl.InventarioDAOImpl;
import sv.udb.cafedonbosco.dao.impl.ProductoDAOImpl;
import sv.udb.cafedonbosco.dto.request.CompraRequestDTO;
import sv.udb.cafedonbosco.dto.response.CompraResponseDTO;
import sv.udb.cafedonbosco.dto.response.DetalleVentaResponseDTO;
import sv.udb.cafedonbosco.exception.ErrorInternoException;
import sv.udb.cafedonbosco.exception.RecursoNoEncontradoException;
import sv.udb.cafedonbosco.exception.ValidacionException;
import sv.udb.cafedonbosco.model.Compra;
import sv.udb.cafedonbosco.model.DetalleCompra;
import sv.udb.cafedonbosco.model.Producto;
import sv.udb.cafedonbosco.service.CompraService;
import sv.udb.cafedonbosco.util.ConexionBD;
import sv.udb.cafedonbosco.util.ValidacionUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CompraServiceImpl implements CompraService {

    private final CompraDAO compraDAO;
    private final ProductoDAO productoDAO;
    private final InventarioDAO inventarioDAO;

    public CompraServiceImpl() {
        this.compraDAO = new CompraDAOImpl();
        this.productoDAO = new ProductoDAOImpl();
        this.inventarioDAO = new InventarioDAOImpl();
    }

    @Override
    public CompraResponseDTO registrar(CompraRequestDTO datos, int usuarioAdminId) {
        if (datos == null || !ValidacionUtil.esTextoValido(datos.getProveedor(), 150)) {
            throw new ValidacionException("El proveedor es obligatorio.");
        }
        if (datos.getItems() == null || datos.getItems().isEmpty()) {
            throw new ValidacionException("La compra debe incluir al menos un producto.");
        }

        Connection conexion = null;
        try {
            conexion = ConexionBD.obtenerConexion();
            conexion.setAutoCommit(false);

            BigDecimal total = BigDecimal.ZERO;
            List<DetalleCompra> detalles = new ArrayList<>();
            for (CompraRequestDTO.DetalleCompraRequestDTO item : datos.getItems()) {
                if (item.getProductoId() == null || !ValidacionUtil.esCantidadValida(item.getCantidad())
                        || item.getCostoUnitario() == null || item.getCostoUnitario().compareTo(BigDecimal.ZERO) < 0) {
                    throw new ValidacionException("Cada linea de la compra necesita producto, cantidad y costo validos.");
                }
                Producto producto = productoDAO.buscarPorId(item.getProductoId());
                if (producto == null) {
                    throw new RecursoNoEncontradoException("Uno de los productos de la compra no existe.");
                }
                BigDecimal subtotal = item.getCostoUnitario().multiply(BigDecimal.valueOf(item.getCantidad()));
                total = total.add(subtotal);
                detalles.add(new DetalleCompra(producto.getId(), item.getCantidad(), item.getCostoUnitario(), subtotal));
            }

            Compra compra = new Compra();
            compra.setProveedor(datos.getProveedor().trim());
            compra.setUsuarioId(usuarioAdminId);
            compra.setTotal(total);
            compraDAO.crear(conexion, compra);

            for (DetalleCompra detalle : detalles) {
                compraDAO.crearDetalle(conexion, detalle, compra.getId());
                inventarioDAO.incrementarStock(conexion, detalle.getProductoId(), detalle.getCantidad());
            }
            compra.setDetalles(detalles);

            conexion.commit();
            return aResponseDTO(compra);
        } catch (SQLException e) {
            revertir(conexion);
            throw new ErrorInternoException("Error al registrar la compra", e);
        } catch (RuntimeException e) {
            revertir(conexion);
            throw e;
        } finally {
            cerrar(conexion);
        }
    }

    @Override
    public List<CompraResponseDTO> listarTodas() {
        List<CompraResponseDTO> resultado = new ArrayList<>();
        for (Compra compra : compraDAO.listarTodas()) {
            resultado.add(aResponseDTO(compraDAO.buscarPorId(compra.getId())));
        }
        return resultado;
    }

    private CompraResponseDTO aResponseDTO(Compra compra) {
        CompraResponseDTO dto = new CompraResponseDTO();
        dto.setId(compra.getId());
        dto.setProveedor(compra.getProveedor());
        dto.setTotal(compra.getTotal());
        dto.setFecha(compra.getFecha());
        List<DetalleVentaResponseDTO> items = new ArrayList<>();
        for (DetalleCompra detalle : compra.getDetalles()) {
            Producto producto = productoDAO.buscarPorId(detalle.getProductoId());
            items.add(new DetalleVentaResponseDTO(
                    detalle.getProductoId(),
                    producto != null ? producto.getNombre() : null,
                    detalle.getCantidad(),
                    detalle.getCostoUnitario(),
                    detalle.getSubtotal()
            ));
        }
        dto.setItems(items);
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
