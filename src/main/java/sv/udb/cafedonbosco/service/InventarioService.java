package sv.udb.cafedonbosco.service;

import sv.udb.cafedonbosco.dto.request.InventarioAjusteRequestDTO;
import sv.udb.cafedonbosco.dto.response.ProductoAdminResponseDTO;

import java.util.List;

public interface InventarioService {

    List<ProductoAdminResponseDTO> listarInventario();

    /** Ajuste manual de stock/stock minimo; queda registrado en el movimiento de inventario y en la bitacora. */
    void ajustar(InventarioAjusteRequestDTO datos, int usuarioAdminId);
}
