const { Router } = require('express');
const catalogo = require('../data/medicamentos.json');

const router = Router();

// GET /api/medicamentos?nombre=amox  -> busca por nombre o principio activo
router.get('/', (req, res) => {
  const nombre = (req.query.nombre || '').toString().trim().toLowerCase();
  if (!nombre) {
    return res.status(400).json({ error: 'Debe indicar el parametro "nombre"' });
  }
  const resultados = catalogo.filter(
    (m) => m.nombre.toLowerCase().includes(nombre) || m.principioActivo.toLowerCase().includes(nombre)
  );
  res.json(resultados);
});

// GET /api/medicamentos/:codigo
router.get('/:codigo', (req, res) => {
  const med = catalogo.find((m) => m.codigo === req.params.codigo);
  if (!med) {
    return res.status(404).json({ error: `Medicamento ${req.params.codigo} no existe` });
  }
  res.json(med);
});

module.exports = router;
