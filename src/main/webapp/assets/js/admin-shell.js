const menu=document.querySelector('.menu'),side=document.querySelector('.sidebar');
menu.addEventListener('click',()=>side.classList.toggle('open'));
document.addEventListener('click',e=>{if(innerWidth<=700&&!side.contains(e.target)&&!menu.contains(e.target))side.classList.remove('open')});

(function () {
    var boton = document.getElementById('botonCerrarSesion');
    var modal = document.getElementById('modalCerrarSesion');
    var botonCancelar = document.getElementById('botonCancelarCerrarSesion');
    var botonConfirmar = document.getElementById('botonConfirmarCerrarSesion');
    if (!boton || !modal || !botonConfirmar) {
        return;
    }
    boton.addEventListener('click', function () { modal.showModal(); });
    if (botonCancelar) {
        botonCancelar.addEventListener('click', function () { modal.close(); });
    }
    botonConfirmar.addEventListener('click', function () {
        window.location.href = (modal.dataset.contextPath || '') + '/logout';
    });
})();
