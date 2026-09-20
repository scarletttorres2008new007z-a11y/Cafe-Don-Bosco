<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Cafe Don Bosco - Administrador</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/variables.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/base.css">
</head>
<body>
    <div class="pantalla-centrada">
        <div class="tarjeta-flotante">
            <h1>Cafe Don Bosco</h1>
            <p class="subtitulo-centrado">Panel de administrador</p>

            <c:if test="${not empty error}">
                <div class="mensaje-error">${error}</div>
            </c:if>

            <form method="post" action="${pageContext.request.contextPath}/login">
                <div class="campo">
                    <label for="correo">Correo</label>
                    <input type="email" id="correo" name="correo" required autofocus>
                </div>
                <div class="campo">
                    <label for="password">Contrasena</label>
                    <input type="password" id="password" name="password" required>
                </div>
                <button type="submit" class="boton" style="width:100%;">Iniciar sesion</button>
            </form>

            <p style="text-align:center; margin-top:18px; font-size:0.85rem;">
                <a href="${pageContext.request.contextPath}/">&larr; Volver al portal</a>
            </p>
        </div>
    </div>
</body>
</html>
