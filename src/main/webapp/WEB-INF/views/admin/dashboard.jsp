<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Cafe Don Bosco - Dashboard</title>
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
                <h2>&#161;Hola, ${usuario.nombre}! &#9749;</h2>
                <p>Todo listo para atender a nuestros clientes.</p>
            </div>
            <div class="usuario-actual">
                <span class="avatar">${usuario.nombre.substring(0,1)}</span>
                <div>
                    <strong>${usuario.nombre}</strong><br>
                    <small>Administrador</small>
                </div>
            </div>
        </div>

        <div class="fila-estadisticas">
            <div class="tarjeta-estadistica">
                <span class="titulo">Productos disponibles</span>
                <span class="valor">${resumen.productosDisponibles}</span>
            </div>
            <div class="tarjeta-estadistica">
                <span class="titulo">Ventas del dia</span>
                <span class="valor">$${resumen.ventasHoyTotalFormateado}</span>
                <span class="ayuda">${resumen.ventasHoyCantidad} ventas hoy</span>
            </div>
            <div class="tarjeta-estadistica">
                <span class="titulo">Total de ventas (mes)</span>
                <span class="valor">$${resumen.totalVentasMesFormateado}</span>
            </div>
        </div>

        <div class="hero-admin">
            <p class="etiqueta-superior" style="color:var(--color-cobre-claro); text-transform:uppercase; font-size:0.75rem;">Cafe Don Bosco</p>
            <h2>El mejor cafe, siempre contigo</h2>
            <p>Disfruta de nuestra seleccion de productos hechos con pasion y calidad.</p>
            <a class="boton" href="${pageContext.request.contextPath}/tienda">Ver tienda &rarr;</a>
        </div>

        <div class="diseno-dos-columnas">
            <div>
                <div class="panel">
                    <div class="panel-encabezado">
                        <h3>Ventas recientes</h3>
                        <a href="${pageContext.request.contextPath}/admin/historial-ventas">Ver historial &rarr;</a>
                    </div>
                    <c:choose>
                        <c:when test="${empty resumen.ventasRecientes}">
                            <p class="estado-vacio">Todavia no hay ventas registradas.</p>
                        </c:when>
                        <c:otherwise>
                            <table class="tabla">
                                <thead>
                                <tr>
                                    <th># Venta</th>
                                    <th>Fecha</th>
                                    <th>Origen</th>
                                    <th>Total</th>
                                    <th>Estado</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach var="venta" items="${resumen.ventasRecientes}">
                                    <tr>
                                        <td>#000${venta.id}</td>
                                        <td>${venta.fechaFormateada}</td>
                                        <td>${venta.tipoVenta} <c:if test="${not empty venta.nombreCliente}"> - ${venta.nombreCliente}</c:if></td>
                                        <td>$${venta.totalFormateado}</td>
                                        <td>${venta.estado}</td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </c:otherwise>
                    </c:choose>
                </div>

                <div class="panel">
                    <div class="panel-encabezado">
                        <h3>&#9733; Productos destacados</h3>
                        <a href="${pageContext.request.contextPath}/admin/productos">Ver todos &rarr;</a>
                    </div>
                    <div class="grid-productos-admin">
                        <c:forEach var="producto" items="${destacados}">
                            <div class="tarjeta-producto-admin">
                                <strong>${producto.nombre}</strong>
                                <span class="ayuda">${producto.categoriaNombre}</span>
                                <span class="precio">$${producto.precioFormateado}</span>
                                <c:choose>
                                    <c:when test="${producto.disponible}"><span class="disponible">Disponible</span></c:when>
                                    <c:otherwise><span class="no-disponible">Agotado</span></c:otherwise>
                                </c:choose>
                            </div>
                        </c:forEach>
                    </div>
                </div>
            </div>

            <div>
                <div class="panel">
                    <div class="panel-encabezado">
                        <h3>&#9889; Accesos rapidos</h3>
                    </div>
                    <div class="accesos-rapidos">
                        <a class="acceso-rapido destacado" href="${pageContext.request.contextPath}/admin/venta-nueva">
                            <strong>&#128722; Nueva venta</strong>
                            <span>Registrar una venta</span>
                        </a>
                        <a class="acceso-rapido" href="${pageContext.request.contextPath}/admin/productos">
                            <strong>&#9749; Ver productos</strong>
                            <span>Explorar catalogo</span>
                        </a>
                        <a class="acceso-rapido" href="${pageContext.request.contextPath}/admin/historial-ventas">
                            <strong>&#128337; Historial de ventas</strong>
                            <span>Consultar ventas anteriores</span>
                        </a>
                        <a class="acceso-rapido" href="${pageContext.request.contextPath}/logout">
                            <strong>&#8618; Cerrar sesion</strong>
                            <span>Salir del sistema</span>
                        </a>
                    </div>
                </div>

                <c:if test="${not empty resumen.productosStockBajo}">
                    <div class="panel">
                        <div class="panel-encabezado">
                            <h3>&#9888; Stock bajo</h3>
                        </div>
                        <c:forEach var="producto" items="${resumen.productosStockBajo}">
                            <div class="pos-producto">
                                <div class="info">
                                    <strong>${producto.nombre}</strong>
                                    <span>${producto.stock} unidades (minimo ${producto.stockMinimo})</span>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:if>
            </div>
        </div>
    </main>
</div>
</body>
</html>
