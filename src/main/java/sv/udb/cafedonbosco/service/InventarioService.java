package sv.udb.cafedonbosco.service;

import sv.udb.cafedonbosco.dto.request.InventarioAjusteRequestDTO;
import sv.udb.cafedonbosco.dto.response.ProductoAdminResponseDTO;

import java.util.List;

public interface InventarioService {

    List<ProductoAdminResponseDTO> listarInventario();

    void ajustar(InventarioAjusteRequestDTO datos);
}
