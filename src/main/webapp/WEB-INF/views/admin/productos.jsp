<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Cafe Don Bosco - Productos</title>
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
                <h2>Productos</h2>
                <p>Catalogo completo con stock e informacion administrativa.</p>
            </div>
        </div>

        <div class="panel">
            <c:choose>
                <c:when test="${empty productos}">
                    <p class="estado-vacio">Todavia no hay productos registrados.</p>
                </c:when>
                <c:otherwise>
                    <table class="tabla">
                        <thead>
                        <tr>
                            <th>Producto</th>
                            <th>Categoria</th>
                            <th>Precio</th>
                            <th>Stock</th>
                            <th>Estado</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="producto" items="${productos}">
                            <tr>
                                <td>
                                    <strong>${producto.nombre}</strong><br>
                                    <span class="ayuda">${producto.descripcion}</span>
                                </td>
                                <td>${producto.categoriaNombre}</td>
                                <td>$${producto.precioFormateado}</td>
                                <td>
                                    ${producto.stock}
                                    <c:if test="${producto.stock <= producto.stockMinimo}">
                                        <span class="etiqueta" style="color:var(--color-error); background:var(--color-error-fondo);">Stock bajo</span>
                                    </c:if>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${producto.activo}"><span class="disponible">Activo</span></c:when>
                                        <c:otherwise><span class="no-disponible">Inactivo</span></c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>
        </div>
    </main>
</div>
</body>
</html>
