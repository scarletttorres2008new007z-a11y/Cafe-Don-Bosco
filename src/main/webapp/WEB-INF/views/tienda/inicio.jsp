<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Cafe Don Bosco</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/variables.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/base.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/tienda.css">
</head>
<body>
<%@ include file="_header.jspf" %>

<section class="hero-tienda">
    <p class="etiqueta-superior">Cafe Don Bosco</p>
    <h2>El cafe que hace especial tu momento.</h2>
    <p>Cafe, bebidas, postres y comida. Todo lo que necesitas para disfrutar el mejor sabor.</p>
    <a class="boton" href="${pageContext.request.contextPath}/tienda/menu" style="margin-top:16px; background-color:var(--color-cobre-claro); color:var(--color-cafe-oscuro); font-weight:bold;">Ver menu &rarr;</a>
</section>

<div class="contenedor-tienda">
    <div class="seccion-titulo">
        <h2>&#9733; Nuestros favoritos</h2>
        <a href="${pageContext.request.contextPath}/tienda/menu">Ver todo &rarr;</a>
    </div>
    <div class="grid-productos">
        <c:forEach var="producto" items="${destacados}">
            <%@ include file="_tarjeta-producto.jspf" %>
        </c:forEach>
    </div>

    <div class="seccion-titulo">
        <h2>Categorias</h2>
    </div>
    <div class="grid-categorias">
        <c:forEach var="categoria" items="${categorias}">
            <a class="tarjeta-categoria" href="${pageContext.request.contextPath}/tienda/menu?categoria=${categoria.id}">
                <span class="icono">&#9749;</span>
                ${categoria.nombre}
            </a>
        </c:forEach>
    </div>

    <div class="cta-tienda">
        <h2>&#191;Listo para pedir algo?</h2>
        <a class="boton" href="${pageContext.request.contextPath}/tienda/menu">Ver productos</a>
    </div>
</div>

<%@ include file="_footer.jspf" %>
</body>
</html>
