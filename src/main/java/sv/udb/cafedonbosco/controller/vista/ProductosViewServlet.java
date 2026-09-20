package sv.udb.cafedonbosco.controller.vista;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sv.udb.cafedonbosco.service.ProductoService;
import sv.udb.cafedonbosco.service.impl.ProductoServiceImpl;
import sv.udb.cafedonbosco.util.SessionUtil;

import java.io.IOException;

/**
 * Pantalla principal del mostrador: lista los productos activos del
 * catalogo. Protegida por SesionVistaFilter, ya que el cliente pidio un
 * sistema con inicio de sesion antes de poder usarlo.
 */
@WebServlet(name = "ProductosViewServlet", urlPatterns = "/productos")
public class ProductosViewServlet extends HttpServlet {

    private static final String VISTA = "/WEB-INF/views/productos.jsp";

    private final ProductoService productoService = new ProductoServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("usuario", SessionUtil.obtenerUsuarioAutenticado(request));
        request.setAttribute("productos", productoService.listarCatalogo(null, null, "nombre"));

        RequestDispatcher dispatcher = request.getRequestDispatcher(VISTA);
        dispatcher.forward(request, response);
    }
}
