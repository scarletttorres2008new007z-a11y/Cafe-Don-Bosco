package sv.udb.cafedonbosco.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sv.udb.cafedonbosco.exception.ValidacionException;
import sv.udb.cafedonbosco.service.TicketService;
import sv.udb.cafedonbosco.service.impl.TicketServiceImpl;
import sv.udb.cafedonbosco.util.JsonUtil;

import java.io.IOException;

/**
 * GET /api/tickets/{token} es publico pero exige el token aleatorio de la
 * venta (nunca el id incremental), para que nadie pueda ver el ticket de
 * otro comprador cambiando un numero en la URL. GET /api/admin/tickets/{id}
 * esta protegido por RolAdminFilter y si acepta el id, porque el
 * administrador ya esta autenticado.
 */
@WebServlet(name = "TicketServlet", urlPatterns = {"/api/tickets/*", "/api/admin/tickets/*"})
public class TicketServlet extends BaseServlet {

    private final TicketService ticketService = new TicketServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.length() < 2) {
                throw new ValidacionException("Debes indicar el ticket a consultar en la URL.");
            }
            String valor = pathInfo.substring(1);
            boolean esAdmin = request.getServletPath().startsWith("/api/admin");

            var ticket = esAdmin
                    ? ticketService.obtenerPorIdAdmin(Integer.parseInt(valor))
                    : ticketService.obtenerPorToken(valor);

            JsonUtil.exito(response, HttpServletResponse.SC_OK, "Ticket obtenido", ticket);
        } catch (NumberFormatException e) {
            JsonUtil.error(response, HttpServletResponse.SC_BAD_REQUEST, "El id de la venta no es valido.");
        } catch (Exception e) {
            manejarError(response, e);
        }
    }
}
