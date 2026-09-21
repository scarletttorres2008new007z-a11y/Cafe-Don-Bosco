package sv.udb.cafedonbosco.service;

import sv.udb.cafedonbosco.dto.response.CarritoResponseDTO;
import sv.udb.cafedonbosco.model.Carrito;

import java.util.List;

public interface CarritoService {

    void agregarProducto(Carrito carrito, int productoId, int cantidad);

    /** opcionIds puede ser null o vacio si el producto no lleva personalizacion. */
    void agregarProducto(Carrito carrito, int productoId, int cantidad, List<Integer> opcionIds);

    void actualizarCantidad(Carrito carrito, int productoId, int cantidad);

    void eliminarProducto(Carrito carrito, int productoId);

    void vaciar(Carrito carrito);

    CarritoResponseDTO obtenerResumen(Carrito carrito);
}
