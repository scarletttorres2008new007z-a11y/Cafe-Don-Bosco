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
import sv.udb.cafedonbosco.model.Producto;
import sv.udb.cafedonbosco.service.CarritoService;
import sv.udb.cafedonbosco.util.ValidacionUtil;

import java.math.BigDecimal;
import java.util.ArrayList;

public class CarritoServiceImpl implements CarritoService {

    private static final int CANTIDAD_MAXIMA_POR_PRODUCTO = 20;

    private final ProductoDAO productoDAO;
    private final InventarioDAO inventarioDAO;

    public CarritoServiceImpl() {
        this.productoDAO = new ProductoDAOImpl();
        this.inventarioDAO = new InventarioDAOImpl();
    }

    @Override
    public void agregarProducto(Carrito carrito, int productoId, int cantidad) {
        Producto producto = validarProductoDisponible(productoId, cantidad);
        carrito.agregarProducto(new CarritoItem(
                producto.getId(), producto.getNombre(), producto.getPrecio(), cantidad, producto.getImagen()
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
