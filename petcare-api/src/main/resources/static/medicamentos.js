// Busqueda de medicamentos (GET /api/medicamentos/buscar?nombre=, Spring -> API Node)
// y filas de prescripcion enviadas como medicamentos[i].campo.
(function () {
    var input = document.getElementById('buscar-med');
    var btnBuscar = document.getElementById('btn-buscar-med');
    var btnManual = document.getElementById('agregar-manual');
    var lista = document.getElementById('resultados-med');
    var error = document.getElementById('med-error');
    var filas = document.getElementById('filas-med');
    var sinMed = document.getElementById('sin-med');
    if (!input || !filas) return;

    var CAMPOS = ['codigoExterno', 'nombre', 'dosis', 'frecuencia', 'duracionDias', 'viaAdministracion'];

    function mostrarError(texto) {
        error.textContent = texto;
        error.hidden = !texto;
    }

    function actualizarVacio() {
        sinMed.hidden = filas.children.length > 0;
    }

    // Renombra name/id de cada fila para que los indices queden 0..n-1.
    function reindexar() {
        Array.prototype.forEach.call(filas.children, function (fila, i) {
            fila.querySelectorAll('input').forEach(function (el) {
                if (el.name) el.name = el.name.replace(/medicamentos\[\d+\]/, 'medicamentos[' + i + ']');
                if (el.id) el.id = el.id.replace(/medicamentos\d+/, 'medicamentos' + i);
            });
        });
        actualizarVacio();
    }

    function input_(i, campo, tipo, valor, placeholder) {
        var el = document.createElement('input');
        el.type = tipo;
        el.name = 'medicamentos[' + i + '].' + campo;
        if (valor != null) el.value = valor;
        if (placeholder) el.placeholder = placeholder;
        return el;
    }

    function celda(el) {
        var d = document.createElement('div');
        d.appendChild(el);
        return d;
    }

    function agregarFila(med) {
        var i = filas.children.length;
        var fila = document.createElement('div');
        fila.className = 'fila-med';

        var c0 = document.createElement('div');
        c0.appendChild(input_(i, 'codigoExterno', 'hidden', med ? med.codigo : ''));
        var nombre = input_(i, 'nombre', 'text', med ? med.nombre : '', 'Nombre');
        if (med) {
            nombre.readOnly = true;
            nombre.title = med.principioActivo + ' · ' + med.presentacion;
        }
        c0.appendChild(nombre);
        fila.appendChild(c0);

        fila.appendChild(celda(input_(i, 'dosis', 'text', '', '1 comp')));
        fila.appendChild(celda(input_(i, 'frecuencia', 'text', '', 'cada 12 h')));
        var dias = input_(i, 'duracionDias', 'number', '', '');
        dias.min = '1';
        fila.appendChild(celda(dias));
        fila.appendChild(celda(input_(i, 'viaAdministracion', 'text', '', 'Oral')));

        var quitar = document.createElement('button');
        quitar.type = 'button';
        quitar.className = 'btn btn-sec btn-chico quitar-med';
        quitar.setAttribute('aria-label', 'Quitar');
        quitar.innerHTML = '&times;';
        fila.appendChild(quitar);

        filas.appendChild(fila);
        actualizarVacio();
        (med ? fila.querySelector('[name$=".dosis"]') : nombre).focus();
    }

    function buscar() {
        var q = input.value.trim();
        lista.innerHTML = '';
        mostrarError('');
        if (q.length < 2) {
            mostrarError('Escriba al menos 2 letras.');
            return;
        }
        fetch('/api/medicamentos/buscar?nombre=' + encodeURIComponent(q))
            .then(function (r) {
                return r.json().catch(function () { return null; }).then(function (body) {
                    if (!r.ok) {
                        var msg = body && (body.mensaje || body.message);
                        throw new Error(msg || 'No se pudo consultar la API de medicamentos (HTTP ' + r.status + ')');
                    }
                    return body;
                });
            })
            .then(function (meds) {
                if (!meds || meds.length === 0) {
                    mostrarError('Sin resultados para "' + q + '". Puede agregarlo manualmente.');
                    return;
                }
                meds.forEach(function (m) {
                    var li = document.createElement('li');
                    li.tabIndex = 0;
                    var t = document.createElement('strong');
                    t.textContent = m.nombre;
                    var s = document.createElement('small');
                    s.textContent = m.principioActivo + ' · ' + m.presentacion;
                    li.appendChild(t);
                    li.appendChild(s);
                    var elegir = function () {
                        agregarFila(m);
                        lista.innerHTML = '';
                    };
                    li.addEventListener('click', elegir);
                    li.addEventListener('keydown', function (e) { if (e.key === 'Enter') { e.preventDefault(); elegir(); } });
                    lista.appendChild(li);
                });
            })
            .catch(function (e) {
                mostrarError(e.message + ' Puede agregar el medicamento manualmente.');
            });
    }

    btnBuscar.addEventListener('click', buscar);
    input.addEventListener('keydown', function (e) {
        if (e.key === 'Enter') { e.preventDefault(); buscar(); }
    });
    btnManual.addEventListener('click', function () { agregarFila(null); });
    filas.addEventListener('click', function (e) {
        var b = e.target.closest('.quitar-med');
        if (b) {
            b.closest('.fila-med').remove();
            reindexar();
        }
    });
    actualizarVacio();
})();
