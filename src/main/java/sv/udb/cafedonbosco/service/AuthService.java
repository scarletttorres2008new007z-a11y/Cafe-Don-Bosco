package sv.udb.cafedonbosco.service;

import sv.udb.cafedonbosco.dto.request.RegistroConsumidorDTO;
import sv.udb.cafedonbosco.dto.response.UsuarioResponseDTO;
import sv.udb.cafedonbosco.model.Rol;

public interface AuthService {

    UsuarioResponseDTO login(String correo, String password, Rol rolEsperado);

    UsuarioResponseDTO registrarConsumidor(RegistroConsumidorDTO datos);
}
