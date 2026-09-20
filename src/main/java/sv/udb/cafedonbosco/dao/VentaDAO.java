package sv.udb.cafedonbosco.dao;

import sv.udb.cafedonbosco.model.DetalleVenta;
import sv.udb.cafedonbosco.model.TipoVenta;
import sv.udb.cafedonbosco.model.Venta;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;

public interface VentaDAO {

    /**
     * Inserta la cabecera de la venta dentro de una transaccion ya abierta
     * y devuelve el id generado en venta.setId(...).
     */
    Venta crear(Connection conexion, Venta venta);

    void crearDetalle(Connection conexion, DetalleVenta detalle, int ventaId);

    Venta buscarPorId(int id);

    Venta buscarPorToken(String token);

    List<Venta> listarHistorial(TipoVenta tipoVenta, int limite);

    List<Venta> listarRecientes(int limite);

    BigDecimal sumarTotalDelDia();

    int contarVentasDelDia();

    BigDecimal sumarTotalDelMes();
}
