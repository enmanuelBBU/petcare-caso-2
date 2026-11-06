const express = require('express');
const cors = require('cors');

const medicamentos = require('./routes/medicamentos');
const geo = require('./routes/geo');
const notificaciones = require('./routes/notificaciones');

const app = express();
const PORT = process.env.PORT || 4000;
// 0.0.0.0 para aceptar conexiones desde otros equipos de la red
const HOST = process.env.HOST || '0.0.0.0';

app.use(cors());
app.use(express.json());

app.get('/api/estado', (req, res) => {
  res.json({ servicio: 'petcare-apis-externas', estado: 'OK', hora: new Date().toISOString() });
});

app.use('/api/medicamentos', medicamentos);
app.use('/api/geo', geo);
app.use('/api/notificaciones', notificaciones);

app.use((req, res) => res.status(404).json({ error: 'Ruta no encontrada', path: req.path }));

app.listen(PORT, HOST, () => {
  console.log(`APIs externas PetCare escuchando en http://${HOST}:${PORT}`);
});
