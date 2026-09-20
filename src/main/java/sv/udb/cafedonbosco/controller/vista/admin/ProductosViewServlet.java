package sv.udb.cafedonbosco.controller.vista.admin;

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
 * Listado administrativo de productos: muestra stock exacto y estado
 * real (activo/inactivo), a diferencia del catalogo publico de la
 * tienda. Es de solo lectura en esta version; crear/editar productos
 * queda pendiente para una siguiente fase.
 */
@WebServlet(name = "AdminProductosViewServlet", urlPatterns = "/admin/productos")
public class ProductosViewServlet extends HttpServlet {

    private static final String VISTA = "/WEB-INF/views/admin/productos.jsp";

    private final ProductoService productoService = new ProductoServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("activo", "productos");
        request.setAttribute("usuario", SessionUtil.obtenerUsuarioAutenticado(request));
        request.setAttribute("productos", productoService.listarAdmin());

        RequestDispatcher dispatcher = request.getRequestDispatcher(VISTA);
        dispatcher.forward(request, response);
    }
}
