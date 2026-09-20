package sv.udb.cafedonbosco.service;

import sv.udb.cafedonbosco.dto.request.ProductoRequestDTO;
import sv.udb.cafedonbosco.dto.response.ProductoAdminResponseDTO;
import sv.udb.cafedonbosco.dto.response.ProductoResponseDTO;

import java.util.List;

public interface ProductoService {

    List<ProductoResponseDTO> listarCatalogo(Integer categoriaId, String busqueda, String orden);

    ProductoResponseDTO obtenerDetalle(int id);

    List<ProductoResponseDTO> listarRelacionados(int productoId, int limite);

    List<ProductoAdminResponseDTO> listarAdmin();

    ProductoAdminResponseDTO obtenerDetalleAdmin(int id);

    ProductoAdminResponseDTO crear(ProductoRequestDTO datos);

    ProductoAdminResponseDTO actualizar(int id, ProductoRequestDTO datos);

    void cambiarEstado(int id, boolean activo);
}
