<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!doctype html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Panel administrativo | Cafe Don Bosco</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin-dashboard.css">
</head>
<body>
<div class="app">
    <aside class="sidebar">
        <div class="brand">
            <div class="brand-row"><span class="cup">&#9749;</span><strong>Cafe<br>Don Bosco</strong></div>
            <small>Buen cafe, mejores momentos</small>
        </div>
        <nav class="nav">
            <a class="active" href="${pageContext.request.contextPath}/admin/dashboard"><span class="ico">&#8962;</span>Inicio</a>
            <a href="${pageContext.request.contextPath}/admin/productos"><span class="ico">&#9749;</span>Productos</a>
            <a href="${pageContext.request.contextPath}/admin/venta-nueva"><span class="ico">&#128722;</span>Nueva venta</a>
            <a href="${pageContext.request.contextPath}/admin/historial-ventas"><span class="ico">&#9201;</span>Historial de ventas</a>
            <a href="${pageContext.request.contextPath}/logout"><span class="ico">&#8618;</span>Cerrar sesion</a>
        </nav>
        <div class="sidebar-foot">
            <div class="botanical">&#9749;</div>
            <b>Cafe Don Bosco</b><br>Sistema de mostrador<br>v1.0
        </div>
    </aside>

    <main class="main">
        <header class="topbar">
            <button type="button" class="menu" aria-label="Abrir menu">&#9776;</button>
            <div class="user">
                <span class="avatar">${usuario.nombre.substring(0,1)}</span>
                <span><b>${usuario.nombre}</b><br>Administrador</span>
            </div>
        </header>

        <div class="content">
            <div class="welcome">
                <h1>&#161;${saludo}, ${usuario.nombre}! &#9749;</h1>
                <p>Todo listo para atender a nuestros clientes.</p>
            </div>

            <div class="columns">
                <div>
                    <section class="stats">
                        <article class="stat">
                            <span class="stat-icon">&#9749;</span>
                            <div>
                                <small>Productos disponibles</small>
                                <b>${resumen.productosDisponibles}</b>
                                <c:choose>
                                    <c:when test="${not empty resumen.productosStockBajo}">
                                        <span class="warn">&#9888; ${resumen.productosStockBajo.size()} con stock bajo</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="up">Catalogo activo</span>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </article>
                        <article class="stat">
                            <span class="stat-icon">&#128722;</span>
                            <div>
                                <small>Ventas del dia</small>
                                <b>$${resumen.ventasHoyTotalFormateado}</b>
                                <span class="up">${resumen.ventasHoyCantidad} pedidos hoy</span>
                            </div>
                        </article>
                        <article class="stat">
                            <span class="stat-icon">&#9635;</span>
                            <div>
                                <small>Total de ventas</small>
                                <b>$${resumen.ingresosTotalesFormateado}</b>
                                <span class="up">$${resumen.totalVentasMesFormateado} este mes</span>
                            </div>
                        </article>
                    </section>

                    <section class="hero">
                        <em>Cafe Don Bosco</em>
                        <h2>El mejor cafe,<br>siempre contigo</h2>
                        <p>Disfruta de nuestra seleccion de productos<br>hechos con pasion y calidad.</p>
                        <a href="${pageContext.request.contextPath}/admin/productos">Ver catalogo &rarr;</a>
                    </section>

                    <section class="panel" id="productos">
                        <div class="panel-title">
                            <h3>&#9733; &nbsp; Productos destacados</h3>
                            <a href="${pageContext.request.contextPath}/admin/productos">Ver todos &rarr;</a>
                        </div>
                        <c:choose>
                            <c:when test="${empty destacados}">
                                <p style="font-size:11px;color:#777;">Todavia no hay productos activos.</p>
                            </c:when>
                            <c:otherwise>
                                <div class="products">
                                    <c:forEach var="producto" items="${destacados}">
                                        <article class="product">
                                            <c:choose>
                                                <c:when test="${fn:containsIgnoreCase(producto.nombre, 'americano')}">
                                                    <div class="pic" style="background-image:url('${pageContext.request.contextPath}/assets/img/americano.jpg')"></div>
                                                </c:when>
                                                <c:when test="${producto.categoriaNombre == 'Cafe'}">
                                                    <div class="pic" style="background-image:url('${pageContext.request.contextPath}/assets/img/capuchino.jpg')"></div>
                                                </c:when>
                                                <c:otherwise>
                                                    <div class="pic" style="background-image:url('${pageContext.request.contextPath}/assets/img/productos.jpg')"></div>
                                                </c:otherwise>
                                            </c:choose>
                                            <h4>${producto.nombre}</h4>
                                            <small>${producto.categoriaNombre}</small>
                                            <div class="price">$${producto.precioFormateado}</div>
                                            <c:choose>
                                                <c:when test="${producto.disponible}">
                                                    <div class="stock">&#9679; Disponible</div>
                                                </c:when>
                                                <c:otherwise>
                                                    <div class="stock" style="color:#b23b2f;">&#9679; Agotado</div>
                                                </c:otherwise>
                                            </c:choose>
                                            <a class="detail" href="${pageContext.request.contextPath}/admin/productos">Ver detalle</a>
                                        </article>
                                    </c:forEach>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </section>
                </div>

                <aside class="right">
                    <section class="panel sales" id="ventas">
                        <div class="panel-title">
                            <h3>&#9638; &nbsp; Ventas recientes</h3>
                            <a href="${pageContext.request.contextPath}/admin/historial-ventas">Ver historial &rarr;</a>
                        </div>
                        <c:choose>
                            <c:when test="${empty resumen.ventasRecientes}">
                                <p style="font-size:11px;color:#777;">Todavia no hay ventas registradas.</p>
                            </c:when>
                            <c:otherwise>
                                <table>
                                    <thead>
                                    <tr>
                                        <th># Venta</th>
                                        <th>Fecha</th>
                                        <th>Total</th>
                                        <th>Estado</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <c:forEach var="venta" items="${resumen.ventasRecientes}">
                                        <tr>
                                            <td><b>#000${venta.id}</b></td>
                                            <td>${venta.fechaFormateada}</td>
                                            <td>$${venta.totalFormateado}</td>
                                            <td>${venta.estado}</td>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>
                            </c:otherwise>
                        </c:choose>
                    </section>

                    <section class="panel">
                        <div class="panel-title"><h3>&#9889; &nbsp; Accesos rapidos</h3></div>
                        <div class="quick">
                            <a href="${pageContext.request.contextPath}/admin/venta-nueva">
                                <span class="ico">&#128722;</span>
                                <span><b>Nueva venta</b><small>Registrar una venta</small></span>
                            </a>
                            <a href="${pageContext.request.contextPath}/admin/productos">
                                <span class="ico">&#9635;</span>
                                <span><b>Ver productos</b><small>Explorar catalogo</small></span>
                            </a>
                            <a href="${pageContext.request.contextPath}/admin/historial-ventas">
                                <span class="ico">&#9201;</span>
                                <span><b>Historial de ventas</b><small>Consultar ventas anteriores</small></span>
                            </a>
                            <a href="${pageContext.request.contextPath}/logout">
                                <span class="ico">&#8618;</span>
                                <span><b>Cerrar sesion</b><small>Salir del sistema</small></span>
                            </a>
                        </div>
                    </section>

                    <c:if test="${not empty resumen.productosStockBajo}">
                        <section class="panel">
                            <div class="panel-title"><h3>&#9888; &nbsp; Stock bajo</h3></div>
                            <div class="lowstock">
                                <c:forEach var="producto" items="${resumen.productosStockBajo}">
                                    <div class="item">
                                        <b>${producto.nombre}</b>
                                        <span>${producto.stock} u. (min ${producto.stockMinimo})</span>
                                    </div>
                                </c:forEach>
                            </div>
                        </section>
                    </c:if>

                    <div class="signature">Cafe Don Bosco<small>Tradicion que se disfruta</small></div>
                </aside>
            </div>
        </div>
    </main>
</div>
<script src="${pageContext.request.contextPath}/assets/js/admin-dashboard.js"></script>
</body>
</html>
