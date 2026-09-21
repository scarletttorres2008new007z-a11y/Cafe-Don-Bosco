package sv.udb.cafedonbosco.model;

import sv.udb.cafedonbosco.util.FormatoUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Item del carrito de compras. El carrito vive en la sesion HTTP y no se
 * persiste en base de datos; el precio se toma del producto en el momento
 * de agregarlo, pero se vuelve a validar contra la BD durante el checkout.
 * precioUnitario es siempre el precio base del producto SIN opciones; el
 * precio adicional de las opciones elegidas (opciones) se suma aparte,
 * ver getPrecioUnitarioConOpciones().
 */
public class CarritoItem {

    private Integer productoId;
    private String nombreProducto;
    private BigDecimal precioUnitario;
    private Integer cantidad;
    private String imagen;
    private List<OpcionSeleccionada> opciones = new ArrayList<>();

    public CarritoItem() {
    }

    public CarritoItem(
            Integer productoId,
            String nombreProducto,
            BigDecimal precioUnitario,
            Integer cantidad,
            String imagen
    ) {
        this(productoId, nombreProducto, precioUnitario, cantidad, imagen, new ArrayList<>());
    }

    public CarritoItem(
            Integer productoId,
            String nombreProducto,
            BigDecimal precioUnitario,
            Integer cantidad,
            String imagen,
            List<OpcionSeleccionada> opciones
    ) {
        this.productoId = productoId;
        this.nombreProducto = nombreProducto;
        this.precioUnitario = precioUnitario;
        this.cantidad = cantidad;
        this.imagen = imagen;
        this.opciones = opciones != null ? opciones : new ArrayList<>();
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

    public List<OpcionSeleccionada> getOpciones() {
        return opciones;
    }

    public void setOpciones(List<OpcionSeleccionada> opciones) {
        this.opciones = opciones != null ? opciones : new ArrayList<>();
    }

    public BigDecimal getPrecioAdicionalOpciones() {
        BigDecimal total = BigDecimal.ZERO;
        for (OpcionSeleccionada opcion : opciones) {
            total = total.add(opcion.getPrecioAdicional());
        }
        return total;
    }

    public BigDecimal getPrecioUnitarioConOpciones() {
        return precioUnitario.add(getPrecioAdicionalOpciones());
    }

    public BigDecimal getSubtotal() {
        return getPrecioUnitarioConOpciones().multiply(BigDecimal.valueOf(cantidad));
    }

    public String getPrecioUnitarioFormateado() {
        return FormatoUtil.moneda(precioUnitario);
    }

    public String getPrecioUnitarioConOpcionesFormateado() {
        return FormatoUtil.moneda(getPrecioUnitarioConOpciones());
    }

    public String getSubtotalFormateado() {
        return FormatoUtil.moneda(getSubtotal());
    }
}
