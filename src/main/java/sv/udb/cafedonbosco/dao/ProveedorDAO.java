package sv.udb.cafedonbosco.dao;

import sv.udb.cafedonbosco.model.Proveedor;

import java.util.List;

public interface ProveedorDAO {

    List<Proveedor> listarActivos();

    List<Proveedor> listarTodos();

    Proveedor buscarPorId(int id);

    Proveedor crear(Proveedor proveedor);

    void actualizar(Proveedor proveedor);
}
