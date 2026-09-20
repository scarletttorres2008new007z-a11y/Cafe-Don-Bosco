package sv.udb.cafedonbosco.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sv.udb.cafedonbosco.util.SessionUtil;

import java.io.IOException;

/**
 * Protege las pantallas JSP del mostrador (productos y detalle): si no
 * hay una sesion iniciada, redirige a la pantalla de login en vez de
 * dejar pasar la solicitud. Es el equivalente, para vistas JSP, de lo
 * que RolAdminFilter hace para la API bajo /api/admin/*.
 */
@WebFilter({"/productos", "/producto"})
public class SesionVistaFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if (SessionUtil.obtenerUsuarioAutenticado(httpRequest) == null) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
