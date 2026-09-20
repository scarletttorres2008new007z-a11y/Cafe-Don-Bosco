package sv.udb.cafedonbosco.dao;

import sv.udb.cafedonbosco.model.Usuario;

public interface UsuarioDAO {

    Usuario buscarPorCorreo(String correo);

    Usuario buscarPorId(int id);

    boolean existeCorreo(String correo);

    Usuario crear(Usuario usuario);
}
