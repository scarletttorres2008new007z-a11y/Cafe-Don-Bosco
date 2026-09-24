package sv.udb.cafedonbosco.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Bloquea temporalmente los intentos de login de una misma clave
 * (normalmente el correo) despues de varios intentos fallidos seguidos,
 * para dificultar un ataque de fuerza bruta contra la contrasena. Es en
 * memoria (no persiste en la base de datos ni sobrevive un reinicio de
 * Tomcat): para el tamano de este sistema, correr en una sola instancia,
 * es suficiente y no agrega infraestructura nueva (Redis, etc.).
 */
public class LoginRateLimiter {

    private static final int MAX_INTENTOS_FALLIDOS = 5;
    private static final long VENTANA_BLOQUEO_MS = 15L * 60 * 1000;

    private final Map<String, RegistroIntentos> intentosPorClave = new ConcurrentHashMap<>();

    public boolean estaBloqueado(String clave) {
        RegistroIntentos registro = intentosPorClave.get(normalizar(clave));
        if (registro == null) {
            return false;
        }
        synchronized (registro) {
            return registro.fallos >= MAX_INTENTOS_FALLIDOS
                    && System.currentTimeMillis() - registro.ultimoFallo < VENTANA_BLOQUEO_MS;
        }
    }

    public void registrarFallo(String clave) {
        RegistroIntentos registro = intentosPorClave.computeIfAbsent(normalizar(clave), k -> new RegistroIntentos());
        synchronized (registro) {
            long ahora = System.currentTimeMillis();
            if (ahora - registro.ultimoFallo > VENTANA_BLOQUEO_MS) {
                registro.fallos = 0;
            }
            registro.fallos++;
            registro.ultimoFallo = ahora;
        }
    }

    public void registrarExito(String clave) {
        intentosPorClave.remove(normalizar(clave));
    }

    private String normalizar(String clave) {
        return clave == null ? "" : clave.trim().toLowerCase();
    }

    private static final class RegistroIntentos {
        private int fallos;
        private long ultimoFallo;
    }
}
