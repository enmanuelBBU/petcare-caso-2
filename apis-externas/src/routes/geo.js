const { Router } = require('express');

const router = Router();

/** Coordenadas + URL para abrir el mapa y URL para embeberlo (iframe) en el front. */
function urlsMapa(lat, lng) {
  const f = (n) => n.toFixed(6);
  const bbox = [f(lng - 0.005), f(lat - 0.003), f(lng + 0.005), f(lat + 0.003)].join('%2C');
  return {
    latitud: lat,
    longitud: lng,
    urlMapa: `https://www.openstreetmap.org/?mlat=${lat}&mlon=${lng}#map=17/${lat}/${lng}`,
    urlEmbed: `https://www.openstreetmap.org/export/embed.html?bbox=${bbox}&layer=mapnik&marker=${lat}%2C${lng}`,
  };
}

// GET /api/geo/ubicacion?lat=-36.82&lng=-73.01 -> URLs de mapa para mostrar en el front
router.get('/ubicacion', (req, res) => {
  const lat = Number(req.query.lat);
  const lng = Number(req.query.lng);
  if (Number.isNaN(lat) || Number.isNaN(lng)) {
    return res.status(400).json({ error: 'Parametros "lat" y "lng" numericos son obligatorios' });
  }
  res.json(urlsMapa(lat, lng));
});

// GET /api/geo/geocodificar?direccion=Av. Collao 1202, Concepcion
// Consulta Nominatim (OpenStreetMap) y devuelve coordenadas + URLs de mapa.
router.get('/geocodificar', async (req, res) => {
  const direccion = (req.query.direccion || '').trim();
  if (!direccion) {
    return res.status(400).json({ error: 'Parametro "direccion" es obligatorio' });
  }
  try {
    const url = `https://nominatim.openstreetmap.org/search?format=json&limit=1&countrycodes=cl&q=${encodeURIComponent(direccion)}`;
    // Nominatim exige un User-Agent que identifique a la aplicacion
    const resp = await fetch(url, {
      headers: { 'User-Agent': 'petcare-apis-externas/1.0', 'Accept-Language': 'es' },
      signal: AbortSignal.timeout(4000),
    });
    if (!resp.ok) {
      return res.status(502).json({ error: `Nominatim respondio ${resp.status}` });
    }
    const resultados = await resp.json();
    if (!resultados.length) {
      return res.status(404).json({ error: `No se encontro la direccion "${direccion}"` });
    }
    const lat = Number(resultados[0].lat);
    const lng = Number(resultados[0].lon);
    res.json({ direccion: resultados[0].display_name, ...urlsMapa(lat, lng) });
  } catch (e) {
    res.status(502).json({ error: `No se pudo consultar Nominatim: ${e.message}` });
  }
});

module.exports = router;
