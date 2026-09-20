<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Cafe Don Bosco - Nueva venta</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/variables.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/base.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css">
</head>
<body>
<div class="app-admin">
    <%@ include file="_sidebar.jspf" %>

    <main class="contenido-admin">
        <div class="encabezado-admin">
            <div>
                <h2>Nueva venta</h2>
                <p>Registra una venta presencial desde el mostrador.</p>
            </div>
        </div>

        <c:if test="${not empty error}">
            <div class="mensaje-error">${error}</div>
        </c:if>

        <div class="pos-layout">
            <div class="panel">
                <div class="panel-encabezado">
                    <h3>Productos</h3>
                </div>
                <c:forEach var="producto" items="${productos}">
                    <c:if test="${producto.activo}">
                        <div class="pos-producto">
                            <div class="info">
                                <strong>${producto.nombre}</strong>
                                <span>${producto.categoriaNombre} &middot; $${producto.precioFormateado} &middot; stock: ${producto.stock}</span>
                            </div>
                            <form method="post" action="${pageContext.request.contextPath}/admin/venta-nueva">
                                <input type="hidden" name="accion" value="agregar">
                                <input type="hidden" name="productoId" value="${producto.id}">
                                <button type="submit" class="boton pequeno" ${producto.stock <= 0 ? 'disabled' : ''}>Agregar</button>
                            </form>
                        </div>
                    </c:if>
                </c:forEach>
            </div>

            <div class="panel">
                <div class="panel-encabezado">
                    <h3>Venta actual</h3>
                    <c:if test="${not empty carrito.items}">
                        <form method="post" action="${pageContext.request.contextPath}/admin/venta-nueva">
                            <input type="hidden" name="accion" value="vaciar">
                            <button type="submit" class="boton secundario pequeno">Vaciar</button>
                        </form>
                    </c:if>
                </div>

                <c:choose>
                    <c:when test="${empty carrito.items}">
                        <p class="estado-vacio">Agrega productos para iniciar la venta.</p>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="item" items="${carrito.items}">
                            <div class="pos-carrito-item">
                                <div class="info">
                                    <strong>${item.nombreProducto}</strong>
                                    <span>${item.cantidad} x $${item.precioUnitarioFormateado} = $${item.subtotalFormateado}</span>
                                </div>
                                <div style="display:flex; gap:4px;">
                                    <form method="post" action="${pageContext.request.contextPath}/admin/venta-nueva">
                                        <input type="hidden" name="accion" value="decrementar">
                                        <input type="hidden" name="productoId" value="${item.productoId}">
                                        <button type="submit" class="boton secundario pequeno">-</button>
                                    </form>
                                    <form method="post" action="${pageContext.request.contextPath}/admin/venta-nueva">
                                        <input type="hidden" name="accion" value="agregar">
                                        <input type="hidden" name="productoId" value="${item.productoId}">
                                        <button type="submit" class="boton secundario pequeno">+</button>
                                    </form>
                                    <form method="post" action="${pageContext.request.contextPath}/admin/venta-nueva">
                                        <input type="hidden" name="accion" value="eliminar">
                                        <input type="hidden" name="productoId" value="${item.productoId}">
                                        <button type="submit" class="boton peligro pequeno">&times;</button>
                                    </form>
                                </div>
                            </div>
                        </c:forEach>

                        <div class="pos-total">
                            <span>Total</span>
                            <span>$${carrito.totalFormateado}</span>
                        </div>

                        <form method="post" action="${pageContext.request.contextPath}/admin/venta-nueva" style="margin-top:16px;">
                            <input type="hidden" name="accion" value="confirmar">
                            <div class="campo">
                                <label for="metodoPago">Metodo de pago</label>
                                <select id="metodoPago" name="metodoPago">
                                    <option value="EFECTIVO">Efectivo</option>
                                    <option value="TARJETA">Tarjeta</option>
                                    <option value="TRANSFERENCIA">Transferencia</option>
                                </select>
                            </div>
                            <button type="submit" class="boton" style="width:100%;">Registrar venta</button>
                        </form>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </main>
</div>
</body>
</html>
