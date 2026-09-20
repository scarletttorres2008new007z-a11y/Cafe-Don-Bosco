package sv.udb.cafedonbosco.dto.response;

import java.math.BigDecimal;
import java.util.List;

public class DashboardResponseDTO {

    private int productosDisponibles;
    private BigDecimal ventasHoyTotal;
    private int ventasHoyCantidad;
    private BigDecimal totalVentasMes;
    private List<VentaResponseDTO> ventasRecientes;
    private List<ProductoAdminResponseDTO> productosStockBajo;

    public DashboardResponseDTO() {
    }

    public int getProductosDisponibles() {
        return productosDisponibles;
    }

    public void setProductosDisponibles(int productosDisponibles) {
        this.productosDisponibles = productosDisponibles;
    }

    public BigDecimal getVentasHoyTotal() {
        return ventasHoyTotal;
    }

    public void setVentasHoyTotal(BigDecimal ventasHoyTotal) {
        this.ventasHoyTotal = ventasHoyTotal;
    }

    public int getVentasHoyCantidad() {
        return ventasHoyCantidad;
    }

    public void setVentasHoyCantidad(int ventasHoyCantidad) {
        this.ventasHoyCantidad = ventasHoyCantidad;
    }

    public BigDecimal getTotalVentasMes() {
        return totalVentasMes;
    }

    public void setTotalVentasMes(BigDecimal totalVentasMes) {
        this.totalVentasMes = totalVentasMes;
    }

    public List<VentaResponseDTO> getVentasRecientes() {
        return ventasRecientes;
    }

    public void setVentasRecientes(List<VentaResponseDTO> ventasRecientes) {
        this.ventasRecientes = ventasRecientes;
    }

    public List<ProductoAdminResponseDTO> getProductosStockBajo() {
        return productosStockBajo;
    }

    public void setProductosStockBajo(List<ProductoAdminResponseDTO> productosStockBajo) {
        this.productosStockBajo = productosStockBajo;
    }
}
