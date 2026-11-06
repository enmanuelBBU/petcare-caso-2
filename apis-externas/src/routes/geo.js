const { Router } = require('express');

const router = Router();

// GET /api/geo/ubicacion?lat=-36.82&lng=-73.01 -> URLs de mapa para mostrar en el front
router.get('/ubicacion', (req, res) => {
  const lat = Number(req.query.lat);
  const lng = Number(req.query.lng);
  if (Number.isNaN(lat) || Number.isNaN(lng)) {
    return res.status(400).json({ error: 'Parametros "lat" y "lng" numericos son obligatorios' });
  }
  const f = (n) => n.toFixed(6);
  const bbox = [f(lng - 0.005), f(lat - 0.003), f(lng + 0.005), f(lat + 0.003)].join('%2C');
  res.json({
    latitud: lat,
    longitud: lng,
    urlMapa: `https://www.openstreetmap.org/?mlat=${lat}&mlon=${lng}#map=17/${lat}/${lng}`,
    urlEmbed: `https://www.openstreetmap.org/export/embed.html?bbox=${bbox}&layer=mapnik&marker=${lat}%2C${lng}`,
  });
});

// GET /api/geo/geocodificar?direccion=Av. Collao 1202, Concepcion
// TODO: consultar Nominatim (https://nominatim.openstreetmap.org/search) u otra API de mapas
router.get('/geocodificar', (req, res) => {
  res.status(501).json({ error: 'No implementado aun' });
});

module.exports = router;
