package sv.udb.cafedonbosco.service.impl;

import sv.udb.cafedonbosco.dao.InventarioDAO;
import sv.udb.cafedonbosco.dao.ProductoDAO;
import sv.udb.cafedonbosco.dao.impl.InventarioDAOImpl;
import sv.udb.cafedonbosco.dao.impl.ProductoDAOImpl;
import sv.udb.cafedonbosco.dto.response.CarritoResponseDTO;
import sv.udb.cafedonbosco.exception.RecursoNoEncontradoException;
import sv.udb.cafedonbosco.exception.ValidacionException;
import sv.udb.cafedonbosco.model.Carrito;
import sv.udb.cafedonbosco.model.CarritoItem;
import sv.udb.cafedonbosco.model.Inventario;
import sv.udb.cafedonbosco.model.OpcionSeleccionada;
import sv.udb.cafedonbosco.model.Producto;
import sv.udb.cafedonbosco.service.CarritoService;
import sv.udb.cafedonbosco.service.PersonalizacionService;
import sv.udb.cafedonbosco.util.ValidacionUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CarritoServiceImpl implements CarritoService {

    private static final int CANTIDAD_MAXIMA_POR_PRODUCTO = 20;

    private final ProductoDAO productoDAO;
    private final InventarioDAO inventarioDAO;
    private final PersonalizacionService personalizacionService;

    public CarritoServiceImpl() {
        this(new ProductoDAOImpl(), new InventarioDAOImpl(), new PersonalizacionServiceImpl());
    }

    /** Permite inyectar DAOs de prueba (Mockito) sin tocar una base de datos real. */
    public CarritoServiceImpl(ProductoDAO productoDAO, InventarioDAO inventarioDAO) {
        this(productoDAO, inventarioDAO, new PersonalizacionServiceImpl());
    }

    public CarritoServiceImpl(ProductoDAO productoDAO, InventarioDAO inventarioDAO, PersonalizacionService personalizacionService) {
        this.productoDAO = productoDAO;
        this.inventarioDAO = inventarioDAO;
        this.personalizacionService = personalizacionService;
    }

    @Override
    public void agregarProducto(Carrito carrito, int productoId, int cantidad) {
        agregarProducto(carrito, productoId, cantidad, null);
    }

    @Override
    public void agregarProducto(Carrito carrito, int productoId, int cantidad, List<Integer> opcionIds) {
        if (!ValidacionUtil.esCantidadValida(cantidad)) {
            throw new ValidacionException("La cantidad debe ser mayor a 0.");
        }
        CarritoItem existente = carrito.getItems().get(productoId);
        int cantidadTotalDeseada = (existente != null ? existente.getCantidad() : 0) + cantidad;
        Producto producto = validarProductoDisponible(productoId, cantidadTotalDeseada);
        // Solo se consulta PersonalizacionService cuando el cliente realmente
        // envio opciones: asi un carrito sin personalizacion (todo el flujo
        // JSP existente) no depende de esa capa para nada.
        List<OpcionSeleccionada> opciones = (opcionIds != null && !opcionIds.isEmpty())
                ? personalizacionService.validarYResolverOpciones(productoId, opcionIds)
                : new ArrayList<>();
        carrito.agregarProducto(new CarritoItem(
                producto.getId(), producto.getNombre(), producto.getPrecio(), cantidad, producto.getImagen(), opciones
        ));
    }

    @Override
    public void actualizarCantidad(Carrito carrito, int productoId, int cantidad) {
        if (!carrito.getItems().containsKey(productoId)) {
            throw new RecursoNoEncontradoException("El producto no esta en el carrito.");
        }
        validarProductoDisponible(productoId, cantidad);
        carrito.actualizarCantidad(productoId, cantidad);
    }

    @Override
    public void eliminarProducto(Carrito carrito, int productoId) {
        carrito.eliminarProducto(productoId);
    }

    @Override
    public void vaciar(Carrito carrito) {
        carrito.vaciar();
    }

    @Override
    public CarritoResponseDTO obtenerResumen(Carrito carrito) {
        resincronizarCarrito(carrito);
        BigDecimal subtotal = carrito.calcularSubtotal();
        BigDecimal envio = BigDecimal.ZERO;
        return new CarritoResponseDTO(
                new ArrayList<>(carrito.getItems().values()),
                carrito.contarUnidades(),
                subtotal,
                envio,
                subtotal.add(envio)
        );
    }

    /**
     * Antes de mostrar el carrito se vuelve a leer cada producto contra la
     * BD: si dejo de existir o quedo inactivo se quita del carrito, si el
     * precio cambio se actualiza (el precio guardado al agregarlo puede
     * quedar obsoleto) y si la cantidad guardada ya no cabe en el stock
     * disponible se recorta. El checkout siempre vuelve a validar todo
     * esto dentro de su propia transaccion, pero esto evita que el
     * cliente vea un total distinto al que realmente se le cobrara.
     */
    private void resincronizarCarrito(Carrito carrito) {
        List<Integer> aEliminar = new ArrayList<>();
        for (CarritoItem item : carrito.getItems().values()) {
            Producto producto = productoDAO.buscarPorId(item.getProductoId());
            if (producto == null || !Boolean.TRUE.equals(producto.getActivo())) {
                aEliminar.add(item.getProductoId());
                continue;
            }
            item.setNombreProducto(producto.getNombre());
            item.setPrecioUnitario(producto.getPrecio());
            item.setImagen(producto.getImagen());

            Inventario inventario = inventarioDAO.buscarPorProducto(item.getProductoId());
            int disponible = inventario != null ? inventario.getCantidad() : 0;
            if (disponible <= 0) {
                aEliminar.add(item.getProductoId());
            } else if (item.getCantidad() > disponible) {
                item.setCantidad(disponible);
            }
        }
        for (Integer productoId : aEliminar) {
            carrito.eliminarProducto(productoId);
        }
    }

    private Producto validarProductoDisponible(int productoId, int cantidad) {
        if (!ValidacionUtil.esCantidadValida(cantidad) || cantidad > CANTIDAD_MAXIMA_POR_PRODUCTO) {
            throw new ValidacionException("La cantidad debe estar entre 1 y " + CANTIDAD_MAXIMA_POR_PRODUCTO + ".");
        }
        Producto producto = productoDAO.buscarPorId(productoId);
        if (producto == null || !Boolean.TRUE.equals(producto.getActivo())) {
            throw new RecursoNoEncontradoException("El producto ya no esta disponible.");
        }
        Inventario inventario = inventarioDAO.buscarPorProducto(productoId);
        if (inventario == null || inventario.getCantidad() < cantidad) {
            throw new ValidacionException("Solo hay " + (inventario == null ? 0 : inventario.getCantidad())
                    + " unidades disponibles de " + producto.getNombre() + ".");
        }
        return producto;
    }
}
