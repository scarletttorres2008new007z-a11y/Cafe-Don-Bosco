package sv.udb.cafedonbosco.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sv.udb.cafedonbosco.dto.request.VentaPresencialRequestDTO;
import sv.udb.cafedonbosco.dto.response.UsuarioResponseDTO;
import sv.udb.cafedonbosco.dto.response.VentaResponseDTO;
import sv.udb.cafedonbosco.exception.AccesoDenegadoException;
import sv.udb.cafedonbosco.exception.ValidacionException;
import sv.udb.cafedonbosco.model.TipoVenta;
import sv.udb.cafedonbosco.service.VentaService;
import sv.udb.cafedonbosco.service.impl.VentaServiceImpl;
import sv.udb.cafedonbosco.util.JsonUtil;
import sv.udb.cafedonbosco.util.SessionUtil;

import java.io.IOException;

/**
 * Ventas del mostrador (POS) y el historial global que ve el
 * administrador. Protegido por RolAdminFilter (/api/admin/*).
 */
@WebServlet(name = "VentaAdminServlet", urlPatterns = "/api/admin/ventas")
public class VentaAdminServlet extends BaseServlet {

    private final VentaService ventaService = new VentaServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String tipoParametro = request.getParameter("tipo");
            TipoVenta tipoVenta = null;
            if (tipoParametro != null && !tipoParametro.isBlank()) {
                try {
                    tipoVenta = TipoVenta.valueOf(tipoParametro.toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw new ValidacionException("El tipo de venta debe ser PRESENCIAL o WEB.");
                }
            }
            int limite = parametroLimite(request);
            JsonUtil.exito(response, HttpServletResponse.SC_OK, "Historial obtenido",
                    ventaService.listarHistorial(tipoVenta, limite));
        } catch (Exception e) {
            manejarError(response, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            UsuarioResponseDTO administrador = SessionUtil.obtenerUsuarioAutenticado(request);
            if (administrador == null) {
                throw new AccesoDenegadoException("Debes iniciar sesion como administrador.");
            }
            VentaPresencialRequestDTO datos = JsonUtil.leerCuerpo(request, VentaPresencialRequestDTO.class);
            VentaResponseDTO venta = ventaService.registrarVentaPresencial(datos, administrador.getId());
            JsonUtil.exito(response, HttpServletResponse.SC_CREATED, "Venta registrada correctamente", venta);
        } catch (Exception e) {
            manejarError(response, e);
        }
    }

    private int parametroLimite(HttpServletRequest request) {
        String valor = request.getParameter("limite");
        if (valor == null || valor.isBlank()) {
            return 50;
        }
        try {
            return Math.max(1, Integer.parseInt(valor));
        } catch (NumberFormatException e) {
            return 50;
        }
    }
}
