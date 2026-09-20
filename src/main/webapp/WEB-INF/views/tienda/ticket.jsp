<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Cafe Don Bosco - Ticket</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/variables.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/base.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/tienda.css">
</head>
<body>
<%@ include file="_header.jspf" %>

<div class="contenedor-tienda">
    <c:choose>
        <c:when test="${not empty error}">
            <div class="mensaje-error">${error}</div>
        </c:when>
        <c:otherwise>
            <%@ include file="../_ticket-contenido.jspf" %>
            <div class="acciones-ticket no-imprimir">
                <button type="button" class="boton" onclick="window.print()">Imprimir / Descargar PDF</button>
                <a class="boton secundario" href="${pageContext.request.contextPath}/tienda">Volver al inicio</a>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<%@ include file="_footer.jspf" %>
</body>
</html>
