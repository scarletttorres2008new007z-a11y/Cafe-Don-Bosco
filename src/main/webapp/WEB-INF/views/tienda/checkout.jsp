<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Cafe Don Bosco - Finalizar compra</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/variables.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/base.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/tienda.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/efectos.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@600;700&family=Plus+Jakarta+Sans:wght@400;500;600;700&display=swap">
</head>
<body>
<%@ include file="_header.jspf" %>

<div class="contenedor-tienda">
    <h2>Finalizar compra</h2>
    <p>Completa tus datos para procesar tu pedido.</p>

    <div class="pasos-checkout">
        <div class="paso activo"><span class="numero">1</span> Datos y pago</div>
        <div class="paso"><span class="numero">2</span> Confirmacion</div>
    </div>

    <c:if test="${not empty error}">
        <div class="mensaje-error">${error}</div>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/tienda/checkout">
        <div class="layout-checkout">
            <div class="tarjeta">
                <h3>Datos de envio</h3>
                <div class="campo">
                    <label for="nombreCompleto">Nombre completo *</label>
                    <input type="text" id="nombreCompleto" name="nombreCompleto" value="${datos.nombreCompleto}" required>
                </div>
                <div class="campo">
                    <label for="correo">Correo electronico *</label>
                    <input type="email" id="correo" name="correo" value="${datos.correo}" required>
                </div>
                <div class="campo">
                    <label for="telefono">Telefono *</label>
                    <input type="tel" id="telefono" name="telefono" value="${datos.telefono}" required>
                </div>
                <div class="campo">
                    <label>Tipo de entrega *</label>
                    <div class="opciones-radio">
                        <label>
                            <input type="radio" name="tipoEntrega" value="RECOGER" ${empty datos.tipoEntrega || datos.tipoEntrega == 'RECOGER' ? 'checked' : ''}>
                            Recoger en la cafeteria (envio gratis)
                        </label>
                        <label>
                            <input type="radio" name="tipoEntrega" value="DOMICILIO" ${datos.tipoEntrega == 'DOMICILIO' ? 'checked' : ''}>
                            Entrega a domicilio
                        </label>
                    </div>
                </div>
                <div class="campo">
                    <label for="direccion">Direccion</label>
                    <input type="text" id="direccion" name="direccion" value="${datos.direccion}" placeholder="Obligatoria solo para entrega a domicilio">
                </div>
                <div class="campo">
                    <label for="notas">Notas adicionales (opcional)</label>
                    <textarea id="notas" name="notas" placeholder="Ej. Sin azucar, sin hielo, etc.">${datos.notas}</textarea>
                </div>
            </div>

            <div>
                <div class="tarjeta" style="margin-bottom:20px;">
                    <h3>Metodo de pago</h3>
                    <div class="opciones-radio">
                        <label>
                            <input type="radio" name="metodoPago" value="TARJETA" ${empty datos.metodoPago || datos.metodoPago == 'TARJETA' ? 'checked' : ''}>
                            Tarjeta de credito / debito
                        </label>
                        <label>
                            <input type="radio" name="metodoPago" value="TRANSFERENCIA" ${datos.metodoPago == 'TRANSFERENCIA' ? 'checked' : ''}>
                            Transferencia bancaria
                        </label>
                        <label>
                            <input type="radio" name="metodoPago" value="CONTRA_ENTREGA" ${datos.metodoPago == 'CONTRA_ENTREGA' ? 'checked' : ''}>
                            Pago contra entrega
                        </label>
                    </div>
                </div>

                <div class="tarjeta resumen-pedido border-beam">
                    <h3>Resumen del pedido</h3>
                    <dl>
                        <dt>Subtotal</dt>
                        <dd>$${resumen.subtotalFormateado}</dd>
                        <dt>Envio</dt>
                        <dd>$${resumen.envioFormateado}</dd>
                        <dt class="total-final">Total</dt>
                        <dd class="total-final odometro">$${resumen.totalFormateado}</dd>
                    </dl>
                    <button type="submit" class="boton" style="width:100%; margin-top:14px;">Confirmar compra</button>
                </div>
            </div>
        </div>
    </form>
</div>

<%@ include file="_footer.jspf" %>
<script src="${pageContext.request.contextPath}/assets/js/efectos.js" defer></script>
</body>
</html>
