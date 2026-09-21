package sv.udb.cafedonbosco.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.udb.cafedonbosco.dao.InventarioDAO;
import sv.udb.cafedonbosco.dao.ProductoDAO;
import sv.udb.cafedonbosco.dto.response.CarritoResponseDTO;
import sv.udb.cafedonbosco.exception.RecursoNoEncontradoException;
import sv.udb.cafedonbosco.exception.ValidacionException;
import sv.udb.cafedonbosco.model.Carrito;
import sv.udb.cafedonbosco.model.CarritoItem;
import sv.udb.cafedonbosco.model.Inventario;
import sv.udb.cafedonbosco.model.Producto;
import sv.udb.cafedonbosco.service.CarritoService;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarritoServiceImplTest {

    @Mock
    private ProductoDAO productoDAO;
    @Mock
    private InventarioDAO inventarioDAO;

    private CarritoService carritoService;

    @BeforeEach
    void configurar() {
        carritoService = new CarritoServiceImpl(productoDAO, inventarioDAO);
    }

    private Producto productoActivo(int id, String nombre, String precio) {
        Producto producto = new Producto();
        producto.setId(id);
        producto.setNombre(nombre);
        producto.setPrecio(new BigDecimal(precio));
        producto.setActivo(true);
        return producto;
    }

    private Inventario inventarioConStock(int productoId, int cantidad) {
        return new Inventario(1, productoId, cantidad, 1);
    }

    @Test
    void agregarProductoConStockSuficienteFunciona() {
        when(productoDAO.buscarPorId(1)).thenReturn(productoActivo(1, "Cafe Latte", "2.50"));
        when(inventarioDAO.buscarPorProducto(1)).thenReturn(inventarioConStock(1, 10));

        Carrito carrito = new Carrito();
        carritoService.agregarProducto(carrito, 1, 3);

        assertEquals(3, carrito.getItems().get(1).getCantidad());
    }

    @Test
    void agregarMasUnidadesQueElStockDisponibleSeRechaza() {
        when(productoDAO.buscarPorId(1)).thenReturn(productoActivo(1, "Cafe Latte", "2.50"));
        when(inventarioDAO.buscarPorProducto(1)).thenReturn(inventarioConStock(1, 2));

        Carrito carrito = new Carrito();
        assertThrows(ValidacionException.class, () -> carritoService.agregarProducto(carrito, 1, 3));
    }

    @Test
    void agregarEnDosVecesQueSumeMasQueElStockSeRechazaEnLaSegunda() {
        // Regresion del bug donde solo se validaba la cantidad del ultimo
        // agregado, no la suma con lo que ya habia en el carrito: con
        // stock=3, agregar 2 y luego 2 mas (4 en total) debia rechazarse,
        // pero antes de la correccion cada llamada se validaba sola contra
        // el stock total y ambas pasaban.
        when(productoDAO.buscarPorId(1)).thenReturn(productoActivo(1, "Cafe Latte", "2.50"));
        when(inventarioDAO.buscarPorProducto(1)).thenReturn(inventarioConStock(1, 3));

        Carrito carrito = new Carrito();
        carritoService.agregarProducto(carrito, 1, 2);

        assertThrows(ValidacionException.class, () -> carritoService.agregarProducto(carrito, 1, 2));
        assertEquals(2, carrito.getItems().get(1).getCantidad(), "el segundo agregado rechazado no debio modificar el carrito");
    }

    @Test
    void agregarUnProductoInactivoSeRechaza() {
        Producto inactivo = productoActivo(1, "Descontinuado", "2.50");
        inactivo.setActivo(false);
        when(productoDAO.buscarPorId(1)).thenReturn(inactivo);

        Carrito carrito = new Carrito();
        assertThrows(RecursoNoEncontradoException.class, () -> carritoService.agregarProducto(carrito, 1, 1));
    }

    @Test
    void agregarUnProductoQueNoExisteSeRechaza() {
        when(productoDAO.buscarPorId(99)).thenReturn(null);

        Carrito carrito = new Carrito();
        assertThrows(RecursoNoEncontradoException.class, () -> carritoService.agregarProducto(carrito, 99, 1));
    }

    @Test
    void agregarCantidadCeroONegativaSeRechazaSinConsultarProductos() {
        Carrito carrito = new Carrito();
        assertThrows(ValidacionException.class, () -> carritoService.agregarProducto(carrito, 1, 0));
        assertThrows(ValidacionException.class, () -> carritoService.agregarProducto(carrito, 1, -1));
    }

    @Test
    void agregarMasDeVeinteUnidadesDeUnMismoProductoSeRechaza() {
        // El limite de 20 por producto se revisa antes de consultar el
        // producto o el inventario, asi que no hace falta stubear esos DAOs.
        Carrito carrito = new Carrito();
        assertThrows(ValidacionException.class, () -> carritoService.agregarProducto(carrito, 1, 21));
    }

    @Test
    void obtenerResumenQuitaDelCarritoUnProductoQueSeQuedoSinStock() {
        Producto producto = productoActivo(1, "Cafe Latte", "2.50");
        Carrito carrito = new Carrito();
        carrito.agregarProducto(new CarritoItem(1, "Cafe Latte", new BigDecimal("2.50"), 2, null));

        when(productoDAO.buscarPorId(1)).thenReturn(producto);
        when(inventarioDAO.buscarPorProducto(1)).thenReturn(inventarioConStock(1, 0));

        CarritoResponseDTO resumen = carritoService.obtenerResumen(carrito);

        assertTrue(resumen.getItems().isEmpty());
        assertTrue(carrito.estaVacio());
    }

    @Test
    void obtenerResumenActualizaElPrecioSiCambioEnLaBaseDeDatos() {
        Carrito carrito = new Carrito();
        carrito.agregarProducto(new CarritoItem(1, "Cafe Latte", new BigDecimal("2.50"), 2, null));

        Producto conPrecioNuevo = productoActivo(1, "Cafe Latte", "3.00");
        when(productoDAO.buscarPorId(1)).thenReturn(conPrecioNuevo);
        when(inventarioDAO.buscarPorProducto(1)).thenReturn(inventarioConStock(1, 10));

        CarritoResponseDTO resumen = carritoService.obtenerResumen(carrito);

        assertEquals(new BigDecimal("6.00"), resumen.getSubtotal());
    }

    @Test
    void obtenerResumenRecortaLaCantidadSiYaNoCabeEnElStock() {
        Carrito carrito = new Carrito();
        carrito.agregarProducto(new CarritoItem(1, "Cafe Latte", new BigDecimal("2.50"), 5, null));

        when(productoDAO.buscarPorId(1)).thenReturn(productoActivo(1, "Cafe Latte", "2.50"));
        when(inventarioDAO.buscarPorProducto(1)).thenReturn(inventarioConStock(1, 2));

        carritoService.obtenerResumen(carrito);

        assertEquals(2, carrito.getItems().get(1).getCantidad());
    }
}
