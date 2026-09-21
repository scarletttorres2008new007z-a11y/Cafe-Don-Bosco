package sv.udb.cafedonbosco.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sv.udb.cafedonbosco.dto.response.VentaResponseDTO;
import sv.udb.cafedonbosco.exception.ValidacionException;
import sv.udb.cafedonbosco.service.TicketService;
import sv.udb.cafedonbosco.service.impl.TicketServiceImpl;
import sv.udb.cafedonbosco.util.JsonUtil;
import sv.udb.cafedonbosco.util.TicketPdfGenerator;

import java.io.IOException;

/**
 * GET /api/tickets/{token}[/pdf] es publico pero exige el token aleatorio
 * de la venta (nunca el id incremental), para que nadie pueda ver el
 * ticket de otro comprador cambiando un numero en la URL.
 * GET /api/admin/tickets/{id}[/pdf] esta protegido por RolAdminFilter y
 * si acepta el id, porque el administrador ya esta autenticado.
 * El sufijo /pdf devuelve el mismo ticket como PDF real (Apache PDFBox)
 * en vez de JSON.
 */
@WebServlet(name = "TicketServlet", urlPatterns = {"/api/tickets/*", "/api/admin/tickets/*"})
public class TicketServlet extends BaseServlet {

    private final TicketService ticketService = new TicketServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String[] segmentos = segmentosDePath(request);
            if (segmentos.length == 0) {
                throw new ValidacionException("Debes indicar el ticket a consultar en la URL.");
            }
            String valor = segmentos[0];
            boolean comoPdf = segmentos.length == 2 && "pdf".equals(segmentos[1]);
            boolean esAdmin = request.getServletPath().startsWith("/api/admin");

            VentaResponseDTO ticket = esAdmin
                    ? ticketService.obtenerPorIdAdmin(Integer.parseInt(valor))
                    : ticketService.obtenerPorToken(valor);

            if (comoPdf) {
                enviarPdf(response, ticket);
                return;
            }
            JsonUtil.exito(response, HttpServletResponse.SC_OK, "Ticket obtenido", ticket);
        } catch (NumberFormatException e) {
            JsonUtil.error(response, HttpServletResponse.SC_BAD_REQUEST, "El id de la venta no es valido.");
        } catch (Exception e) {
            manejarError(response, e);
        }
    }

    private void enviarPdf(HttpServletResponse response, VentaResponseDTO ticket) throws IOException {
        byte[] pdf = TicketPdfGenerator.generar(ticket);
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=\"ticket-" + ticket.getId() + ".pdf\"");
        response.setContentLength(pdf.length);
        response.getOutputStream().write(pdf);
        response.getOutputStream().flush();
    }

    private String[] segmentosDePath(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            return new String[0];
        }
        String limpio = pathInfo.startsWith("/") ? pathInfo.substring(1) : pathInfo;
        return limpio.split("/");
    }
}
