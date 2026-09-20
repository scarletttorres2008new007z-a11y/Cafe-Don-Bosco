package sv.udb.cafedonbosco.dao;

import sv.udb.cafedonbosco.model.Producto;

import java.sql.Connection;
import java.util.List;

public interface ProductoDAO {

    List<Producto> listarActivos();

    List<Producto> listarActivosPorCategoria(int categoriaId);

    List<Producto> buscarActivosPorNombre(String texto);

    List<Producto> listarTodos();

    Producto buscarPorId(int id);

    /**
     * Crea el producto dentro de una transaccion ya abierta, para poder
     * insertar en la misma operacion su registro de inventario inicial.
     */
    Producto crear(Connection conexion, Producto producto);

    void actualizar(Producto producto);

    void cambiarEstado(int id, boolean activo);
}
