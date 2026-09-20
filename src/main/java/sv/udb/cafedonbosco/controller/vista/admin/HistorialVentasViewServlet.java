package sv.udb.cafedonbosco.controller.vista.admin;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sv.udb.cafedonbosco.service.VentaService;
import sv.udb.cafedonbosco.service.impl.VentaServiceImpl;
import sv.udb.cafedonbosco.util.SessionUtil;

import java.io.IOException;

@WebServlet(name = "AdminHistorialVentasViewServlet", urlPatterns = "/admin/historial-ventas")
public class HistorialVentasViewServlet extends HttpServlet {

    private static final String VISTA = "/WEB-INF/views/admin/historial-ventas.jsp";

    private final VentaService ventaService = new VentaServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("activo", "historial");
        request.setAttribute("usuario", SessionUtil.obtenerUsuarioAutenticado(request));
        request.setAttribute("ventas", ventaService.listarHistorial(null, 100));

        RequestDispatcher dispatcher = request.getRequestDispatcher(VISTA);
        dispatcher.forward(request, response);
    }
}
