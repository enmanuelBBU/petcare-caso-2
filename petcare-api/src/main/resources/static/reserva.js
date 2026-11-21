// Selector de horas del formulario de reserva.
// Consulta GET /api/veterinarios/{id}/disponibilidad?fecha= (API REST de Spring) y pinta un boton por hora.
(function () {
    var vet = document.getElementById('veterinarioId');
    var fecha = document.getElementById('fecha');
    var hora = document.getElementById('hora');
    var contenedor = document.getElementById('horas');
    var estado = document.getElementById('horas-estado');
    if (!vet || !fecha || !hora || !contenedor) return;

    function hhmm(t) { return String(t).slice(0, 5); }

    function mensaje(texto) {
        contenedor.innerHTML = '';
        estado.textContent = texto;
    }

    function marcarElegida() {
        var actual = hora.value ? hora.value.slice(0, 5) : '';
        contenedor.querySelectorAll('.hora-btn').forEach(function (b) {
            b.classList.toggle('elegida', b.dataset.hora === actual);
        });
    }

    function boton(h, ocupada) {
        var b = document.createElement('button');
        b.type = 'button';
        b.className = 'hora-btn';
        b.textContent = h;
        b.dataset.hora = h;
        if (ocupada) {
            b.disabled = true;
            b.title = 'Ocupada';
        } else {
            b.addEventListener('click', function () {
                hora.value = h;
                marcarElegida();
            });
        }
        return b;
    }

    function cargar() {
        if (!vet.value || !fecha.value) {
            mensaje('Elija veterinario y fecha para ver las horas disponibles.');
            return;
        }
        mensaje('Cargando disponibilidad…');
        fetch('/api/veterinarios/' + encodeURIComponent(vet.value) + '/disponibilidad?fecha=' + encodeURIComponent(fecha.value))
            .then(function (r) {
                if (!r.ok) throw new Error('HTTP ' + r.status);
                return r.json();
            })
            .then(function (d) {
                if (!d.bloques || d.bloques.length === 0) {
                    mensaje('El veterinario no atiende este día');
                    return;
                }
                var libres = (d.horasDisponibles || []).map(hhmm);
                var ocupadas = (d.horasOcupadas || []).map(hhmm);
                var todas = libres.concat(ocupadas).sort();
                contenedor.innerHTML = '';
                todas.forEach(function (h) {
                    contenedor.appendChild(boton(h, ocupadas.indexOf(h) !== -1));
                });
                estado.textContent = libres.length + ' horas libres · ' + ocupadas.length + ' ocupadas';
                marcarElegida();
            })
            .catch(function () {
                mensaje('No se pudo cargar la disponibilidad');
            });
    }

    vet.addEventListener('change', cargar);
    fecha.addEventListener('change', cargar);
    hora.addEventListener('input', marcarElegida);
    cargar();
})();
