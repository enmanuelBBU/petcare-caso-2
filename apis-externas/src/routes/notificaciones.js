const { Router } = require('express');
const { randomUUID } = require('crypto');

const router = Router();

// POST /api/notificaciones/email  body: { para, asunto, mensaje }
// Por ahora simula el envio (lo registra en consola).
// TODO: enviar el correo real con nodemailer (SMTP de Gmail/Mailtrap) usando variables de entorno.
router.post('/email', (req, res) => {
  const { para, asunto, mensaje } = req.body || {};
  if (!para || !mensaje) {
    return res.status(400).json({ error: 'Campos "para" y "mensaje" son obligatorios' });
  }
  const id = randomUUID();
  console.log(`[EMAIL ${id}] para=${para} asunto="${asunto || ''}"\n${mensaje}`);
  res.status(202).json({ id, canal: 'EMAIL', estado: 'ENVIADA', fecha: new Date().toISOString() });
});

// POST /api/notificaciones/sms  body: { para, mensaje }
// TODO: integrar proveedor de SMS/WhatsApp (ej. Twilio)
router.post('/sms', (req, res) => {
  const { para, mensaje } = req.body || {};
  if (!para || !mensaje) {
    return res.status(400).json({ error: 'Campos "para" y "mensaje" son obligatorios' });
  }
  const id = randomUUID();
  console.log(`[SMS ${id}] para=${para}\n${mensaje}`);
  res.status(202).json({ id, canal: 'SMS', estado: 'ENVIADA', fecha: new Date().toISOString() });
});

module.exports = router;
