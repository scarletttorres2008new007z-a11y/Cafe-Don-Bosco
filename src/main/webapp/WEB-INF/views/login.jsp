<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Cafe Don Bosco - Iniciar sesion</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/estilo.css">
</head>
<body>
    <div class="login-envoltorio">
        <div class="login-tarjeta">
            <h1>Cafe Don Bosco</h1>
            <p style="text-align:center; margin-top:-8px; color:#6f4518;">Sistema de mostrador</p>

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
        </div>
    </div>
</body>
</html>
