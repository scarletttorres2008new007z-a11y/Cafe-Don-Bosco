package sv.udb.cafedonbosco.dao.impl;

import sv.udb.cafedonbosco.dao.ProductoDAO;
import sv.udb.cafedonbosco.exception.ErrorInternoException;
import sv.udb.cafedonbosco.model.Producto;
import sv.udb.cafedonbosco.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAOImpl implements ProductoDAO {

    private static final String COLUMNAS =
            "id, categoria_id, nombre, descripcion, precio, imagen, tiempo_preparacion, activo";

    @Override
    public List<Producto> listarActivos() {
        return listar("SELECT " + COLUMNAS + " FROM producto WHERE activo = TRUE ORDER BY nombre");
    }

    @Override
    public List<Producto> listarActivosPorCategoria(int categoriaId) {
        String sql = "SELECT " + COLUMNAS + " FROM producto WHERE activo = TRUE AND categoria_id = ? ORDER BY nombre";
        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, categoriaId);
            return ejecutarListado(stmt);
        } catch (SQLException e) {
            throw new ErrorInternoException("Error al listar productos por categoria", e);
        }
    }

    @Override
    public List<Producto> buscarActivosPorNombre(String texto) {
        String sql = "SELECT " + COLUMNAS + " FROM producto "
                + "WHERE activo = TRUE AND (nombre LIKE ? OR descripcion LIKE ?) ORDER BY nombre";
        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {
            String comodin = "%" + texto + "%";
            stmt.setString(1, comodin);
            stmt.setString(2, comodin);
            return ejecutarListado(stmt);
        } catch (SQLException e) {
            throw new ErrorInternoException("Error al buscar productos por nombre", e);
        }
    }

    @Override
    public List<Producto> listarTodos() {
        return listar("SELECT " + COLUMNAS + " FROM producto ORDER BY nombre");
    }

    private List<Producto> listar(String sql) {
        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {
            return ejecutarListado(stmt);
        } catch (SQLException e) {
            throw new ErrorInternoException("Error al listar productos", e);
        }
    }

    private List<Producto> ejecutarListado(PreparedStatement stmt) throws SQLException {
        List<Producto> productos = new ArrayList<>();
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                productos.add(mapear(rs));
            }
        }
        return productos;
    }

    @Override
    public Producto buscarPorId(int id) {
        String sql = "SELECT " + COLUMNAS + " FROM producto WHERE id = ?";
        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new ErrorInternoException("Error al buscar el producto", e);
        }
    }

    @Override
    public Producto crear(Connection conexion, Producto producto) {
        String sql = "INSERT INTO producto "
                + "(categoria_id, nombre, descripcion, precio, imagen, tiempo_preparacion, activo) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            enlazarCampos(stmt, producto);
            stmt.executeUpdate();
            try (ResultSet claves = stmt.getGeneratedKeys()) {
                if (claves.next()) {
                    producto.setId(claves.getInt(1));
                }
            }
            return producto;
        } catch (SQLException e) {
            throw new ErrorInternoException("Error al crear el producto", e);
        }
    }

    @Override
    public void actualizar(Producto producto) {
        String sql = "UPDATE producto SET categoria_id = ?, nombre = ?, descripcion = ?, "
                + "precio = ?, imagen = ?, tiempo_preparacion = ?, activo = ? WHERE id = ?";
        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {
            enlazarCampos(stmt, producto);
            stmt.setInt(8, producto.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new ErrorInternoException("Error al actualizar el producto", e);
        }
    }

    @Override
    public void cambiarEstado(int id, boolean activo) {
        String sql = "UPDATE producto SET activo = ? WHERE id = ?";
        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setBoolean(1, activo);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new ErrorInternoException("Error al cambiar el estado del producto", e);
        }
    }

    private void enlazarCampos(PreparedStatement stmt, Producto producto) throws SQLException {
        stmt.setInt(1, producto.getCategoriaId());
        stmt.setString(2, producto.getNombre());
        stmt.setString(3, producto.getDescripcion());
        stmt.setBigDecimal(4, producto.getPrecio());
        stmt.setString(5, producto.getImagen());
        stmt.setString(6, producto.getTiempoPreparacion());
        stmt.setBoolean(7, Boolean.TRUE.equals(producto.getActivo()));
    }

    private Producto mapear(ResultSet rs) throws SQLException {
        return new Producto(
                rs.getInt("id"),
                rs.getInt("categoria_id"),
                rs.getString("nombre"),
                rs.getString("descripcion"),
                rs.getBigDecimal("precio"),
                rs.getString("imagen"),
                rs.getString("tiempo_preparacion"),
                rs.getBoolean("activo")
        );
    }
}
