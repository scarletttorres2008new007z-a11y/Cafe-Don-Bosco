package sv.udb.cafedonbosco.service;

import sv.udb.cafedonbosco.dto.response.VentaResponseDTO;

public interface TicketService {

    /**
     * Consulta el ticket de una venta ya registrada usando el token de
     * acceso aleatorio, nunca el id incremental, para que un consumidor
     * invitado no pueda ver tickets ajenos adivinando el numero de venta.
     */
    VentaResponseDTO obtenerPorToken(String token);

    /**
     * Consulta el ticket de cualquier venta por id; solo debe exponerse en
     * rutas protegidas para el administrador.
     */
    VentaResponseDTO obtenerPorIdAdmin(int ventaId);
}
