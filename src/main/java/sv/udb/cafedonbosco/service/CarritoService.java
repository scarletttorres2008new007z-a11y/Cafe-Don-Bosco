package sv.udb.cafedonbosco.service;

import sv.udb.cafedonbosco.dto.response.CarritoResponseDTO;
import sv.udb.cafedonbosco.model.Carrito;

public interface CarritoService {

    void agregarProducto(Carrito carrito, int productoId, int cantidad);

    void actualizarCantidad(Carrito carrito, int productoId, int cantidad);

    void eliminarProducto(Carrito carrito, int productoId);

    void vaciar(Carrito carrito);

    CarritoResponseDTO obtenerResumen(Carrito carrito);
}
