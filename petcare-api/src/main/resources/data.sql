-- =====================================================================
--  PetCare - Datos iniciales de prueba
--  INSERT IGNORE + ids fijos: se puede ejecutar en cada arranque sin duplicar.
--  Las contrasenas son de demostracion ({noop} = sin cifrar); cambiarlas
--  por hashes BCrypt cuando se implemente la seguridad.
-- =====================================================================

INSERT IGNORE INTO usuario (id, email, password_hash, rol) VALUES
 (1, 'admin@petcare.cl',       '{noop}admin123', 'ADMIN'),
 (2, 'recepcion@petcare.cl',   '{noop}recep123', 'RECEPCIONISTA'),
 (3, 'cperez@petcare.cl',      '{noop}vet123',   'VETERINARIO'),
 (4, 'msoto@petcare.cl',       '{noop}vet123',   'VETERINARIO'),
 (5, 'juan.rojas@gmail.com',   '{noop}cli123',   'CLIENTE');

INSERT IGNORE INTO clinica (id, nombre, direccion, comuna, ciudad, telefono, email, latitud, longitud, hora_apertura, hora_cierre) VALUES
 (1, 'PetCare Concepcion', 'Av. Collao 1202', 'Concepcion', 'Concepcion', '+56412111111', 'contacto@petcare.cl', -36.8214000, -73.0122000, '09:00:00', '19:00:00');

INSERT IGNORE INTO cliente (id, rut, nombre, apellido, email, telefono, direccion, usuario_id) VALUES
 (1, '12345678-9', 'Juan',   'Rojas',    'juan.rojas@gmail.com',   '+56911111111', 'Los Carrera 100, Concepcion', 5),
 (2, '98765432-1', 'Maria',  'Gonzalez', 'maria.gonzalez@gmail.com', '+56922222222', 'Barros Arana 500, Concepcion', NULL),
 (3, '11222333-4', 'Pedro',  'Munoz',    'pedro.munoz@gmail.com',  '+56933333333', 'O''Higgins 750, Concepcion', NULL);

INSERT IGNORE INTO mascota (id, cliente_id, nombre, especie, raza, sexo, fecha_nacimiento, peso, color, microchip) VALUES
 (1, 1, 'Firulais', 'Perro', 'Labrador',        'MACHO',  '2020-03-15', 28.50, 'Dorado', '900000000000001'),
 (2, 1, 'Michi',    'Gato',  'Siames',          'HEMBRA', '2021-07-01',  4.20, 'Crema',  NULL),
 (3, 2, 'Rocky',    'Perro', 'Pastor Aleman',   'MACHO',  '2019-11-20', 32.00, 'Negro',  '900000000000002'),
 (4, 3, 'Luna',     'Perro', 'Mestizo',         'HEMBRA', '2022-01-10', 12.30, 'Blanco', NULL);

INSERT IGNORE INTO veterinario (id, rut, nombre, apellido, email, telefono, especialidad, clinica_id, usuario_id) VALUES
 (1, '15111222-3', 'Carla',  'Perez', 'cperez@petcare.cl', '+56944444444', 'Medicina general', 1, 3),
 (2, '16222333-4', 'Manuel', 'Soto',  'msoto@petcare.cl',  '+56955555555', 'Cirugia',          1, 4);

INSERT IGNORE INTO horario_veterinario (id, veterinario_id, dia_semana, hora_inicio, hora_fin) VALUES
 (1, 1, 1, '09:00:00', '13:00:00'),
 (2, 1, 3, '09:00:00', '13:00:00'),
 (3, 1, 5, '14:00:00', '18:00:00'),
 (4, 2, 2, '09:00:00', '17:00:00'),
 (5, 2, 4, '09:00:00', '17:00:00');

INSERT IGNORE INTO tipo_servicio (id, nombre, descripcion, duracion_minutos, precio) VALUES
 (1, 'Consulta general',  'Evaluacion clinica general',       30, 20000),
 (2, 'Vacunacion',        'Aplicacion de vacunas',            15, 12000),
 (3, 'Control',           'Control post tratamiento',         20, 10000),
 (4, 'Cirugia menor',     'Procedimientos quirurgicos menores', 90, 85000);

INSERT IGNORE INTO reserva (id, mascota_id, veterinario_id, tipo_servicio_id, clinica_id, fecha, hora, motivo, estado) VALUES
 (1, 1, 1, 1, 1, '2026-11-16', '09:00:00', 'Decaimiento y falta de apetito', 'CONFIRMADA'),
 (2, 3, 2, 2, 1, '2026-11-17', '10:00:00', 'Vacuna antirrabica anual',        'PENDIENTE'),
 (3, 2, 1, 3, 1, '2026-11-02', '11:00:00', 'Control dermatologico',           'ATENDIDA');

INSERT IGNORE INTO historial_estado_reserva (id, reserva_id, estado_anterior, estado_nuevo, motivo) VALUES
 (1, 1, NULL,        'PENDIENTE',  'Reserva creada'),
 (2, 1, 'PENDIENTE', 'CONFIRMADA', 'Confirmada por recepcion'),
 (3, 3, 'CONFIRMADA','ATENDIDA',   'Atencion registrada');

INSERT IGNORE INTO atencion (id, reserva_id, mascota_id, veterinario_id, fecha_atencion, motivo_consulta, diagnostico, peso_registrado, temperatura) VALUES
 (1, 3, 2, 1, '2026-11-02 11:10:00', 'Control dermatologico', 'Dermatitis alergica leve', 4.10, 38.6);

INSERT IGNORE INTO tratamiento (id, atencion_id, descripcion, indicaciones, fecha_inicio, fecha_fin) VALUES
 (1, 1, 'Tratamiento antialergico', 'Administrar con comida', '2026-11-02', '2026-11-12');

INSERT IGNORE INTO medicamento_prescrito (id, tratamiento_id, codigo_externo, nombre, principio_activo, dosis, frecuencia, duracion_dias, via_administracion) VALUES
 (1, 1, 'MED-001', 'Apoquel 3.6 mg', 'Oclacitinib', '1 comprimido', 'Cada 12 horas', 10, 'Oral');

INSERT IGNORE INTO vacuna (id, mascota_id, veterinario_id, nombre, lote, fecha_aplicacion, fecha_proxima_dosis) VALUES
 (1, 1, 1, 'Antirrabica',  'L-2025-01', '2025-11-10', '2026-11-10'),
 (2, 3, 2, 'Octuple',      'L-2025-07', '2025-12-01', '2026-12-01');

INSERT IGNORE INTO notificacion (id, cliente_id, reserva_id, canal, tipo, destinatario, asunto, mensaje, estado, enviado_en) VALUES
 (1, 1, 1, 'EMAIL', 'CONFIRMACION', 'juan.rojas@gmail.com', 'Reserva confirmada',
  'Su hora para Firulais el 16-11-2026 a las 09:00 fue confirmada.', 'ENVIADA', '2026-11-05 10:00:00');

INSERT IGNORE INTO pago (id, reserva_id, monto, metodo, estado, fecha_pago) VALUES
 (1, 3, 10000, 'DEBITO', 'PAGADO', '2026-11-02 11:40:00');
