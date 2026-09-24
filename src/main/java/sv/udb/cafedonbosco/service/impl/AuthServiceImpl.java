package sv.udb.cafedonbosco.service.impl;

import sv.udb.cafedonbosco.dao.UsuarioDAO;
import sv.udb.cafedonbosco.dao.impl.UsuarioDAOImpl;
import sv.udb.cafedonbosco.dto.request.ActualizarPerfilRequestDTO;
import sv.udb.cafedonbosco.dto.request.RegistroConsumidorDTO;
import sv.udb.cafedonbosco.dto.response.UsuarioResponseDTO;
import sv.udb.cafedonbosco.exception.CredencialesInvalidasException;
import sv.udb.cafedonbosco.exception.RecursoDuplicadoException;
import sv.udb.cafedonbosco.exception.RecursoNoEncontradoException;
import sv.udb.cafedonbosco.exception.ValidacionException;
import sv.udb.cafedonbosco.model.Rol;
import sv.udb.cafedonbosco.model.Usuario;
import sv.udb.cafedonbosco.service.AuthService;
import sv.udb.cafedonbosco.util.LoginRateLimiter;
import sv.udb.cafedonbosco.util.PasswordUtil;
import sv.udb.cafedonbosco.util.ValidacionUtil;

public class AuthServiceImpl implements AuthService {

    private final UsuarioDAO usuarioDAO;
    private final LoginRateLimiter loginRateLimiter;

    public AuthServiceImpl() {
        this(new UsuarioDAOImpl());
    }

    /** Permite inyectar un UsuarioDAO de prueba (Mockito) sin tocar una base de datos real. */
    public AuthServiceImpl(UsuarioDAO usuarioDAO) {
        this(usuarioDAO, new LoginRateLimiter());
    }

    public AuthServiceImpl(UsuarioDAO usuarioDAO, LoginRateLimiter loginRateLimiter) {
        this.usuarioDAO = usuarioDAO;
        this.loginRateLimiter = loginRateLimiter;
    }

    @Override
    public UsuarioResponseDTO login(String correo, String password, Rol rolEsperado) {
        if (!ValidacionUtil.esCorreoValido(correo) || !ValidacionUtil.esTextoValido(password)) {
            throw new CredencialesInvalidasException();
        }
        if (loginRateLimiter.estaBloqueado(correo)) {
            throw new ValidacionException("Demasiados intentos fallidos. Intenta de nuevo en unos minutos.");
        }

        Usuario usuario = usuarioDAO.buscarPorCorreo(correo);
        if (usuario == null || !Boolean.TRUE.equals(usuario.getActivo())) {
            loginRateLimiter.registrarFallo(correo);
            throw new CredencialesInvalidasException();
        }
        if (!PasswordUtil.verificar(password, usuario.getPassword())) {
            loginRateLimiter.registrarFallo(correo);
            throw new CredencialesInvalidasException();
        }
        if (rolEsperado != null && usuario.getRol() != rolEsperado) {
            loginRateLimiter.registrarFallo(correo);
            throw new CredencialesInvalidasException();
        }

        loginRateLimiter.registrarExito(correo);
        return aDTO(usuario);
    }

    @Override
    public UsuarioResponseDTO registrarConsumidor(RegistroConsumidorDTO datos) {
        if (datos == null
                || !ValidacionUtil.esNombreValido(datos.getNombre())
                || !ValidacionUtil.esNombreValido(datos.getApellido())
                || !ValidacionUtil.esCorreoValido(datos.getCorreo())
                || !ValidacionUtil.esPasswordValida(datos.getPassword())) {
            throw new ValidacionException("Revisa los datos del registro: nombre y apellido (solo letras y espacios), correo valido y contrasena de 6 a 72 caracteres.");
        }
        if (usuarioDAO.existeCorreo(datos.getCorreo())) {
            throw new RecursoDuplicadoException("Ya existe una cuenta registrada con ese correo.");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(datos.getNombre().trim());
        usuario.setApellido(datos.getApellido().trim());
        usuario.setCorreo(datos.getCorreo().trim().toLowerCase());
        usuario.setPassword(PasswordUtil.hashear(datos.getPassword()));
        usuario.setRol(Rol.CONSUMIDOR);
        usuario.setActivo(true);

        return aDTO(usuarioDAO.crear(usuario));
    }

    @Override
    public UsuarioResponseDTO obtenerPerfil(int usuarioId) {
        Usuario usuario = usuarioDAO.buscarPorId(usuarioId);
        if (usuario == null) {
            throw new RecursoNoEncontradoException("El usuario no existe.");
        }
        return aDTO(usuario);
    }

    @Override
    public UsuarioResponseDTO actualizarPerfil(int usuarioId, ActualizarPerfilRequestDTO datos) {
        if (datos == null
                || !ValidacionUtil.esNombreValido(datos.getNombre())
                || !ValidacionUtil.esNombreValido(datos.getApellido())
                || !ValidacionUtil.esCorreoValido(datos.getCorreo())) {
            throw new ValidacionException("Revisa los datos del perfil: nombre y apellido (solo letras y espacios) y correo valido son obligatorios.");
        }
        Usuario usuario = usuarioDAO.buscarPorId(usuarioId);
        if (usuario == null) {
            throw new RecursoNoEncontradoException("El usuario no existe.");
        }

        String nuevoCorreo = datos.getCorreo().trim().toLowerCase();
        if (!nuevoCorreo.equalsIgnoreCase(usuario.getCorreo())) {
            Usuario existente = usuarioDAO.buscarPorCorreo(nuevoCorreo);
            if (existente != null && !existente.getId().equals(usuarioId)) {
                throw new RecursoDuplicadoException("Ya existe una cuenta registrada con ese correo.");
            }
        }

        usuario.setNombre(datos.getNombre().trim());
        usuario.setApellido(datos.getApellido().trim());
        usuario.setCorreo(nuevoCorreo);
        usuarioDAO.actualizarPerfil(usuario);
        return aDTO(usuario);
    }

    private UsuarioResponseDTO aDTO(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getCorreo(),
                usuario.getRol(),
                usuario.getActivo()
        );
    }
}
