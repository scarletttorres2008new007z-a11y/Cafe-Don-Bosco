package sv.udb.cafedonbosco.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sv.udb.cafedonbosco.dto.request.CarritoItemRequestDTO;
import sv.udb.cafedonbosco.exception.ValidacionException;
import sv.udb.cafedonbosco.model.Carrito;
import sv.udb.cafedonbosco.service.CarritoService;
import sv.udb.cafedonbosco.service.impl.CarritoServiceImpl;
import sv.udb.cafedonbosco.util.JsonUtil;
import sv.udb.cafedonbosco.util.SessionUtil;

import java.io.IOException;

/**
 * El carrito vive en la sesion HTTP del consumidor (invitado o
 * autenticado): no requiere login para comprar, como se definio en el
 * flujo de la tienda.
 */
@WebServlet(name = "CarritoServlet", urlPatterns = {"/api/carrito", "/api/carrito/items", "/api/carrito/items/*"})
public class CarritoServlet extends BaseServlet {

    private final CarritoService carritoService = new CarritoServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Carrito carrito = SessionUtil.obtenerOCrearCarrito(request);
            JsonUtil.exito(response, HttpServletResponse.SC_OK, "Carrito obtenido", carritoService.obtenerResumen(carrito));
        } catch (Exception e) {
            manejarError(response, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            CarritoItemRequestDTO datos = JsonUtil.leerCuerpo(request, CarritoItemRequestDTO.class);
            if (datos == null || datos.getProductoId() == null || datos.getCantidad() == null) {
                throw new ValidacionException("Debes indicar el producto y la cantidad.");
            }
            Carrito carrito = SessionUtil.obtenerOCrearCarrito(request);
            carritoService.agregarProducto(carrito, datos.getProductoId(), datos.getCantidad());
            JsonUtil.exito(response, HttpServletResponse.SC_OK, "Producto agregado al carrito", carritoService.obtenerResumen(carrito));
        } catch (Exception e) {
            manejarError(response, e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int productoId = extraerProductoId(request);
            CarritoItemRequestDTO datos = JsonUtil.leerCuerpo(request, CarritoItemRequestDTO.class);
            if (datos == null || datos.getCantidad() == null) {
                throw new ValidacionException("Debes indicar la nueva cantidad.");
            }
            Carrito carrito = SessionUtil.obtenerOCrearCarrito(request);
            carritoService.actualizarCantidad(carrito, productoId, datos.getCantidad());
            JsonUtil.exito(response, HttpServletResponse.SC_OK, "Cantidad actualizada", carritoService.obtenerResumen(carrito));
        } catch (Exception e) {
            manejarError(response, e);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Carrito carrito = SessionUtil.obtenerOCrearCarrito(request);
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                carritoService.vaciar(carrito);
                JsonUtil.exito(response, HttpServletResponse.SC_OK, "Carrito vaciado", carritoService.obtenerResumen(carrito));
                return;
            }
            int productoId = extraerProductoId(request);
            carritoService.eliminarProducto(carrito, productoId);
            JsonUtil.exito(response, HttpServletResponse.SC_OK, "Producto eliminado del carrito", carritoService.obtenerResumen(carrito));
        } catch (Exception e) {
            manejarError(response, e);
        }
    }

    private int extraerProductoId(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.length() < 2) {
            throw new ValidacionException("Debes indicar el id del producto en la URL.");
        }
        try {
            return Integer.parseInt(pathInfo.substring(1));
        } catch (NumberFormatException e) {
            throw new ValidacionException("El id del producto no es valido.");
        }
    }
}
