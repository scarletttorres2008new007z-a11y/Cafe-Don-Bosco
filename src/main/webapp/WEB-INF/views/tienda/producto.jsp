<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Cafe Don Bosco - ${producto.nombre}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/variables.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/base.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/tienda.css">
</head>
<body>
<%@ include file="_header.jspf" %>

<div class="contenedor-tienda">
    <a href="${pageContext.request.contextPath}/tienda/menu">&larr; Volver al menu</a>

    <c:choose>
        <c:when test="${not empty error}">
            <div class="mensaje-error" style="margin-top:16px;">${error}</div>
        </c:when>
        <c:otherwise>
            <div class="detalle-layout" style="margin-top:20px;">
                <div class="detalle-imagen">&#9749;</div>

                <div>
                    <span class="etiqueta">${producto.categoriaNombre}</span>
                    <h2>${producto.nombre}</h2>
                    <p>${producto.descripcion}</p>
                    <div class="detalle-precio">$${producto.precioFormateado}</div>

                    <form method="post" action="${pageContext.request.contextPath}/tienda/carrito">
                        <input type="hidden" name="accion" value="agregar">
                        <input type="hidden" name="productoId" value="${producto.id}">
                        <input type="hidden" name="volver" value="${pageContext.request.contextPath}/tienda/producto?id=${producto.id}">
                        <div class="selector-cantidad">
                            <label for="cantidad">Cantidad</label>
                            <input type="number" id="cantidad" name="cantidad" value="1" min="1" max="20">
                        </div>
                        <button type="submit" class="boton" ${producto.disponible ? '' : 'disabled'}>&#128722; Agregar al carrito</button>
                    </form>

                    <div class="info-secundaria">
                        <div class="item">
                            <span class="etiqueta-info">Disponibilidad</span>
                            <c:choose>
                                <c:when test="${producto.disponible}"><span class="disponible">Disponible</span></c:when>
                                <c:otherwise><span class="no-disponible">Agotado</span></c:otherwise>
                            </c:choose>
                        </div>
                        <div class="item">
                            <span class="etiqueta-info">Categoria</span>
                            ${producto.categoriaNombre}
                        </div>
                        <div class="item">
                            <span class="etiqueta-info">Tiempo de preparacion</span>
                            ${empty producto.tiempoPreparacion ? 'No especificado' : producto.tiempoPreparacion}
                        </div>
                    </div>
                </div>
            </div>

            <c:if test="${not empty relacionados}">
                <div class="seccion-titulo">
                    <h2>Productos relacionados</h2>
                </div>
                <div class="grid-productos">
                    <c:forEach var="producto" items="${relacionados}">
                        <%@ include file="_tarjeta-producto.jspf" %>
                    </c:forEach>
                </div>
            </c:if>
        </c:otherwise>
    </c:choose>
</div>

<%@ include file="_footer.jspf" %>
</body>
</html>
