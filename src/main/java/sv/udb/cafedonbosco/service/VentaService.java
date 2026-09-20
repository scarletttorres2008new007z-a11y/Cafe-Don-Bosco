package sv.udb.cafedonbosco.service;

import sv.udb.cafedonbosco.dto.request.CheckoutRequestDTO;
import sv.udb.cafedonbosco.dto.request.VentaPresencialRequestDTO;
import sv.udb.cafedonbosco.dto.response.VentaResponseDTO;
import sv.udb.cafedonbosco.model.Carrito;
import sv.udb.cafedonbosco.model.TipoVenta;

import java.util.List;

public interface VentaService {

    /**
     * Valida el carrito y los datos de checkout, revalida precios y stock
     * contra la base de datos dentro de una transaccion, registra la venta
     * WEB con sus detalles, descuenta el inventario y vacia el carrito.
     */
    VentaResponseDTO procesarCheckoutWeb(Carrito carrito, CheckoutRequestDTO datos, Integer usuarioId);

    /**
     * Registra una venta PRESENCIAL desde el mostrador (POS del
     * administrador), con la misma logica transaccional de stock.
     */
    VentaResponseDTO registrarVentaPresencial(VentaPresencialRequestDTO datos, int usuarioAdminId);

    VentaResponseDTO obtenerPorToken(String token);

    VentaResponseDTO obtenerPorId(int id);

    List<VentaResponseDTO> listarHistorial(TipoVenta tipoVenta, int limite);
}
