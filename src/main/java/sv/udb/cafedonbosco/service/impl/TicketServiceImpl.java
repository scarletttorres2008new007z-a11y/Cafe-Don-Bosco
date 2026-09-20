package sv.udb.cafedonbosco.service.impl;

import sv.udb.cafedonbosco.dto.response.VentaResponseDTO;
import sv.udb.cafedonbosco.service.TicketService;
import sv.udb.cafedonbosco.service.VentaService;

public class TicketServiceImpl implements TicketService {

    private final VentaService ventaService;

    public TicketServiceImpl() {
        this.ventaService = new VentaServiceImpl();
    }

    @Override
    public VentaResponseDTO obtenerPorToken(String token) {
        return ventaService.obtenerPorToken(token);
    }

    @Override
    public VentaResponseDTO obtenerPorIdAdmin(int ventaId) {
        return ventaService.obtenerPorId(ventaId);
    }
}
