/**
 * CAPA 1: motor de efectos visuales y motion UX de /tienda/*.
 * JavaScript Vanilla (ES6+), sin dependencias externas. Cada modulo se
 * inicializa por separado y con try/catch propio: si uno falla, el resto
 * de la pagina (formularios, navegacion) sigue funcionando igual.
 */
(function () {
    'use strict';

    var ES_PUNTERO_TOSCO = window.matchMedia('(pointer: coarse)').matches;

    // ---------- Parallax 3D de fondo ----------
    function iniciarParallax() {
        var elementos = document.querySelectorAll('.bg-coffee-parallax');
        if (!elementos.length) {
            return;
        }
        var actualizando = false;

        function actualizar() {
            var scrollY = window.scrollY || 0;
            elementos.forEach(function (el) {
                var traslado = scrollY * 0.25;
                var rotX = Math.min(scrollY * 0.05, 25);
                var rotZ = scrollY * 0.02;
                el.style.transform = 'translateY(' + traslado + 'px) rotateX(' + rotX + 'deg) rotateZ(' + rotZ + 'deg)';
            });
            actualizando = false;
        }

        window.addEventListener('scroll', function () {
            if (!actualizando) {
                actualizando = true;
                requestAnimationFrame(actualizar);
            }
        }, { passive: true });

        actualizar();
    }

    // ---------- Tarjetas: spotlight + 3D tilt ----------
    function iniciarSpotlightYTilt() {
        if (ES_PUNTERO_TOSCO) {
            return;
        }
        var tarjetas = document.querySelectorAll('.tarjeta-producto, .tarjeta-categoria');
        tarjetas.forEach(function (tarjeta) {
            tarjeta.addEventListener('mousemove', function (evento) {
                var rect = tarjeta.getBoundingClientRect();
                var x = evento.clientX - rect.left;
                var y = evento.clientY - rect.top;

                tarjeta.style.setProperty('--mouse-x', (x / rect.width) * 100 + '%');
                tarjeta.style.setProperty('--mouse-y', (y / rect.height) * 100 + '%');

                var tiltX = ((y / rect.height) - 0.5) * -8;
                var tiltY = ((x / rect.width) - 0.5) * 8;
                tarjeta.style.setProperty('--tilt-x', tiltX.toFixed(2) + 'deg');
                tarjeta.style.setProperty('--tilt-y', tiltY.toFixed(2) + 'deg');

                tarjeta.classList.add('spotlight-activo', 'tilt-activo');
            });

            tarjeta.addEventListener('mouseleave', function () {
                tarjeta.classList.remove('spotlight-activo', 'tilt-activo');
                tarjeta.style.setProperty('--tilt-x', '0deg');
                tarjeta.style.setProperty('--tilt-y', '0deg');
            });
        });
    }

    // ---------- Cursor magnetico ----------
    function iniciarCursorMagnetico() {
        if (ES_PUNTERO_TOSCO) {
            return;
        }
        var cursor = document.createElement('div');
        cursor.className = 'cursor-magnetico';
        document.body.appendChild(cursor);

        var x = window.innerWidth / 2;
        var y = window.innerHeight / 2;
        var cx = x;
        var cy = y;

        document.addEventListener('mousemove', function (evento) {
            x = evento.clientX;
            y = evento.clientY;
            cursor.classList.add('cursor-listo');
            var sobreBoton = evento.target.closest ? evento.target.closest('.boton, .carrito-boton') : null;
            cursor.classList.toggle('cursor-sobre-boton', !!sobreBoton);
        });

        document.addEventListener('mouseleave', function () {
            cursor.classList.remove('cursor-listo');
        });

        function animar() {
            cx += (x - cx) * 0.2;
            cy += (y - cy) * 0.2;
            cursor.style.transform = 'translate(' + cx + 'px, ' + cy + 'px) translate(-50%, -50%)';
            requestAnimationFrame(animar);
        }
        requestAnimationFrame(animar);
    }

    // ---------- Toast notifications (estilo espresso) ----------
    var toastContenedor = null;

    function obtenerContenedorToast() {
        if (!toastContenedor) {
            toastContenedor = document.createElement('div');
            toastContenedor.className = 'toast-contenedor';
            document.body.appendChild(toastContenedor);
        }
        return toastContenedor;
    }

    function mostrarToast(mensaje) {
        var contenedor = obtenerContenedorToast();
        var toast = document.createElement('div');
        toast.className = 'toast-espresso';

        var texto = document.createElement('span');
        texto.textContent = mensaje;

        var barra = document.createElement('div');
        barra.className = 'toast-barra';

        toast.appendChild(texto);
        toast.appendChild(barra);
        contenedor.appendChild(toast);

        var quitar = function () {
            if (toast.parentNode) {
                toast.parentNode.removeChild(toast);
            }
        };
        toast.addEventListener('click', quitar);
        setTimeout(quitar, 3000);
    }

    // ---------- Odometro: cuenta desde 0 hasta el valor real ya renderizado ----------
    function iniciarOdometros() {
        var elementos = document.querySelectorAll('.odometro');
        elementos.forEach(function (el) {
            var textoOriginal = el.textContent.trim();
            var coincidencia = textoOriginal.match(/\d[\d,]*\.?\d*/);
            if (!coincidencia) {
                return;
            }
            var crudo = coincidencia[0];
            var valorFinal = parseFloat(crudo.replace(/,/g, ''));
            if (isNaN(valorFinal)) {
                return;
            }
            var decimales = crudo.indexOf('.') >= 0 ? (crudo.split('.')[1] || '').length : 0;
            var prefijo = textoOriginal.slice(0, coincidencia.index);
            var sufijo = textoOriginal.slice(coincidencia.index + crudo.length);
            var duracionMs = 700;
            var inicio = null;

            function paso(marca) {
                if (inicio === null) {
                    inicio = marca;
                }
                var progreso = Math.min((marca - inicio) / duracionMs, 1);
                var facilitado = 1 - Math.pow(1 - progreso, 3);
                el.textContent = prefijo + (valorFinal * facilitado).toFixed(decimales) + sufijo;
                if (progreso < 1) {
                    requestAnimationFrame(paso);
                } else {
                    el.textContent = textoOriginal;
                }
            }
            requestAnimationFrame(paso);
        });
    }

    // ---------- Fly-to-cart ----------
    function iniciarFlyToCart() {
        var formularios = document.querySelectorAll('form.form-agregar-carrito');
        var iconoCarrito = document.querySelector('.carrito-boton');

        formularios.forEach(function (form) {
            form.addEventListener('submit', function (evento) {
                if (form.dataset.volando === '1') {
                    evento.preventDefault();
                    return;
                }
                if (!iconoCarrito) {
                    return;
                }
                var tarjeta = form.closest('.tarjeta-producto') || form.closest('.detalle-layout') || form.parentElement;
                var origenEl = tarjeta ? (tarjeta.querySelector('.imagen-producto') || tarjeta.querySelector('.detalle-imagen')) : null;
                if (!origenEl) {
                    return;
                }

                evento.preventDefault();
                form.dataset.volando = '1';
                var boton = form.querySelector('button[type="submit"]');
                if (boton) {
                    boton.disabled = true;
                }

                var rectOrigen = origenEl.getBoundingClientRect();
                var rectDestino = iconoCarrito.getBoundingClientRect();

                var clon = document.createElement('div');
                clon.className = 'fly-item';
                clon.textContent = (origenEl.textContent || '').trim() || String.fromCodePoint(9749);
                clon.style.left = rectOrigen.left + 'px';
                clon.style.top = rectOrigen.top + 'px';
                clon.style.width = rectOrigen.width + 'px';
                clon.style.height = rectOrigen.height + 'px';
                document.body.appendChild(clon);

                requestAnimationFrame(function () {
                    requestAnimationFrame(function () {
                        var deltaX = (rectDestino.left + rectDestino.width / 2) - (rectOrigen.left + rectOrigen.width / 2);
                        var deltaY = (rectDestino.top + rectDestino.height / 2) - (rectOrigen.top + rectOrigen.height / 2);
                        clon.style.transform = 'translate(' + deltaX + 'px, ' + deltaY + 'px) scale(0.15)';
                        clon.classList.add('fly-en-vuelo');
                    });
                });

                try {
                    sessionStorage.setItem('cdb_pulso_carrito', '1');
                } catch (almacenamientoNoDisponible) {
                    // Sin sessionStorage (modo privado, etc.): se omite solo el pulso del badge tras recargar.
                }

                mostrarToast('Se agrego al carrito');

                setTimeout(function () {
                    if (clon.parentNode) {
                        clon.parentNode.removeChild(clon);
                    }
                    form.submit();
                }, 550);
            });
        });
    }

    // ---------- Pulso del badge tras la recarga real ----------
    function iniciarPulsoBadgeSiCorresponde() {
        var pulsar = false;
        try {
            pulsar = sessionStorage.getItem('cdb_pulso_carrito') === '1';
            if (pulsar) {
                sessionStorage.removeItem('cdb_pulso_carrito');
            }
        } catch (almacenamientoNoDisponible) {
            return;
        }
        if (!pulsar) {
            return;
        }
        var badge = document.querySelector('.carrito-boton .contador');
        if (!badge) {
            return;
        }
        badge.classList.add('cart-badge-bounce');
        badge.addEventListener('animationend', function () {
            badge.classList.remove('cart-badge-bounce');
        }, { once: true });
    }

    document.addEventListener('DOMContentLoaded', function () {
        var modulos = [
            iniciarParallax,
            iniciarSpotlightYTilt,
            iniciarCursorMagnetico,
            iniciarOdometros,
            iniciarFlyToCart,
            iniciarPulsoBadgeSiCorresponde
        ];
        modulos.forEach(function (modulo) {
            try {
                modulo();
            } catch (error) {
                console.error('[efectos] fallo al iniciar ' + modulo.name, error);
            }
        });
    });
})();
