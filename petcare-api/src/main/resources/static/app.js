// Confirmaciones de formularios y cierre de toasts (comun a todas las vistas).
document.addEventListener('submit', function (e) {
    var form = e.target;
    var msg = form.getAttribute && form.getAttribute('data-confirm');
    if (msg && !window.confirm(msg)) {
        e.preventDefault();
    }
});

document.addEventListener('click', function (e) {
    var btn = e.target.closest('.toast-cerrar');
    if (btn) {
        btn.closest('.toast').hidden = true;
    }
});

document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('.toast[data-autocierre]').forEach(function (t) {
        setTimeout(function () { t.hidden = true; }, parseInt(t.getAttribute('data-autocierre'), 10));
    });
});
