package sv.udb.cafedonbosco.util;

import java.math.BigDecimal;

public final class Constantes {

    private Constantes() {
    }

    // Atributos de sesion HTTP
    public static final String SESSION_USUARIO = "usuarioAutenticado";
    public static final String SESSION_CARRITO = "carrito";
    // Carrito de la venta presencial (POS del administrador); se guarda
    // separado del carrito del consumidor para que compartir el mismo
    // navegador de pruebas no mezcle una venta de mostrador con una compra
    // web.
    public static final String SESSION_CARRITO_ADMIN = "carritoAdmin";
    // Clave de idempotencia del intento de checkout en curso: se genera al
    // mostrar el formulario y se reutiliza en reintentos de la misma
    // sesion (p. ej. doble clic o un reenvio accidental) para que el
    // backend pueda detectar y descartar una venta duplicada.
    public static final String SESSION_CHECKOUT_IDEMPOTENCY = "checkoutIdempotencyKey";

    // Cabecera de respuesta JSON
    public static final String CONTENT_TYPE_JSON = "application/json; charset=UTF-8";

    // Reglas de negocio
    public static final int STOCK_MINIMO_POR_DEFECTO = 5;

    public static final String ENTREGA_RECOGER = "RECOGER";
    public static final String ENTREGA_DOMICILIO = "DOMICILIO";

    /** Tarifa fija de envio a domicilio; recoger en tienda siempre es 0. */
    public static final BigDecimal TARIFA_ENVIO_DOMICILIO = new BigDecimal("1.50");

    /** Limite maximo para "size"/"limite" en listados paginados. */
    public static final int TAMANO_PAGINA_MAXIMO = 100;
    public static final int TAMANO_PAGINA_POR_DEFECTO = 20;
}
