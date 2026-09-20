package sv.udb.cafedonbosco.service.impl;

import sv.udb.cafedonbosco.dao.CategoriaDAO;
import sv.udb.cafedonbosco.dao.InventarioDAO;
import sv.udb.cafedonbosco.dao.ProductoDAO;
import sv.udb.cafedonbosco.dao.impl.CategoriaDAOImpl;
import sv.udb.cafedonbosco.dao.impl.InventarioDAOImpl;
import sv.udb.cafedonbosco.dao.impl.ProductoDAOImpl;
import sv.udb.cafedonbosco.dto.request.ProductoRequestDTO;
import sv.udb.cafedonbosco.dto.response.ProductoAdminResponseDTO;
import sv.udb.cafedonbosco.dto.response.ProductoResponseDTO;
import sv.udb.cafedonbosco.exception.ErrorInternoException;
import sv.udb.cafedonbosco.exception.RecursoNoEncontradoException;
import sv.udb.cafedonbosco.exception.ValidacionException;
import sv.udb.cafedonbosco.model.Categoria;
import sv.udb.cafedonbosco.model.Inventario;
import sv.udb.cafedonbosco.model.Producto;
import sv.udb.cafedonbosco.service.ProductoService;
import sv.udb.cafedonbosco.util.ConexionBD;
import sv.udb.cafedonbosco.util.Constantes;
import sv.udb.cafedonbosco.util.ValidacionUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductoServiceImpl implements ProductoService {

    private final ProductoDAO productoDAO;
    private final CategoriaDAO categoriaDAO;
    private final InventarioDAO inventarioDAO;

    public ProductoServiceImpl() {
        this.productoDAO = new ProductoDAOImpl();
        this.categoriaDAO = new CategoriaDAOImpl();
        this.inventarioDAO = new InventarioDAOImpl();
    }

    @Override
    public List<ProductoResponseDTO> listarCatalogo(Integer categoriaId, String busqueda, String orden) {
        List<Producto> productos;
        if (ValidacionUtil.esTextoValido(busqueda)) {
            productos = productoDAO.buscarActivosPorNombre(busqueda.trim());
        } else if (categoriaId != null) {
            productos = productoDAO.listarActivosPorCategoria(categoriaId);
        } else {
            productos = productoDAO.listarActivos();
        }

        Map<Integer, Categoria> categorias = indexarCategorias();
        Map<Integer, Inventario> inventarios = indexarInventarios();

        List<ProductoResponseDTO> catalogo = new ArrayList<>();
        for (Producto producto : productos) {
            catalogo.add(aResponseDTO(producto, categorias, inventarios));
        }

        ordenar(catalogo, orden);
        return catalogo;
    }

    private void ordenar(List<ProductoResponseDTO> catalogo, String orden) {
        if (orden == null) {
            return;
        }
        switch (orden) {
            case "precio_menor":
                catalogo.sort(Comparator.comparing(ProductoResponseDTO::getPrecio));
                break;
            case "precio_mayor":
                catalogo.sort(Comparator.comparing(ProductoResponseDTO::getPrecio).reversed());
                break;
            case "nombre":
                catalogo.sort(Comparator.comparing(ProductoResponseDTO::getNombre));
                break;
            default:
                // "popularidad" u otro valor: se mantiene el orden por nombre que
                // ya entrega el DAO hasta que exista un ranking real de ventas.
        }
    }

    @Override
    public ProductoResponseDTO obtenerDetalle(int id) {
        Producto producto = productoDAO.buscarPorId(id);
        if (producto == null || !Boolean.TRUE.equals(producto.getActivo())) {
            throw new RecursoNoEncontradoException("El producto solicitado no existe.");
        }
        return aResponseDTO(producto, indexarCategorias(), indexarInventarios());
    }

    @Override
    public List<ProductoResponseDTO> listarRelacionados(int productoId, int limite) {
        Producto producto = productoDAO.buscarPorId(productoId);
        if (producto == null) {
            return List.of();
        }
        Map<Integer, Categoria> categorias = indexarCategorias();
        Map<Integer, Inventario> inventarios = indexarInventarios();
        List<ProductoResponseDTO> relacionados = new ArrayList<>();
        for (Producto candidato : productoDAO.listarActivosPorCategoria(producto.getCategoriaId())) {
            if (candidato.getId().equals(productoId)) {
                continue;
            }
            relacionados.add(aResponseDTO(candidato, categorias, inventarios));
            if (relacionados.size() >= limite) {
                break;
            }
        }
        return relacionados;
    }

    @Override
    public List<ProductoAdminResponseDTO> listarAdmin() {
        Map<Integer, Categoria> categorias = indexarCategorias();
        Map<Integer, Inventario> inventarios = indexarInventarios();
        List<ProductoAdminResponseDTO> resultado = new ArrayList<>();
        for (Producto producto : productoDAO.listarTodos()) {
            resultado.add(aAdminDTO(producto, categorias, inventarios));
        }
        return resultado;
    }

    @Override
    public ProductoAdminResponseDTO obtenerDetalleAdmin(int id) {
        Producto producto = productoDAO.buscarPorId(id);
        if (producto == null) {
            throw new RecursoNoEncontradoException("El producto solicitado no existe.");
        }
        return aAdminDTO(producto, indexarCategorias(), indexarInventarios());
    }

    @Override
    public ProductoAdminResponseDTO crear(ProductoRequestDTO datos) {
        validar(datos);
        if (categoriaDAO.buscarPorId(datos.getCategoriaId()) == null) {
            throw new ValidacionException("La categoria indicada no existe.");
        }

        Producto producto = new Producto();
        producto.setCategoriaId(datos.getCategoriaId());
        producto.setNombre(datos.getNombre().trim());
        producto.setDescripcion(datos.getDescripcion());
        producto.setPrecio(datos.getPrecio());
        producto.setImagen(datos.getImagen());
        producto.setTiempoPreparacion(datos.getTiempoPreparacion());
        producto.setActivo(datos.getActivo() == null || datos.getActivo());

        int stockInicial = datos.getStockInicial() == null ? 0 : datos.getStockInicial();
        int stockMinimo = datos.getStockMinimo() == null ? Constantes.STOCK_MINIMO_POR_DEFECTO : datos.getStockMinimo();

        Connection conexion = null;
        try {
            conexion = ConexionBD.obtenerConexion();
            conexion.setAutoCommit(false);

            productoDAO.crear(conexion, producto);

            Inventario inventario = new Inventario(null, producto.getId(), stockInicial, stockMinimo);
            inventarioDAO.crear(conexion, inventario);

            conexion.commit();
        } catch (SQLException e) {
            revertir(conexion);
            throw new ErrorInternoException("Error al crear el producto con su inventario inicial", e);
        } finally {
            cerrar(conexion);
        }

        return aAdminDTO(producto, indexarCategorias(), indexarInventarios());
    }

    @Override
    public ProductoAdminResponseDTO actualizar(int id, ProductoRequestDTO datos) {
        validar(datos);
        Producto existente = productoDAO.buscarPorId(id);
        if (existente == null) {
            throw new RecursoNoEncontradoException("El producto solicitado no existe.");
        }
        if (categoriaDAO.buscarPorId(datos.getCategoriaId()) == null) {
            throw new ValidacionException("La categoria indicada no existe.");
        }

        existente.setCategoriaId(datos.getCategoriaId());
        existente.setNombre(datos.getNombre().trim());
        existente.setDescripcion(datos.getDescripcion());
        existente.setPrecio(datos.getPrecio());
        existente.setImagen(datos.getImagen());
        existente.setTiempoPreparacion(datos.getTiempoPreparacion());
        existente.setActivo(datos.getActivo() == null ? existente.getActivo() : datos.getActivo());
        productoDAO.actualizar(existente);

        if (datos.getStockMinimo() != null) {
            inventarioDAO.actualizarStockMinimo(id, datos.getStockMinimo());
        }

        return aAdminDTO(existente, indexarCategorias(), indexarInventarios());
    }

    @Override
    public void cambiarEstado(int id, boolean activo) {
        if (productoDAO.buscarPorId(id) == null) {
            throw new RecursoNoEncontradoException("El producto solicitado no existe.");
        }
        productoDAO.cambiarEstado(id, activo);
    }

    private void validar(ProductoRequestDTO datos) {
        if (datos == null
                || datos.getCategoriaId() == null
                || !ValidacionUtil.esTextoValido(datos.getNombre(), 120)
                || datos.getPrecio() == null
                || datos.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidacionException("Nombre, categoria y precio (mayor a cero) son obligatorios.");
        }
    }

    private Map<Integer, Categoria> indexarCategorias() {
        Map<Integer, Categoria> mapa = new HashMap<>();
        for (Categoria categoria : categoriaDAO.listarTodas()) {
            mapa.put(categoria.getId(), categoria);
        }
        return mapa;
    }

    private Map<Integer, Inventario> indexarInventarios() {
        Map<Integer, Inventario> mapa = new HashMap<>();
        for (Inventario inventario : inventarioDAO.listarTodos()) {
            mapa.put(inventario.getProductoId(), inventario);
        }
        return mapa;
    }

    private ProductoResponseDTO aResponseDTO(Producto producto, Map<Integer, Categoria> categorias, Map<Integer, Inventario> inventarios) {
        ProductoResponseDTO dto = new ProductoResponseDTO();
        dto.setId(producto.getId());
        dto.setCategoriaId(producto.getCategoriaId());
        Categoria categoria = categorias.get(producto.getCategoriaId());
        dto.setCategoriaNombre(categoria != null ? categoria.getNombre() : null);
        dto.setNombre(producto.getNombre());
        dto.setDescripcion(producto.getDescripcion());
        dto.setPrecio(producto.getPrecio());
        dto.setImagen(producto.getImagen());
        dto.setTiempoPreparacion(producto.getTiempoPreparacion());
        Inventario inventario = inventarios.get(producto.getId());
        dto.setDisponible(inventario != null && inventario.getCantidad() != null && inventario.getCantidad() > 0);
        return dto;
    }

    private ProductoAdminResponseDTO aAdminDTO(Producto producto, Map<Integer, Categoria> categorias, Map<Integer, Inventario> inventarios) {
        ProductoAdminResponseDTO dto = new ProductoAdminResponseDTO();
        dto.setId(producto.getId());
        dto.setCategoriaId(producto.getCategoriaId());
        Categoria categoria = categorias.get(producto.getCategoriaId());
        dto.setCategoriaNombre(categoria != null ? categoria.getNombre() : null);
        dto.setNombre(producto.getNombre());
        dto.setDescripcion(producto.getDescripcion());
        dto.setPrecio(producto.getPrecio());
        dto.setImagen(producto.getImagen());
        dto.setTiempoPreparacion(producto.getTiempoPreparacion());
        dto.setActivo(producto.getActivo());
        Inventario inventario = inventarios.get(producto.getId());
        dto.setStock(inventario != null ? inventario.getCantidad() : 0);
        dto.setStockMinimo(inventario != null ? inventario.getStockMinimo() : Constantes.STOCK_MINIMO_POR_DEFECTO);
        return dto;
    }

    private void revertir(Connection conexion) {
        if (conexion != null) {
            try {
                conexion.rollback();
            } catch (SQLException ignorada) {
                // La conexion se cerrara de todas formas en el bloque finally.
            }
        }
    }

    private void cerrar(Connection conexion) {
        if (conexion != null) {
            try {
                conexion.close();
            } catch (SQLException ignorada) {
                // No hay una accion util adicional si el cierre falla.
            }
        }
    }
}
