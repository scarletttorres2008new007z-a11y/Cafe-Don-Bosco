package sv.udb.cafedonbosco.controller.vista;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sv.udb.cafedonbosco.exception.RecursoNoEncontradoException;
import sv.udb.cafedonbosco.service.ProductoService;
import sv.udb.cafedonbosco.service.impl.ProductoServiceImpl;
import sv.udb.cafedonbosco.util.SessionUtil;

import java.io.IOException;

/**
 * Pantalla de detalle de un producto especifico (GET /producto?id=..).
 * Protegida por SesionVistaFilter igual que la pantalla principal.
 */
@WebServlet(name = "ProductoDetalleViewServlet", urlPatterns = "/producto")
public class ProductoDetalleViewServlet extends HttpServlet {

    private static final String VISTA = "/WEB-INF/views/detalle-producto.jsp";

    private final ProductoService productoService = new ProductoServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("usuario", SessionUtil.obtenerUsuarioAutenticado(request));

        String idParametro = request.getParameter("id");
        try {
            int id = Integer.parseInt(idParametro);
            request.setAttribute("producto", productoService.obtenerDetalle(id));
        } catch (NumberFormatException e) {
            request.setAttribute("error", "El producto solicitado no es valido.");
        } catch (RecursoNoEncontradoException e) {
            request.setAttribute("error", e.getMessage());
        }

        RequestDispatcher dispatcher = request.getRequestDispatcher(VISTA);
        dispatcher.forward(request, response);
    }
}
