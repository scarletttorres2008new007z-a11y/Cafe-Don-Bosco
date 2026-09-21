<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Cafe Don Bosco - Administrador</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@600;700&family=Caveat:wght@600&family=Poppins:wght@400;500;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/variables.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/base.css">
    <style>
        * { box-sizing: border-box; }

        body.login-body {
            margin: 0;
            font-family: 'Poppins', var(--fuente-interfaz);
        }

        .login-pantalla {
            min-height: 100vh;
            display: flex;
            background: #17110d;
        }

        /* ---------- Panel izquierdo ---------- */
        .login-izquierda {
            position: relative;
            flex: 1.15;
            display: flex;
            flex-direction: column;
            justify-content: center;
            padding: 60px 70px;
            overflow: hidden;
            color: #fff;
            background:
                radial-gradient(ellipse 700px 500px at 15% 15%, rgba(180, 120, 60, 0.28), transparent 60%),
                linear-gradient(150deg, #1c130d 0%, #241a12 45%, #100b08 100%);
        }

        .blob {
            position: absolute;
            width: 220px;
            height: 220px;
            opacity: 0.55;
            color: #a9743f;
            pointer-events: none;
        }

        .blob-superior { top: -40px; left: -50px; transform: rotate(-10deg); }
        .blob-inferior { bottom: -50px; right: -40px; transform: rotate(165deg); color: #8a5a30; }

        .login-izquierda .contenido-marca { position: relative; z-index: 1; }

        .login-izquierda .marca-login {
            display: flex;
            align-items: center;
            gap: 18px;
            margin-bottom: 14px;
        }

        .login-izquierda .marca-login svg {
            width: 58px;
            height: 58px;
            color: #d9a765;
            flex-shrink: 0;
        }

        .login-izquierda .marca-login h1 {
            font-family: 'Playfair Display', var(--fuente-marca);
            font-weight: 700;
            font-size: 2.6rem;
            line-height: 1.05;
            margin: 0;
            color: #fff;
        }

        .login-izquierda .lema {
            font-size: 1.05rem;
            color: #d9b98d;
            margin: 0 0 34px;
            padding-bottom: 22px;
            border-bottom: 1px solid rgba(217, 185, 141, 0.35);
            display: inline-block;
        }

        .login-izquierda .frase-script {
            font-family: 'Caveat', cursive;
            font-size: 1.9rem;
            color: #f4ead9;
            margin: 0 0 40px;
            line-height: 1.25;
        }

        .taza-decorativa {
            position: relative;
            z-index: 1;
            width: 260px;
            padding: 22px;
            border-radius: 16px;
            background: rgba(255, 255, 255, 0.05);
            border: 1px solid rgba(255, 255, 255, 0.12);
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .taza-decorativa svg { width: 130px; height: 130px; color: #d9a765; }

        /* ---------- Panel derecho ---------- */
        .login-derecha {
            flex: 1;
            min-width: 420px;
            display: flex;
            align-items: center;
            justify-content: center;
            background: #17110d;
            padding: 40px;
        }

        .tarjeta-login {
            width: 100%;
            max-width: 400px;
            background: var(--color-crema);
            border-radius: 20px;
            padding: 44px 38px 34px;
            box-shadow: 0 24px 60px rgba(0, 0, 0, 0.35);
        }

        .tarjeta-login .encabezado-tarjeta {
            text-align: center;
            margin-bottom: 22px;
        }

        .tarjeta-login .encabezado-tarjeta svg {
            width: 46px;
            height: 46px;
            color: var(--color-cobre);
            margin-bottom: 8px;
        }

        .tarjeta-login .encabezado-tarjeta h2 {
            font-family: 'Playfair Display', var(--fuente-marca);
            font-size: 1.5rem;
            margin: 0;
            color: var(--color-cafe);
        }

        .tarjeta-login .encabezado-tarjeta .subtitulo {
            font-size: 0.85rem;
            color: #8a7a6a;
            margin: 2px 0 0;
        }

        .tarjeta-login .separador {
            width: 46px;
            height: 3px;
            background: var(--color-cobre-claro);
            border: none;
            margin: 18px auto;
            border-radius: 2px;
        }

        .tarjeta-login .bienvenida {
            text-align: center;
            margin-bottom: 22px;
        }

        .tarjeta-login .bienvenida h3 {
            font-size: 1.3rem;
            margin: 0 0 4px;
            color: var(--color-cafe);
        }

        .tarjeta-login .bienvenida p {
            font-size: 0.85rem;
            color: #8a7a6a;
            margin: 0;
        }

        .campo-icono {
            position: relative;
            margin-bottom: 16px;
        }

        .campo-icono svg.icono-campo {
            position: absolute;
            left: 16px;
            top: 50%;
            transform: translateY(-50%);
            width: 18px;
            height: 18px;
            color: #8a7a6a;
            pointer-events: none;
        }

        .campo-icono input {
            width: 100%;
            padding: 13px 16px 13px 46px;
            border-radius: 12px;
            border: 1px solid var(--color-borde);
            background: #fff;
            font-size: 0.92rem;
            font-family: inherit;
            color: var(--color-cafe);
        }

        .campo-icono input:focus {
            outline: none;
            border-color: var(--color-cobre-claro);
            box-shadow: 0 0 0 3px rgba(201, 138, 75, 0.18);
        }

        .campo-icono .alternar-clave {
            position: absolute;
            right: 14px;
            top: 50%;
            transform: translateY(-50%);
            width: 18px;
            height: 18px;
            color: #8a7a6a;
            background: none;
            border: none;
            padding: 0;
            cursor: pointer;
        }

        .boton-login {
            width: 100%;
            padding: 14px;
            margin-top: 6px;
            border: none;
            border-radius: 12px;
            background: var(--color-cafe);
            color: #fff;
            font-family: 'Poppins', var(--fuente-interfaz);
            font-weight: 600;
            font-size: 0.95rem;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 8px;
            cursor: pointer;
            transition: background 0.2s ease;
        }

        .boton-login:hover { background: var(--color-cafe-oscuro); }

        .divisor-marca {
            display: flex;
            align-items: center;
            gap: 12px;
            margin: 26px 0 14px;
            color: #8a7a6a;
            font-size: 0.78rem;
        }

        .divisor-marca::before,
        .divisor-marca::after {
            content: "";
            flex: 1;
            height: 1px;
            background: var(--color-borde);
        }

        .frase-final {
            text-align: center;
            font-size: 0.82rem;
            color: var(--color-cobre);
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 6px;
        }

        .volver-portal {
            display: block;
            text-align: center;
            margin-top: 16px;
            font-size: 0.78rem;
            color: #8a7a6a;
        }

        @media (max-width: 900px) {
            .login-izquierda { display: none; }
            .login-derecha { min-width: 0; width: 100%; }
        }
    </style>
</head>
<body class="login-body">
<div class="login-pantalla">
    <div class="login-izquierda">
        <svg class="blob blob-superior" viewBox="0 0 200 200" fill="currentColor">
            <path d="M45,-58C60,-49,74,-36,79,-20C83,-4,78,15,68,31C58,47,42,60,24,66C6,72,-14,71,-33,63C-53,55,-71,40,-79,20C-86,0,-83,-25,-70,-43C-57,-62,-33,-74,-9,-73C15,-72,30,-67,45,-58Z" transform="translate(100 100)"/>
        </svg>
        <svg class="blob blob-inferior" viewBox="0 0 200 200" fill="currentColor">
            <path d="M45,-58C60,-49,74,-36,79,-20C83,-4,78,15,68,31C58,47,42,60,24,66C6,72,-14,71,-33,63C-53,55,-71,40,-79,20C-86,0,-83,-25,-70,-43C-57,-62,-33,-74,-9,-73C15,-72,30,-67,45,-58Z" transform="translate(100 100)"/>
        </svg>

        <div class="contenido-marca">
            <div class="marca-login">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.4">
                    <path d="M4 8h13v6a5 5 0 0 1-5 5H9a5 5 0 0 1-5-5V8z"/>
                    <path d="M17 9h1.5a2.5 2.5 0 0 1 0 5H17"/>
                    <path d="M7 5c0-1 .8-1 .8-2M11 5c0-1 .8-1 .8-2M15 5c0-1 .8-1 .8-2" stroke-linecap="round"/>
                </svg>
                <h1>Cafe<br>Don Bosco</h1>
            </div>
            <p class="lema">Tradicion que se disfruta</p>
            <p class="frase-script">Un buen cafe<br>comienza aqui</p>

            <div class="taza-decorativa">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.1">
                    <ellipse cx="10" cy="16" rx="7" ry="2.6"/>
                    <path d="M3 16V9a7 7 0 0 1 14 0v7"/>
                    <path d="M17 10h1.8a2.6 2.6 0 0 1 0 5.2H17"/>
                    <path d="M7.5 12.5c1-1 2-.2 3-1.2s0-2.3 1-3.3" stroke-linecap="round"/>
                </svg>
            </div>
        </div>
    </div>

    <div class="login-derecha">
        <div class="tarjeta-login">
            <div class="encabezado-tarjeta">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.4" style="display:block;margin:0 auto 8px;">
                    <path d="M4 8h13v6a5 5 0 0 1-5 5H9a5 5 0 0 1-5-5V8z"/>
                    <path d="M17 9h1.5a2.5 2.5 0 0 1 0 5H17"/>
                    <path d="M7 5c0-1 .8-1 .8-2M11 5c0-1 .8-1 .8-2M15 5c0-1 .8-1 .8-2" stroke-linecap="round"/>
                </svg>
                <h2>Cafe Don Bosco</h2>
                <p class="subtitulo">Sistema de mostrador</p>
            </div>

            <hr class="separador">

            <div class="bienvenida">
                <h3>Bienvenido</h3>
                <p>Ingresa a tu cuenta para continuar</p>
            </div>

            <c:if test="${not empty error}">
                <div class="mensaje-error">${error}</div>
            </c:if>

            <form method="post" action="${pageContext.request.contextPath}/login">
                <div class="campo-icono">
                    <svg class="icono-campo" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6">
                        <circle cx="12" cy="8" r="3.4"/>
                        <path d="M4.5 20c0-4 3.4-6.8 7.5-6.8s7.5 2.8 7.5 6.8" stroke-linecap="round"/>
                    </svg>
                    <input type="email" id="correo" name="correo" placeholder="Usuario" required autofocus>
                </div>
                <div class="campo-icono">
                    <svg class="icono-campo" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6">
                        <rect x="5" y="10.5" width="14" height="9.5" rx="2"/>
                        <path d="M8 10.5V7.5a4 4 0 0 1 8 0v3" stroke-linecap="round"/>
                    </svg>
                    <input type="password" id="password" name="password" placeholder="Contrasena" required>
                    <button type="button" class="alternar-clave" onclick="alternarVisibilidadClave()" aria-label="Mostrar u ocultar contrasena">
                        <svg id="icono-ojo" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" width="18" height="18">
                            <path d="M1.5 12S5 5.5 12 5.5 22.5 12 22.5 12 19 18.5 12 18.5 1.5 12 1.5 12z"/>
                            <circle cx="12" cy="12" r="2.6"/>
                        </svg>
                    </button>
                </div>

                <button type="submit" class="boton-login">
                    Iniciar sesion
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16">
                        <path d="M5 12h14M13 6l6 6-6 6" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                </button>
            </form>

            <div class="divisor-marca">Cafe Don Bosco</div>
            <p class="frase-final">&#9749; Mas que cafe, es comunidad</p>
            <a class="volver-portal" href="${pageContext.request.contextPath}/">&larr; Volver al portal</a>
        </div>
    </div>
</div>

<script>
    function alternarVisibilidadClave() {
        var campo = document.getElementById('password');
        campo.type = campo.type === 'password' ? 'text' : 'password';
    }
</script>
</body>
</html>
