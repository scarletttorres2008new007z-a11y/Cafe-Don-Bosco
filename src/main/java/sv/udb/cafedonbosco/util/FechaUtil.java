package sv.udb.cafedonbosco.util;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Toda la hora "actual" del negocio (validar horario de atencion, marcar
 * cuando se registro una venta, calcular el tiempo estimado de un pedido)
 * debe pasar por aqui en vez de usar LocalDateTime.now() directamente: si
 * el servidor corre en un contenedor/nube con reloj en UTC, now() daria
 * una hora distinta a la hora real de El Salvador.
 */
public final class FechaUtil {

    public static final ZoneId ZONA_EL_SALVADOR = ZoneId.of("America/El_Salvador");

    private FechaUtil() {
    }

    public static LocalDateTime obtenerFechaHoraActual() {
        return ZonedDateTime.now(ZONA_EL_SALVADOR).toLocalDateTime();
    }

    public static LocalTime obtenerHoraActual() {
        return ZonedDateTime.now(ZONA_EL_SALVADOR).toLocalTime();
    }

    /** Saludo segun la hora real de El Salvador, para encabezados de vistas. */
    public static String obtenerSaludo() {
        int hora = obtenerHoraActual().getHour();
        if (hora < 12) {
            return "Buenos dias";
        }
        if (hora < 19) {
            return "Buenas tardes";
        }
        return "Buenas noches";
    }
}
