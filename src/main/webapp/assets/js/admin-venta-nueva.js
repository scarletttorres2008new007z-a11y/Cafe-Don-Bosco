(function () {
    function iniciarBusquedaYFiltro() {
        var input = document.getElementById('buscarProducto');
        var botonesCategoria = document.querySelectorAll('.cat');
        var tarjetas = document.querySelectorAll('.product');
        var sinResultados = document.getElementById('sinResultadosProductos');
        var categoriaActiva = 'Todas';

        function aplicarFiltro() {
            var texto = (input ? input.value : '').trim().toLowerCase();
            var visibles = 0;
            tarjetas.forEach(function (tarjeta) {
                var nombre = (tarjeta.dataset.nombre || '').toLowerCase();
                var sku = (tarjeta.dataset.sku || '').toLowerCase();
                var categoria = tarjeta.dataset.categoria || '';
                var coincideTexto = texto === '' || nombre.indexOf(texto) !== -1 || sku.indexOf(texto) !== -1;
                var coincideCategoria = categoriaActiva === 'Todas' || categoria === categoriaActiva;
                var visible = coincideTexto && coincideCategoria;
                tarjeta.style.display = visible ? '' : 'none';
                if (visible) {
                    visibles++;
                }
            });
            if (sinResultados) {
                sinResultados.style.display = visibles === 0 ? '' : 'none';
            }
        }

        if (input) {
            input.addEventListener('input', aplicarFiltro);
        }
        botonesCategoria.forEach(function (boton) {
            boton.addEventListener('click', function () {
                botonesCategoria.forEach(function (b) { b.classList.remove('active'); });
                boton.classList.add('active');
                categoriaActiva = boton.dataset.cat;
                aplicarFiltro();
            });
        });
    }

    function iniciarPagoYCambio() {
        var radiosPago = document.querySelectorAll('input[name="metodoPago"]');
        var filaEfectivo = document.getElementById('filaEfectivo');
        var montoRecibido = document.getElementById('montoRecibido');
        var totalElemento = document.getElementById('totalVenta');
        var cambioElemento = document.getElementById('montoCambio');

        function actualizarVisibilidadEfectivo() {
            var seleccionado = document.querySelector('input[name="metodoPago"]:checked');
            var esEfectivo = seleccionado && seleccionado.value === 'EFECTIVO';
            if (filaEfectivo) {
                filaEfectivo.style.display = esEfectivo ? '' : 'none';
            }
        }

        function actualizarCambio() {
            if (!totalElemento || !cambioElemento || !montoRecibido) {
                return;
            }
            var total = parseFloat(totalElemento.dataset.total || '0') || 0;
            var recibido = parseFloat(montoRecibido.value) || 0;
            var cambio = recibido - total;
            cambioElemento.textContent = '$' + Math.max(0, cambio).toFixed(2);
            cambioElemento.classList.toggle('negativo', cambio < 0);
        }

        radiosPago.forEach(function (radio) {
            radio.addEventListener('change', actualizarVisibilidadEfectivo);
        });
        if (montoRecibido) {
            montoRecibido.addEventListener('input', actualizarCambio);
        }
        actualizarVisibilidadEfectivo();
        actualizarCambio();
    }

    function iniciarPersonalizacion() {
        var contextPath = document.body.dataset.contextPath || '';
        var modal = document.getElementById('modalPersonalizarProducto');
        var titulo = document.getElementById('tituloPersonalizar');
        var cuerpo = document.getElementById('cuerpoPersonalizar');
        var mensajeError = document.getElementById('errorPersonalizar');
        var botonConfirmar = document.getElementById('botonConfirmarPersonalizar');
        var botonCancelar = document.getElementById('botonCancelarPersonalizar');
        var formsAgregar = document.querySelectorAll('.product form');
        if (!modal || !cuerpo || !botonConfirmar || formsAgregar.length === 0) {
            return;
        }

        var cacheGrupos = {};
        var formPendiente = null;

        function renderizarGrupos(grupos) {
            cuerpo.innerHTML = '';
            grupos.forEach(function (grupo) {
                var fieldset = document.createElement('fieldset');
                fieldset.className = 'grupo-personalizacion';
                fieldset.dataset.grupoId = grupo.id;
                fieldset.dataset.obligatorio = grupo.obligatorio ? '1' : '0';

                var leyenda = document.createElement('legend');
                leyenda.textContent = grupo.nombre + (grupo.obligatorio ? ' (obligatorio)' : ' (opcional)');
                fieldset.appendChild(leyenda);

                (grupo.opciones || []).filter(function (o) { return o.activo; }).forEach(function (opcion) {
                    var etiqueta = document.createElement('label');
                    etiqueta.className = 'opcion-personalizacion';
                    var input = document.createElement('input');
                    input.type = grupo.seleccionMultiple ? 'checkbox' : 'radio';
                    input.name = 'grupo-' + grupo.id;
                    input.value = opcion.id;
                    etiqueta.appendChild(input);
                    var extra = Number(opcion.precioAdicional) > 0 ? ' (+$' + Number(opcion.precioAdicional).toFixed(2) + ')' : '';
                    etiqueta.appendChild(document.createTextNode(' ' + opcion.nombre + extra));
                    fieldset.appendChild(etiqueta);
                });

                cuerpo.appendChild(fieldset);
            });
        }

        function abrirModal(grupos, nombreProducto, form) {
            formPendiente = form;
            if (titulo) {
                titulo.textContent = 'Personalizar ' + nombreProducto;
            }
            if (mensajeError) {
                mensajeError.hidden = true;
            }
            renderizarGrupos(grupos);
            modal.showModal();
        }

        function confirmarSeleccion() {
            var gruposEstado = {};
            cuerpo.querySelectorAll('fieldset').forEach(function (fieldset) {
                gruposEstado[fieldset.dataset.grupoId] = {
                    obligatorio: fieldset.dataset.obligatorio === '1',
                    marcado: fieldset.querySelector('input:checked') !== null
                };
            });
            var incompleto = Object.keys(gruposEstado).some(function (id) {
                return gruposEstado[id].obligatorio && !gruposEstado[id].marcado;
            });
            if (incompleto) {
                if (mensajeError) {
                    mensajeError.hidden = false;
                }
                return;
            }
            if (!formPendiente) {
                return;
            }
            cuerpo.querySelectorAll('input:checked').forEach(function (input) {
                var oculto = document.createElement('input');
                oculto.type = 'hidden';
                oculto.name = 'opcionIds';
                oculto.value = input.value;
                formPendiente.appendChild(oculto);
            });
            modal.close();
            formPendiente.submit();
        }

        botonConfirmar.addEventListener('click', confirmarSeleccion);
        if (botonCancelar) {
            botonCancelar.addEventListener('click', function () { modal.close(); });
        }

        formsAgregar.forEach(function (form) {
            form.addEventListener('submit', function (evento) {
                var campoProducto = form.querySelector('input[name="productoId"]');
                if (!campoProducto) {
                    return;
                }
                var productoId = campoProducto.value;
                var nombreProducto = form.closest('.product') ? form.closest('.product').dataset.nombre : '';

                if (cacheGrupos[productoId]) {
                    if (cacheGrupos[productoId].length > 0) {
                        evento.preventDefault();
                        abrirModal(cacheGrupos[productoId], nombreProducto, form);
                    }
                    return;
                }

                evento.preventDefault();
                fetch(contextPath + '/api/productos/' + productoId + '/opciones', { headers: { Accept: 'application/json' } })
                    .then(function (respuesta) { return respuesta.json(); })
                    .then(function (json) {
                        var grupos = (json.datos || []).filter(function (g) { return g.activo; });
                        cacheGrupos[productoId] = grupos;
                        if (grupos.length === 0) {
                            form.submit();
                        } else {
                            abrirModal(grupos, nombreProducto, form);
                        }
                    })
                    .catch(function () {
                        // Si la consulta de opciones falla, se envia el
                        // formulario igual: el backend sigue validando lo
                        // obligatorio antes de registrar la venta.
                        form.submit();
                    });
            });
        });
    }

    function iniciarConfirmacionCancelar() {
        var boton = document.getElementById('botonCancelarVenta');
        var modal = document.getElementById('modalCancelarVenta');
        var botonConfirmar = document.getElementById('botonConfirmarCancelar');
        var botonCerrar = document.getElementById('botonCerrarCancelar');
        var formVaciar = document.getElementById('formVaciarVenta');
        if (!boton || !modal || !botonConfirmar || !formVaciar) {
            return;
        }
        boton.addEventListener('click', function () { modal.showModal(); });
        botonCerrar.addEventListener('click', function () { modal.close(); });
        botonConfirmar.addEventListener('click', function () {
            modal.close();
            formVaciar.submit();
        });
    }

    document.addEventListener('DOMContentLoaded', function () {
        iniciarBusquedaYFiltro();
        iniciarPagoYCambio();
        iniciarPersonalizacion();
        iniciarConfirmacionCancelar();
    });
})();
