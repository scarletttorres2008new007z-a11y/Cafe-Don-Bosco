package sv.udb.cafedonbosco.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Carrito de compras del consumidor. Se guarda como atributo de
 * HttpSession (ver Constantes.SESSION_CARRITO), por lo que implementa
 * Serializable.
 */
public class Carrito implements Serializable {

    private final Map<Integer, CarritoItem> items = new LinkedHashMap<>();

    public void agregarProducto(CarritoItem nuevo) {
        CarritoItem existente = items.get(nuevo.getProductoId());
        if (existente != null) {
            existente.setCantidad(existente.getCantidad() + nuevo.getCantidad());
            // El carrito solo guarda una linea por producto: si el cliente
            // vuelve a agregar el mismo producto con una personalizacion
            // distinta (desde la ficha del producto), la nueva reemplaza a
            // la anterior para toda la linea (no se separan en dos lineas
            // independientes). Pero un simple "+1" sin opciones (boton del
            // carrito, tarjeta del catalogo, vista rapida) no debe borrar
            // la personalizacion que la linea ya tenia.
            if (nuevo.getOpciones() != null && !nuevo.getOpciones().isEmpty()) {
                existente.setOpciones(nuevo.getOpciones());
            }
        } else {
            items.put(nuevo.getProductoId(), nuevo);
        }
    }

    public void actualizarCantidad(Integer productoId, Integer cantidad) {
        CarritoItem item = items.get(productoId);
        if (item != null) {
            item.setCantidad(cantidad);
        }
    }

    public void eliminarProducto(Integer productoId) {
        items.remove(productoId);
    }

    public void vaciar() {
        items.clear();
    }

    public Map<Integer, CarritoItem> getItems() {
        return items;
    }

    public boolean estaVacio() {
        return items.isEmpty();
    }

    public BigDecimal calcularSubtotal() {
        BigDecimal subtotal = BigDecimal.ZERO;
        for (CarritoItem item : items.values()) {
            subtotal = subtotal.add(item.getSubtotal());
        }
        return subtotal;
    }

    public int contarUnidades() {
        int total = 0;
        for (CarritoItem item : items.values()) {
            total += item.getCantidad();
        }
        return total;
    }
}
