package sv.udb.cafedonbosco.dao.impl;

import sv.udb.cafedonbosco.dao.InventarioDAO;
import sv.udb.cafedonbosco.exception.ErrorInternoException;
import sv.udb.cafedonbosco.model.Inventario;
import sv.udb.cafedonbosco.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InventarioDAOImpl implements InventarioDAO {

    @Override
    public List<Inventario> listarTodos() {
        String sql = "SELECT id, producto_id, cantidad, stock_minimo FROM inventario";
        List<Inventario> inventarios = new ArrayList<>();
        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                inventarios.add(mapear(rs));
            }
            return inventarios;
        } catch (SQLException e) {
            throw new ErrorInternoException("Error al listar el inventario", e);
        }
    }

    @Override
    public Inventario buscarPorProducto(int productoId) {
        String sql = "SELECT id, producto_id, cantidad, stock_minimo FROM inventario WHERE producto_id = ?";
        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, productoId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new ErrorInternoException("Error al buscar el inventario del producto", e);
        }
    }

    @Override
    public void crear(Connection conexion, Inventario inventario) {
        String sql = "INSERT INTO inventario (producto_id, cantidad, stock_minimo) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, inventario.getProductoId());
            stmt.setInt(2, inventario.getCantidad());
            stmt.setInt(3, inventario.getStockMinimo());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new ErrorInternoException("Error al crear el registro de inventario", e);
        }
    }

    @Override
    public void actualizarStockMinimo(int productoId, int stockMinimo) {
        String sql = "UPDATE inventario SET stock_minimo = ? WHERE producto_id = ?";
        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, stockMinimo);
            stmt.setInt(2, productoId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new ErrorInternoException("Error al actualizar el stock minimo", e);
        }
    }

    @Override
    public void fijarCantidad(int productoId, int cantidad) {
        String sql = "UPDATE inventario SET cantidad = ? WHERE producto_id = ?";
        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, cantidad);
            stmt.setInt(2, productoId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new ErrorInternoException("Error al fijar la cantidad de inventario", e);
        }
    }

    @Override
    public boolean descontarStock(Connection conexion, int productoId, int cantidad) {
        String sql = "UPDATE inventario SET cantidad = cantidad - ? "
                + "WHERE producto_id = ? AND cantidad >= ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, cantidad);
            stmt.setInt(2, productoId);
            stmt.setInt(3, cantidad);
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new ErrorInternoException("Error al descontar stock", e);
        }
    }

    @Override
    public void incrementarStock(Connection conexion, int productoId, int cantidad) {
        String sql = "UPDATE inventario SET cantidad = cantidad + ? WHERE producto_id = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, cantidad);
            stmt.setInt(2, productoId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new ErrorInternoException("Error al incrementar stock", e);
        }
    }

    private Inventario mapear(ResultSet rs) throws SQLException {
        return new Inventario(
                rs.getInt("id"),
                rs.getInt("producto_id"),
                rs.getInt("cantidad"),
                rs.getInt("stock_minimo")
        );
    }
}
