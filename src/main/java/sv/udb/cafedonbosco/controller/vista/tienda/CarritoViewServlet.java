package sv.udb.cafedonbosco.controller.vista.tienda;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sv.udb.cafedonbosco.exception.AppException;
import sv.udb.cafedonbosco.model.Carrito;
import sv.udb.cafedonbosco.model.CarritoItem;
import sv.udb.cafedonbosco.service.CarritoService;
import sv.udb.cafedonbosco.service.impl.CarritoServiceImpl;
import sv.udb.cafedonbosco.util.SessionUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Carrito de compras del consumidor. No requiere sesion iniciada: vive
 * en la sesion HTTP de todos modos (invitado), tal como se definio para
 * que el consumidor pueda comprar sin crear cuenta.
 */
@WebServlet(name = "TiendaCarritoViewServlet", urlPatterns = "/tienda/carrito")
public class CarritoViewServlet extends TiendaBaseServlet {

    private static final String VISTA = "/WEB-INF/views/tienda/carrito.jsp";

    private final CarritoService carritoService = new CarritoServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        mostrarCarrito(request, response, null);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Carrito carrito = SessionUtil.obtenerOCrearCarrito(request);
        String accion = request.getParameter("accion");

        try {
            switch (accion == null ? "" : accion) {
                case "agregar" -> {
                    int productoId = Integer.parseInt(request.getParameter("productoId"));
                    int cantidad = parametroCantidad(request, 1);
                    carritoService.agregarProducto(carrito, productoId, cantidad, parametroOpcionIds(request));
                }
                case "decrementar" -> {
                    int productoId = Integer.parseInt(request.getParameter("productoId"));
                    CarritoItem actual = carrito.getItems().get(productoId);
                    if (actual != null && actual.getCantidad() > 1) {
                        carritoService.actualizarCantidad(carrito, productoId, actual.getCantidad() - 1);
                    } else {
                        carritoService.eliminarProducto(carrito, productoId);
                    }
                }
                case "eliminar" -> {
                    int productoId = Integer.parseInt(request.getParameter("productoId"));
                    carritoService.eliminarProducto(carrito, productoId);
                }
                case "vaciar" -> carritoService.vaciar(carrito);
                default -> {
                    // Sin accion reconocida: no se hace nada especial.
                }
            }

            response.sendRedirect(destinoSeguro(request));
        } catch (AppException e) {
            mostrarCarrito(request, response, e.getMessage());
        }
    }

    /**
     * "volver" indica a donde regresar tras agregar un producto desde el
     * inicio, el menu o el detalle. Viene del cliente, asi que solo se
     * acepta si es una ruta relativa dentro de esta misma aplicacion;
     * cualquier otro valor (una URL absoluta a otro sitio, por ejemplo)
     * se ignora para evitar un open redirect.
     */
    private String destinoSeguro(HttpServletRequest request) {
        String volver = request.getParameter("volver");
        String contexto = request.getContextPath();
        if (volver != null && volver.startsWith(contexto + "/") && !volver.contains("://")
                && !volver.startsWith(contexto + "/WEB-INF/")) {
            return volver;
        }
        return contexto + "/tienda/carrito";
    }

    /** El form clasico envia un "opcionId" por cada pill/checkbox marcado; puede no venir ninguno. */
    private List<Integer> parametroOpcionIds(HttpServletRequest request) {
        String[] valores = request.getParameterValues("opcionId");
        if (valores == null || valores.length == 0) {
            return null;
        }
        List<Integer> ids = new ArrayList<>();
        for (String valor : valores) {
            try {
                ids.add(Integer.parseInt(valor));
            } catch (NumberFormatException e) {
                // Un valor no numerico no deberia llegar desde el formulario propio; se ignora.
            }
        }
        return ids;
    }

    private int parametroCantidad(HttpServletRequest request, int porDefecto) {
        String valor = request.getParameter("cantidad");
        if (valor == null || valor.isBlank()) {
            return porDefecto;
        }
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException e) {
            return porDefecto;
        }
    }

    private void mostrarCarrito(HttpServletRequest request, HttpServletResponse response, String error)
            throws ServletException, IOException {
        request.setAttribute("activo", "carrito");
        request.setAttribute("error", error);

        Carrito carrito = SessionUtil.obtenerOCrearCarrito(request);
        request.setAttribute("carritoUnidades", carrito.contarUnidades());
        request.setAttribute("resumen", carritoService.obtenerResumen(carrito));

        RequestDispatcher dispatcher = request.getRequestDispatcher(VISTA);
        dispatcher.forward(request, response);
    }
}
