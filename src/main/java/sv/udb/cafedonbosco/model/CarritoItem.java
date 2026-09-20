package sv.udb.cafedonbosco.model;

import java.math.BigDecimal;

/**
 * Item del carrito de compras. El carrito vive en la sesion HTTP y no se
 * persiste en base de datos; el precio se toma del producto en el momento
 * de agregarlo, pero se vuelve a validar contra la BD durante el checkout.
 */
public class CarritoItem {

    private Integer productoId;
    private String nombreProducto;
    private BigDecimal precioUnitario;
    private Integer cantidad;
    private String imagen;

    public CarritoItem() {
    }

    public CarritoItem(
            Integer productoId,
            String nombreProducto,
            BigDecimal precioUnitario,
            Integer cantidad,
            String imagen
    ) {
        this.productoId = productoId;
        this.nombreProducto = nombreProducto;
        this.precioUnitario = precioUnitario;
        this.cantidad = cantidad;
        this.imagen = imagen;
    }

    public Integer getProductoId() {
        return productoId;
    }

    public void setProductoId(Integer productoId) {
        this.productoId = productoId;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public BigDecimal getSubtotal() {
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }
}
