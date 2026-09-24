package sv.udb.cafedonbosco.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginRateLimiterTest {

    @Test
    void noBloqueaAntesDeAgotarLosIntentos() {
        LoginRateLimiter limitador = new LoginRateLimiter();
        for (int i = 0; i < 4; i++) {
            limitador.registrarFallo("ana@correo.com");
        }
        assertFalse(limitador.estaBloqueado("ana@correo.com"));
    }

    @Test
    void bloqueaTrasCincoFallosSeguidos() {
        LoginRateLimiter limitador = new LoginRateLimiter();
        for (int i = 0; i < 5; i++) {
            limitador.registrarFallo("ana@correo.com");
        }
        assertTrue(limitador.estaBloqueado("ana@correo.com"));
    }

    @Test
    void unLoginExitosoReiniciaElContador() {
        LoginRateLimiter limitador = new LoginRateLimiter();
        for (int i = 0; i < 4; i++) {
            limitador.registrarFallo("ana@correo.com");
        }
        limitador.registrarExito("ana@correo.com");
        limitador.registrarFallo("ana@correo.com");
        assertFalse(limitador.estaBloqueado("ana@correo.com"));
    }

    @Test
    void elBloqueoEsPorClaveNoAfectaAOtroCorreo() {
        LoginRateLimiter limitador = new LoginRateLimiter();
        for (int i = 0; i < 5; i++) {
            limitador.registrarFallo("ana@correo.com");
        }
        assertFalse(limitador.estaBloqueado("bob@correo.com"));
    }

    @Test
    void laClaveNoDistingueMayusculasNiEspacios() {
        LoginRateLimiter limitador = new LoginRateLimiter();
        for (int i = 0; i < 5; i++) {
            limitador.registrarFallo(" Ana@Correo.com ");
        }
        assertTrue(limitador.estaBloqueado("ana@correo.com"));
    }
}
