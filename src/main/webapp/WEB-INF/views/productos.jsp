<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Cafe Don Bosco - Productos</title>
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
        <h2>Productos</h2>
        <p>Estos son los productos disponibles en el catalogo.</p>

        <c:if test="${empty productos}">
            <p>No hay productos registrados todavia.</p>
        </c:if>

        <div class="grid-productos">
            <c:forEach var="producto" items="${productos}">
                <div class="tarjeta-producto">
                    <span class="categoria-etiqueta">${producto.categoriaNombre}</span>
                    <h3>${producto.nombre}</h3>
                    <p>${producto.descripcion}</p>
                    <span class="precio">$<c:out value="${producto.precio}"/></span>
                    <c:choose>
                        <c:when test="${producto.disponible}">
                            <span class="disponible">Disponible</span>
                        </c:when>
                        <c:otherwise>
                            <span class="no-disponible">Agotado</span>
                        </c:otherwise>
                    </c:choose>
                    <a class="boton" href="${pageContext.request.contextPath}/producto?id=${producto.id}">Ver detalle</a>
                </div>
            </c:forEach>
        </div>
    </main>
</body>
</html>
