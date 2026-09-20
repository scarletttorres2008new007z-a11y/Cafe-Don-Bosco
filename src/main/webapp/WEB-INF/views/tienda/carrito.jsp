<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Cafe Don Bosco - Carrito</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/variables.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/base.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/tienda.css">
</head>
<body>
<%@ include file="_header.jspf" %>

<div class="contenedor-tienda">
    <div class="seccion-titulo" style="margin-top:0;">
        <div>
            <h2>Tu carrito de compras</h2>
            <p>Revisa tus productos antes de continuar con la compra.</p>
        </div>
        <a href="${pageContext.request.contextPath}/tienda/menu">&larr; Seguir comprando</a>
    </div>

    <c:if test="${not empty error}">
        <div class="mensaje-error">${error}</div>
    </c:if>

    <c:choose>
        <c:when test="${empty resumen.items}">
            <p class="estado-vacio">Tu carrito esta vacio. <a href="${pageContext.request.contextPath}/tienda/menu">Ver el menu</a></p>
        </c:when>
        <c:otherwise>
            <div class="layout-carrito">
                <div class="tarjeta">
                    <c:forEach var="item" items="${resumen.items}">
                        <div class="pos-producto">
                            <div class="fila-carrito-item">
                                <div class="imagen-producto">&#9749;</div>
                                <div>
                                    <strong>${item.nombreProducto}</strong><br>
                                    <span class="ayuda">$${item.precioUnitarioFormateado} c/u</span>
                                </div>
                            </div>
                            <div class="controles-cantidad">
                                <form method="post" action="${pageContext.request.contextPath}/tienda/carrito">
                                    <input type="hidden" name="accion" value="decrementar">
                                    <input type="hidden" name="productoId" value="${item.productoId}">
                                    <button type="submit">-</button>
                                </form>
                                <span>${item.cantidad}</span>
                                <form method="post" action="${pageContext.request.contextPath}/tienda/carrito">
                                    <input type="hidden" name="accion" value="agregar">
                                    <input type="hidden" name="productoId" value="${item.productoId}">
                                    <input type="hidden" name="cantidad" value="1">
                                    <button type="submit">+</button>
                                </form>
                            </div>
                            <strong>$${item.subtotalFormateado}</strong>
                            <form method="post" action="${pageContext.request.contextPath}/tienda/carrito">
                                <input type="hidden" name="accion" value="eliminar">
                                <input type="hidden" name="productoId" value="${item.productoId}">
                                <button type="submit" class="boton peligro pequeno">&#128465;</button>
                            </form>
                        </div>
                    </c:forEach>

                    <form method="post" action="${pageContext.request.contextPath}/tienda/carrito" style="margin-top:14px;">
                        <input type="hidden" name="accion" value="vaciar">
                        <button type="submit" class="boton secundario pequeno">Vaciar carrito</button>
                    </form>
                </div>

                <div class="tarjeta resumen-pedido">
                    <h3>Resumen del pedido</h3>
                    <dl>
                        <dt>Subtotal (${resumen.cantidadUnidades} productos)</dt>
                        <dd>$${resumen.subtotalFormateado}</dd>
                        <dt>Envio</dt>
                        <dd>$${resumen.envioFormateado}</dd>
                        <dt class="total-final">Total</dt>
                        <dd class="total-final">$${resumen.totalFormateado}</dd>
                    </dl>
                    <a class="boton" style="width:100%; margin-top:14px; text-align:center;" href="${pageContext.request.contextPath}/tienda/checkout">Proceder al checkout &rarr;</a>
                </div>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<%@ include file="_footer.jspf" %>
</body>
</html>
