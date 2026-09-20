package sv.udb.cafedonbosco.dto.request;

public class CarritoItemRequestDTO {

    private Integer productoId;
    private Integer cantidad;

    public CarritoItemRequestDTO() {
    }

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
}
