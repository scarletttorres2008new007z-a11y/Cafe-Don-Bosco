package sv.udb.cafedonbosco.service.impl;

import sv.udb.cafedonbosco.dao.InventarioDAO;
import sv.udb.cafedonbosco.dao.impl.InventarioDAOImpl;
import sv.udb.cafedonbosco.dto.request.InventarioAjusteRequestDTO;
import sv.udb.cafedonbosco.dto.response.ProductoAdminResponseDTO;
import sv.udb.cafedonbosco.exception.RecursoNoEncontradoException;
import sv.udb.cafedonbosco.exception.ValidacionException;
import sv.udb.cafedonbosco.model.Inventario;
import sv.udb.cafedonbosco.service.InventarioService;
import sv.udb.cafedonbosco.service.ProductoService;

import java.util.List;

public class InventarioServiceImpl implements InventarioService {

    private final InventarioDAO inventarioDAO;
    private final ProductoService productoService;

    public InventarioServiceImpl() {
        this.inventarioDAO = new InventarioDAOImpl();
        this.productoService = new ProductoServiceImpl();
    }

    @Override
    public List<ProductoAdminResponseDTO> listarInventario() {
        return productoService.listarAdmin();
    }

    @Override
    public void ajustar(InventarioAjusteRequestDTO datos) {
        if (datos == null || datos.getProductoId() == null || datos.getCantidad() == null || datos.getCantidad() < 0) {
            throw new ValidacionException("Debes indicar el producto y una cantidad valida (0 o mas).");
        }
        Inventario inventario = inventarioDAO.buscarPorProducto(datos.getProductoId());
        if (inventario == null) {
            throw new RecursoNoEncontradoException("El producto no tiene un registro de inventario.");
        }
        inventarioDAO.fijarCantidad(datos.getProductoId(), datos.getCantidad());
        if (datos.getStockMinimo() != null && datos.getStockMinimo() >= 0) {
            inventarioDAO.actualizarStockMinimo(datos.getProductoId(), datos.getStockMinimo());
        }
    }
}
