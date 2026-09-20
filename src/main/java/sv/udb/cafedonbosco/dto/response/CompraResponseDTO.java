package sv.udb.cafedonbosco.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class CompraResponseDTO {

    private Integer id;
    private String proveedor;
    private BigDecimal total;
    private LocalDateTime fecha;
    private List<DetalleVentaResponseDTO> items;

    public CompraResponseDTO() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProveedor() {
        return proveedor;
    }

    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public List<DetalleVentaResponseDTO> getItems() {
        return items;
    }

    public void setItems(List<DetalleVentaResponseDTO> items) {
        this.items = items;
    }
}
