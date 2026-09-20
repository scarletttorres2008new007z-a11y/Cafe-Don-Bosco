package sv.udb.cafedonbosco.util;

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

    // Cabecera de respuesta JSON
    public static final String CONTENT_TYPE_JSON = "application/json; charset=UTF-8";

    // Reglas de negocio
    public static final int STOCK_MINIMO_POR_DEFECTO = 5;
    public static final String ESTADO_PAGO_PENDIENTE = "PENDIENTE";
    public static final String ESTADO_PAGO_APROBADO = "APROBADO";

    public static final String ENTREGA_RECOGER = "RECOGER";
    public static final String ENTREGA_DOMICILIO = "DOMICILIO";
}
