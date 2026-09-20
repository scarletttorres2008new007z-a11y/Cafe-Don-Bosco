package sv.udb.cafedonbosco.controller.vista;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import sv.udb.cafedonbosco.dto.response.UsuarioResponseDTO;
import sv.udb.cafedonbosco.exception.AppException;
import sv.udb.cafedonbosco.service.AuthService;
import sv.udb.cafedonbosco.service.impl.AuthServiceImpl;
import sv.udb.cafedonbosco.util.Constantes;
import sv.udb.cafedonbosco.util.SessionUtil;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Pantalla de inicio de sesion del mostrador. GET muestra el formulario;
 * POST valida las credenciales con el mismo AuthService que usa la API
 * JSON y, si son correctas, guarda al usuario en la sesion y redirige a
 * la pantalla principal de productos.
 */
@WebServlet(name = "LoginViewServlet", urlPatterns = "/login")
public class LoginViewServlet extends HttpServlet {

    private static final String VISTA = "/WEB-INF/views/login.jsp";
    private final Logger logger = Logger.getLogger(getClass().getName());
    private final AuthService authService = new AuthServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (SessionUtil.obtenerUsuarioAutenticado(request) != null) {
            response.sendRedirect(request.getContextPath() + "/productos");
            return;
        }
        mostrarFormulario(request, response, null);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String correo = request.getParameter("correo");
        String password = request.getParameter("password");

        try {
            UsuarioResponseDTO usuario = authService.login(correo, password, null);
            HttpSession sesion = request.getSession(true);
            sesion.setAttribute(Constantes.SESSION_USUARIO, usuario);
            response.sendRedirect(request.getContextPath() + "/productos");
        } catch (AppException e) {
            logger.log(Level.WARNING, "Login rechazado: " + e.getMessage(), e.getCause());
            mostrarFormulario(request, response, e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error inesperado al iniciar sesion", e);
            mostrarFormulario(request, response, "Ocurrio un error inesperado. Intenta de nuevo.");
        }
    }

    private void mostrarFormulario(HttpServletRequest request, HttpServletResponse response, String error)
            throws ServletException, IOException {
        request.setAttribute("error", error);
        RequestDispatcher dispatcher = request.getRequestDispatcher(VISTA);
        dispatcher.forward(request, response);
    }
}
