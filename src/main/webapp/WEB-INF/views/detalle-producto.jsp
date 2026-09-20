<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Cafe Don Bosco - Detalle de producto</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/estilo.css">
</head>
<body>
    <header class="encabezado">
        <h1>Cafe Don Bosco</h1>
        <div class="sesion">
            <span>Hola, ${usuario.nombre}</span>
            <a href="${pageContext.request.contextPath}/logout">Cerrar sesion</a>
        </div>
    </header>

    <main class="contenedor">
        <c:choose>
            <c:when test="${not empty error}">
                <div class="mensaje-error">${error}</div>
            </c:when>
            <c:otherwise>
                <div class="detalle-producto">
                    <span class="categoria-etiqueta">${producto.categoriaNombre}</span>
                    <h2>${producto.nombre}</h2>
                    <dl>
                        <dt>Descripcion</dt>
                        <dd>${producto.descripcion}</dd>

                        <dt>Precio</dt>
                        <dd>$<c:out value="${producto.precio}"/></dd>

                        <dt>Tiempo de preparacion</dt>
                        <dd>${producto.tiempoPreparacion}</dd>

                        <dt>Disponibilidad</dt>
                        <dd>
                            <c:choose>
                                <c:when test="${producto.disponible}">
                                    <span class="disponible">Disponible</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="no-disponible">Agotado</span>
                                </c:otherwise>
                            </c:choose>
                        </dd>
                    </dl>
                </div>
            </c:otherwise>
        </c:choose>

        <a class="volver boton" href="${pageContext.request.contextPath}/productos">Volver a productos</a>
    </main>
</body>
</html>
