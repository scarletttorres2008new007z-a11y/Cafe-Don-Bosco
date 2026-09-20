package sv.udb.cafedonbosco.dto.request;

import java.math.BigDecimal;
import java.util.List;

public class CompraRequestDTO {

    private String proveedor;
    private List<DetalleCompraRequestDTO> items;

    public CompraRequestDTO() {
    }

    public String getProveedor() {
        return proveedor;
    }

    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }

    public List<DetalleCompraRequestDTO> getItems() {
        return items;
    }

    public void setItems(List<DetalleCompraRequestDTO> items) {
        this.items = items;
    }

    public static class DetalleCompraRequestDTO {
        private Integer productoId;
        private Integer cantidad;
        private BigDecimal costoUnitario;

        public Integer getProductoId() {
            return productoId;
        }

        public void setProductoId(Integer productoId) {
            this.productoId = productoId;
        }

        public Integer getCantidad() {
            return cantidad;
        }

        public void setCantidad(Integer cantidad) {
            this.cantidad = cantidad;
        }

        public BigDecimal getCostoUnitario() {
            return costoUnitario;
        }

        public void setCostoUnitario(BigDecimal costoUnitario) {
            this.costoUnitario = costoUnitario;
        }
    }
}
