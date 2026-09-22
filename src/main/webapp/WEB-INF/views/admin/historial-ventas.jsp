<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!doctype html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Cafe Don Bosco - Historial de ventas</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin-shell.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin-historial.css">
</head>
<body>
<div class="app">
    <%@ include file="_sidebar.jspf" %>

    <main class="main">
        <%@ include file="_topbar.jspf" %>

        <div class="content">
            <div class="encabezado-productos">
                <div>
                    <h1>Historial de ventas</h1>
                    <p>Ventas presenciales y pedidos web, todos en un mismo lugar.</p>
                </div>
            </div>

            <div class="panel">
                <c:choose>
                    <c:when test="${empty ventas}">
                        <p class="estado-vacio">Todavia no hay ventas registradas.</p>
                    </c:when>
                    <c:otherwise>
                        <table class="tabla-productos">
                            <thead>
                            <tr>
                                <th># Venta</th>
                                <th>Fecha</th>
                                <th>Hora</th>
                                <th>Origen</th>
                                <th>Cliente</th>
                                <th>Total</th>
                                <th>Estado</th>
                                <th></th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="venta" items="${ventas}">
                                <tr>
                                    <td>#000${venta.id}</td>
                                    <td>${venta.fechaFormateada}</td>
                                    <td>${venta.horaFormateada}</td>
                                    <td>${venta.tipoVenta}</td>
                                    <td>${empty venta.nombreCliente ? 'Mostrador' : fn:escapeXml(venta.nombreCliente)}</td>
                                    <td>$${venta.totalFormateado}</td>
                                    <td>${venta.estado}</td>
                                    <td><a href="${pageContext.request.contextPath}/admin/ticket?id=${venta.id}">Ver ticket</a></td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </main>
</div>
<script src="${pageContext.request.contextPath}/assets/js/admin-shell.js"></script>
</body>
</html>
