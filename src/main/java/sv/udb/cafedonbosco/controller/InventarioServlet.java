package sv.udb.cafedonbosco.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sv.udb.cafedonbosco.dto.request.InventarioAjusteRequestDTO;
import sv.udb.cafedonbosco.dto.response.UsuarioResponseDTO;
import sv.udb.cafedonbosco.exception.AccesoDenegadoException;
import sv.udb.cafedonbosco.service.InventarioService;
import sv.udb.cafedonbosco.service.impl.InventarioServiceImpl;
import sv.udb.cafedonbosco.util.JsonUtil;
import sv.udb.cafedonbosco.util.SessionUtil;

import java.io.IOException;

@WebServlet(name = "InventarioServlet", urlPatterns = "/api/admin/inventario")
public class InventarioServlet extends BaseServlet {

    private final InventarioService inventarioService = new InventarioServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            JsonUtil.exito(response, HttpServletResponse.SC_OK, "Inventario obtenido", inventarioService.listarInventario());
        } catch (Exception e) {
            manejarError(response, e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            UsuarioResponseDTO administrador = SessionUtil.obtenerUsuarioAutenticado(request);
            if (administrador == null) {
                throw new AccesoDenegadoException("Debes iniciar sesion como administrador.");
            }
            InventarioAjusteRequestDTO datos = JsonUtil.leerCuerpo(request, InventarioAjusteRequestDTO.class);
            inventarioService.ajustar(datos, administrador.getId());
            JsonUtil.exito(response, HttpServletResponse.SC_OK, "Inventario actualizado correctamente",
                    inventarioService.listarInventario());
        } catch (Exception e) {
            manejarError(response, e);
        }
    }
}
