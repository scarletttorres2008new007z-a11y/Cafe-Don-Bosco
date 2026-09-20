package sv.udb.cafedonbosco.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import sv.udb.cafedonbosco.dto.response.UsuarioResponseDTO;
import sv.udb.cafedonbosco.model.Carrito;
import sv.udb.cafedonbosco.model.Rol;

public final class SessionUtil {

    private SessionUtil() {
    }

    public static UsuarioResponseDTO obtenerUsuarioAutenticado(HttpServletRequest request) {
        HttpSession sesion = request.getSession(false);
        if (sesion == null) {
            return null;
        }
        return (UsuarioResponseDTO) sesion.getAttribute(Constantes.SESSION_USUARIO);
    }

    public static boolean tieneRol(HttpServletRequest request, Rol rol) {
        UsuarioResponseDTO usuario = obtenerUsuarioAutenticado(request);
        return usuario != null && usuario.getRol() == rol;
    }

    public static Carrito obtenerOCrearCarrito(HttpServletRequest request) {
        HttpSession sesion = request.getSession(true);
        Carrito carrito = (Carrito) sesion.getAttribute(Constantes.SESSION_CARRITO);
        if (carrito == null) {
            carrito = new Carrito();
            sesion.setAttribute(Constantes.SESSION_CARRITO, carrito);
        }
        return carrito;
    }
}
