package sv.udb.cafedonbosco.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.udb.cafedonbosco.dao.UsuarioDAO;
import sv.udb.cafedonbosco.dto.request.RegistroConsumidorDTO;
import sv.udb.cafedonbosco.dto.response.UsuarioResponseDTO;
import sv.udb.cafedonbosco.exception.CredencialesInvalidasException;
import sv.udb.cafedonbosco.exception.RecursoDuplicadoException;
import sv.udb.cafedonbosco.exception.ValidacionException;
import sv.udb.cafedonbosco.model.Rol;
import sv.udb.cafedonbosco.model.Usuario;
import sv.udb.cafedonbosco.service.AuthService;
import sv.udb.cafedonbosco.util.PasswordUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    private static final String PASSWORD_PLANO = "Password123";
    private static final String PASSWORD_HASH = PasswordUtil.hashear(PASSWORD_PLANO);

    @Mock
    private UsuarioDAO usuarioDAO;

    private AuthService authService;

    @BeforeEach
    void configurar() {
        authService = new AuthServiceImpl(usuarioDAO);
    }

    private Usuario usuarioActivo(Rol rol) {
        Usuario usuario = new Usuario();
        usuario.setId(1);
        usuario.setNombre("Ana");
        usuario.setApellido("Perez");
        usuario.setCorreo("ana@correo.com");
        usuario.setPassword(PASSWORD_HASH);
        usuario.setRol(rol);
        usuario.setActivo(true);
        return usuario;
    }

    @Test
    void loginConCredencialesCorrectasDevuelveElUsuario() {
        when(usuarioDAO.buscarPorCorreo("ana@correo.com")).thenReturn(usuarioActivo(Rol.ADMINISTRADOR));

        UsuarioResponseDTO resultado = authService.login("ana@correo.com", PASSWORD_PLANO, Rol.ADMINISTRADOR);

        assertEquals("Ana", resultado.getNombre());
        assertEquals(Rol.ADMINISTRADOR, resultado.getRol());
    }

    @Test
    void loginConContrasenaIncorrectaSeRechaza() {
        when(usuarioDAO.buscarPorCorreo("ana@correo.com")).thenReturn(usuarioActivo(Rol.ADMINISTRADOR));

        assertThrows(CredencialesInvalidasException.class,
                () -> authService.login("ana@correo.com", "otra-contrasena", Rol.ADMINISTRADOR));
    }

    @Test
    void loginConUsuarioInactivoSeRechaza() {
        Usuario inactivo = usuarioActivo(Rol.ADMINISTRADOR);
        inactivo.setActivo(false);
        when(usuarioDAO.buscarPorCorreo("ana@correo.com")).thenReturn(inactivo);

        assertThrows(CredencialesInvalidasException.class,
                () -> authService.login("ana@correo.com", PASSWORD_PLANO, Rol.ADMINISTRADOR));
    }

    @Test
    void loginConUnRolDistintoAlEsperadoSeRechaza() {
        // Un consumidor no debe poder entrar por el login de administrador,
        // aunque su contrasena sea correcta.
        when(usuarioDAO.buscarPorCorreo("ana@correo.com")).thenReturn(usuarioActivo(Rol.CONSUMIDOR));

        assertThrows(CredencialesInvalidasException.class,
                () -> authService.login("ana@correo.com", PASSWORD_PLANO, Rol.ADMINISTRADOR));
    }

    @Test
    void loginConCorreoInexistenteSeRechaza() {
        when(usuarioDAO.buscarPorCorreo("nadie@correo.com")).thenReturn(null);

        assertThrows(CredencialesInvalidasException.class,
                () -> authService.login("nadie@correo.com", PASSWORD_PLANO, null));
    }

    @Test
    void loginConCorreoMalFormadoNiSiquieraConsultaLaBaseDeDatos() {
        assertThrows(CredencialesInvalidasException.class,
                () -> authService.login("no-es-un-correo", PASSWORD_PLANO, null));

        verify(usuarioDAO, never()).buscarPorCorreo(anyString());
    }

    @Test
    void registrarConsumidorConDatosValidosCreaElUsuarioComoConsumidorActivo() {
        RegistroConsumidorDTO datos = new RegistroConsumidorDTO();
        datos.setNombre("Bob");
        datos.setApellido("Lopez");
        datos.setCorreo("Bob@Correo.com");
        datos.setPassword("Password123");

        when(usuarioDAO.existeCorreo("Bob@Correo.com")).thenReturn(false);
        when(usuarioDAO.crear(any(Usuario.class))).thenAnswer(invocacion -> {
            Usuario guardado = invocacion.getArgument(0);
            guardado.setId(5);
            return guardado;
        });

        UsuarioResponseDTO resultado = authService.registrarConsumidor(datos);

        assertEquals(Rol.CONSUMIDOR, resultado.getRol());
        assertEquals("bob@correo.com", resultado.getCorreo());
    }

    @Test
    void registrarConsumidorConCorreoYaRegistradoSeRechaza() {
        RegistroConsumidorDTO datos = new RegistroConsumidorDTO();
        datos.setNombre("Bob");
        datos.setApellido("Lopez");
        datos.setCorreo("bob@correo.com");
        datos.setPassword("Password123");

        when(usuarioDAO.existeCorreo("bob@correo.com")).thenReturn(true);

        assertThrows(RecursoDuplicadoException.class, () -> authService.registrarConsumidor(datos));
        verify(usuarioDAO, never()).crear(any());
    }

    @Test
    void registrarConsumidorConContrasenaCortaSeRechazaAntesDeConsultarLaBaseDeDatos() {
        RegistroConsumidorDTO datos = new RegistroConsumidorDTO();
        datos.setNombre("Bob");
        datos.setApellido("Lopez");
        datos.setCorreo("bob@correo.com");
        datos.setPassword("123");

        assertThrows(ValidacionException.class, () -> authService.registrarConsumidor(datos));
        verify(usuarioDAO, never()).existeCorreo(anyString());
    }

    @Test
    void registrarConsumidorConNombreQueContieneDigitosSeRechaza() {
        RegistroConsumidorDTO datos = new RegistroConsumidorDTO();
        datos.setNombre("Bob123");
        datos.setApellido("Lopez");
        datos.setCorreo("bob@correo.com");
        datos.setPassword(PASSWORD_PLANO);

        assertThrows(ValidacionException.class, () -> authService.registrarConsumidor(datos));
        verify(usuarioDAO, never()).existeCorreo(anyString());
    }

    @Test
    void trasCincoContrasenasIncorrectasSeguidasElLoginQuedaBloqueadoAunqueLaClaveSeaCorrecta() {
        when(usuarioDAO.buscarPorCorreo("ana@correo.com")).thenReturn(usuarioActivo(Rol.ADMINISTRADOR));

        for (int i = 0; i < 5; i++) {
            assertThrows(CredencialesInvalidasException.class,
                    () -> authService.login("ana@correo.com", "clave-incorrecta", Rol.ADMINISTRADOR));
        }

        // La sexta vez, aunque la contrasena ahora si sea la correcta, el
        // limitador ya debe bloquear el intento antes de volver a verificarla.
        assertThrows(ValidacionException.class,
                () -> authService.login("ana@correo.com", PASSWORD_PLANO, Rol.ADMINISTRADOR));
    }

    @Test
    void unLoginExitosoNoQuedaBloqueadoPorIntentosFallidosDeOtroCorreo() {
        when(usuarioDAO.buscarPorCorreo("ana@correo.com")).thenReturn(usuarioActivo(Rol.ADMINISTRADOR));
        when(usuarioDAO.buscarPorCorreo("otro@correo.com")).thenReturn(null);

        for (int i = 0; i < 5; i++) {
            assertThrows(CredencialesInvalidasException.class,
                    () -> authService.login("otro@correo.com", "clave-incorrecta", null));
        }

        UsuarioResponseDTO resultado = authService.login("ana@correo.com", PASSWORD_PLANO, Rol.ADMINISTRADOR);
        assertEquals("ana@correo.com", resultado.getCorreo());
    }
}
