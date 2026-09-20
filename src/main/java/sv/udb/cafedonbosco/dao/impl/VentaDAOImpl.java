package sv.udb.cafedonbosco.dao.impl;

import sv.udb.cafedonbosco.dao.VentaDAO;
import sv.udb.cafedonbosco.exception.ErrorInternoException;
import sv.udb.cafedonbosco.model.DetalleVenta;
import sv.udb.cafedonbosco.model.EstadoVenta;
import sv.udb.cafedonbosco.model.TipoVenta;
import sv.udb.cafedonbosco.model.Venta;
import sv.udb.cafedonbosco.util.ConexionBD;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class VentaDAOImpl implements VentaDAO {

    private static final String COLUMNAS_VENTA =
            "id, usuario_id, tipo_venta, estado, subtotal, envio, total, metodo_pago, "
                    + "estado_pago, tipo_entrega, nombre_cliente, correo_cliente, telefono_cliente, "
                    + "direccion_cliente, notas, token_ticket, fecha";

    @Override
    public Venta crear(Connection conexion, Venta venta) {
        String sql = "INSERT INTO venta (usuario_id, tipo_venta, estado, subtotal, envio, total, "
                + "metodo_pago, estado_pago, tipo_entrega, nombre_cliente, correo_cliente, "
                + "telefono_cliente, direccion_cliente, notas, token_ticket) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (venta.getUsuarioId() != null) {
                stmt.setInt(1, venta.getUsuarioId());
            } else {
                stmt.setNull(1, java.sql.Types.INTEGER);
            }
            stmt.setString(2, venta.getTipoVenta().name());
            stmt.setString(3, venta.getEstado().name());
            stmt.setBigDecimal(4, venta.getSubtotal());
            stmt.setBigDecimal(5, venta.getEnvio());
            stmt.setBigDecimal(6, venta.getTotal());
            stmt.setString(7, venta.getMetodoPago());
            stmt.setString(8, venta.getEstadoPago());
            stmt.setString(9, venta.getTipoEntrega());
            stmt.setString(10, venta.getNombreCliente());
            stmt.setString(11, venta.getCorreoCliente());
            stmt.setString(12, venta.getTelefonoCliente());
            stmt.setString(13, venta.getDireccionCliente());
            stmt.setString(14, venta.getNotas());
            stmt.setString(15, venta.getTokenTicket());
            stmt.executeUpdate();
            try (ResultSet claves = stmt.getGeneratedKeys()) {
                if (claves.next()) {
                    venta.setId(claves.getInt(1));
                }
            }
            return venta;
        } catch (SQLException e) {
            throw new ErrorInternoException("Error al registrar la venta", e);
        }
    }

    @Override
    public void crearDetalle(Connection conexion, DetalleVenta detalle, int ventaId) {
        String sql = "INSERT INTO detalle_venta "
                + "(venta_id, producto_id, nombre_producto, cantidad, precio_unitario, subtotal) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, ventaId);
            stmt.setInt(2, detalle.getProductoId());
            stmt.setString(3, detalle.getNombreProducto());
            stmt.setInt(4, detalle.getCantidad());
            stmt.setBigDecimal(5, detalle.getPrecioUnitario());
            stmt.setBigDecimal(6, detalle.getSubtotal());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new ErrorInternoException("Error al registrar el detalle de la venta", e);
        }
    }

    @Override
    public Venta buscarPorId(int id) {
        return buscarPorCampo("id", String.valueOf(id));
    }

    @Override
    public Venta buscarPorToken(String token) {
        return buscarPorCampo("token_ticket", token);
    }

    private Venta buscarPorCampo(String columna, String valor) {
        String sql = "SELECT " + COLUMNAS_VENTA + " FROM venta WHERE " + columna + " = ?";
        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, valor);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                Venta venta = mapear(rs);
                venta.setDetalles(buscarDetalles(conexion, venta.getId()));
                return venta;
            }
        } catch (SQLException e) {
            throw new ErrorInternoException("Error al buscar la venta", e);
        }
    }

    private List<DetalleVenta> buscarDetalles(Connection conexion, int ventaId) throws SQLException {
        String sql = "SELECT id, venta_id, producto_id, nombre_producto, cantidad, precio_unitario, subtotal "
                + "FROM detalle_venta WHERE venta_id = ?";
        List<DetalleVenta> detalles = new ArrayList<>();
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, ventaId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    DetalleVenta detalle = new DetalleVenta(
                            rs.getInt("producto_id"),
                            rs.getString("nombre_producto"),
                            rs.getInt("cantidad"),
                            rs.getBigDecimal("precio_unitario"),
                            rs.getBigDecimal("subtotal")
                    );
                    detalle.setId(rs.getInt("id"));
                    detalle.setVentaId(rs.getInt("venta_id"));
                    detalles.add(detalle);
                }
            }
        }
        return detalles;
    }

    @Override
    public List<Venta> listarHistorial(TipoVenta tipoVenta, int limite) {
        String sql = "SELECT " + COLUMNAS_VENTA + " FROM venta "
                + (tipoVenta != null ? "WHERE tipo_venta = ? " : "")
                + "ORDER BY fecha DESC LIMIT ?";
        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {
            int indice = 1;
            if (tipoVenta != null) {
                stmt.setString(indice++, tipoVenta.name());
            }
            stmt.setInt(indice, limite);
            List<Venta> ventas = new ArrayList<>();
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ventas.add(mapear(rs));
                }
            }
            return ventas;
        } catch (SQLException e) {
            throw new ErrorInternoException("Error al listar el historial de ventas", e);
        }
    }

    @Override
    public List<Venta> listarRecientes(int limite) {
        return listarHistorial(null, limite);
    }

    @Override
    public BigDecimal sumarTotalDelDia() {
        String sql = "SELECT COALESCE(SUM(total), 0) FROM venta "
                + "WHERE DATE(fecha) = CURDATE() AND estado <> 'CANCELADA'";
        return ejecutarSuma(sql);
    }

    @Override
    public int contarVentasDelDia() {
        String sql = "SELECT COUNT(*) FROM venta WHERE DATE(fecha) = CURDATE() AND estado <> 'CANCELADA'";
        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            throw new ErrorInternoException("Error al contar las ventas del dia", e);
        }
    }

    @Override
    public BigDecimal sumarTotalDelMes() {
        String sql = "SELECT COALESCE(SUM(total), 0) FROM venta "
                + "WHERE YEAR(fecha) = YEAR(CURDATE()) AND MONTH(fecha) = MONTH(CURDATE()) "
                + "AND estado <> 'CANCELADA'";
        return ejecutarSuma(sql);
    }

    private BigDecimal ejecutarSuma(String sql) {
        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getBigDecimal(1) : BigDecimal.ZERO;
        } catch (SQLException e) {
            throw new ErrorInternoException("Error al calcular el total de ventas", e);
        }
    }

    private Venta mapear(ResultSet rs) throws SQLException {
        Venta venta = new Venta();
        venta.setId(rs.getInt("id"));
        int usuarioId = rs.getInt("usuario_id");
        venta.setUsuarioId(rs.wasNull() ? null : usuarioId);
        venta.setTipoVenta(TipoVenta.valueOf(rs.getString("tipo_venta")));
        venta.setEstado(EstadoVenta.valueOf(rs.getString("estado")));
        venta.setSubtotal(rs.getBigDecimal("subtotal"));
        venta.setEnvio(rs.getBigDecimal("envio"));
        venta.setTotal(rs.getBigDecimal("total"));
        venta.setMetodoPago(rs.getString("metodo_pago"));
        venta.setEstadoPago(rs.getString("estado_pago"));
        venta.setTipoEntrega(rs.getString("tipo_entrega"));
        venta.setNombreCliente(rs.getString("nombre_cliente"));
        venta.setCorreoCliente(rs.getString("correo_cliente"));
        venta.setTelefonoCliente(rs.getString("telefono_cliente"));
        venta.setDireccionCliente(rs.getString("direccion_cliente"));
        venta.setNotas(rs.getString("notas"));
        venta.setTokenTicket(rs.getString("token_ticket"));
        Timestamp fecha = rs.getTimestamp("fecha");
        venta.setFecha(fecha != null ? fecha.toLocalDateTime() : null);
        return venta;
    }
}
