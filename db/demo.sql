-- =====================================================================
--  PetCare - Datos de DEMO para grabar el video
--
--  Deja la BD en un estado conocido, con fechas RELATIVAS al dia en que
--  se ejecuta (hoy, proximo lunes, proximo martes, manana...).
--
--  Requisitos: la aplicacion debe haber arrancado al menos una vez
--  (schema.sql crea las tablas y data.sql carga usuarios, clinica,
--  veterinarios, horarios y servicios).
--
--  Ejecutar (Docker):
--    docker exec -i petcare-mysql mysql -upetcare -ppetcare petcare < db/demo.sql
--  PowerShell:
--    Get-Content db/demo.sql | docker exec -i petcare-mysql mysql -upetcare -ppetcare petcare
--
--  Se puede ejecutar cuantas veces se quiera (antes de cada toma).
--  Usa ids fijos que cubren todos los de data.sql: al reiniciar Spring,
--  sus INSERT IGNORE no vuelven a meter las filas antiguas.
--
--  Estado que deja (dias referidos a la fecha de ejecucion):
--   Clientes 1-3 (Juan, Maria, Pedro). El cliente 4 queda libre para crearlo en vivo.
--   Reserva 1  Firulais  Dra. Perez  HOY 09:30           CONFIRMADA  -> agenda de hoy
--   Reserva 2  Rocky     Dr. Soto    HOY 11:00           PENDIENTE   -> agenda de hoy
--   Reserva 3  Michi     Dra. Perez  hace 18 dias 11:00  ATENDIDA    -> historial clinico + tratamiento
--   Reserva 4  Luna      Dra. Perez  PROX. LUNES 09:00   PENDIENTE   -> se cancela en vivo (libera hora)
--   Reserva 5  Rocky     Dra. Perez  PROX. LUNES 10:00   CONFIRMADA  -> hora ocupada para probar DUPLICADO
--   Reserva 6  Firulais  Dr. Soto    PROX. MARTES 15:00  CANCELADA   -> muestra estado cancelado
--   Reserva 7  Luna      Dr. Soto    MANANA 16:00        CONFIRMADA  -> "Enviar recordatorios de manana"
--   Notificacion 2 FALLIDA (reserva 5) -> boton "Reenviar"
-- =====================================================================

-- ---------------------------------------------------------------- fechas
SET @hoy    = CURDATE();
-- proximo dia D (0=lunes ... 6=domingo) estrictamente posterior a hoy
SET @lunes  = @hoy + INTERVAL (MOD(0 - WEEKDAY(@hoy) + 6, 7) + 1) DAY;
SET @martes = @hoy + INTERVAL (MOD(1 - WEEKDAY(@hoy) + 6, 7) + 1) DAY;
SET @manana = @hoy + INTERVAL 1 DAY;
SET @pasado = @hoy - INTERVAL 18 DAY;

-- ---------------------------------------------------------------- limpieza
DELETE FROM pago;
DELETE FROM notificacion;
DELETE FROM medicamento_prescrito;
DELETE FROM tratamiento;
DELETE FROM atencion;
DELETE FROM historial_estado_reserva;
DELETE FROM reserva;
DELETE FROM vacuna;
DELETE FROM mascota;
DELETE FROM cliente;

-- ids predecibles para lo que se crea en vivo (cliente 4, mascotas 5 y 6, reserva 8...)
ALTER TABLE cliente                  AUTO_INCREMENT = 4;
ALTER TABLE mascota                  AUTO_INCREMENT = 5;
ALTER TABLE reserva                  AUTO_INCREMENT = 8;
ALTER TABLE historial_estado_reserva AUTO_INCREMENT = 14;
ALTER TABLE atencion                 AUTO_INCREMENT = 2;
ALTER TABLE tratamiento              AUTO_INCREMENT = 2;
ALTER TABLE medicamento_prescrito    AUTO_INCREMENT = 2;
ALTER TABLE notificacion             AUTO_INCREMENT = 3;
ALTER TABLE pago                     AUTO_INCREMENT = 2;
ALTER TABLE vacuna                   AUTO_INCREMENT = 3;

-- ---------------------------------------------------------------- clientes y mascotas
INSERT INTO cliente (id, rut, nombre, apellido, email, telefono, direccion, usuario_id) VALUES
 (1, '12345678-9', 'Juan',  'Rojas',    'juan.rojas@gmail.com',     '+56911111111', 'Los Carrera 100, Concepcion',  5),
 (2, '98765432-1', 'Maria', 'Gonzalez', 'maria.gonzalez@gmail.com', '+56922222222', 'Barros Arana 500, Concepcion', NULL),
 (3, '11222333-4', 'Pedro', 'Munoz',    'pedro.munoz@gmail.com',    '+56933333333', 'O''Higgins 750, Concepcion',   NULL);

INSERT INTO mascota (id, cliente_id, nombre, especie, raza, sexo, fecha_nacimiento, peso, color, microchip) VALUES
 (1, 1, 'Firulais', 'Perro', 'Labrador',      'MACHO',  '2020-03-15', 28.50, 'Dorado', '900000000000001'),
 (2, 1, 'Michi',    'Gato',  'Siames',        'HEMBRA', '2021-07-01',  4.20, 'Crema',  NULL),
 (3, 2, 'Rocky',    'Perro', 'Pastor Aleman', 'MACHO',  '2019-11-20', 32.00, 'Negro',  '900000000000002'),
 (4, 3, 'Luna',     'Perro', 'Mestizo',       'HEMBRA', '2022-01-10', 12.30, 'Blanco', NULL);

-- ---------------------------------------------------------------- reservas
INSERT INTO reserva (id, mascota_id, veterinario_id, tipo_servicio_id, clinica_id, fecha, hora, motivo, estado) VALUES
 (1, 1, 1, 1, 1, @hoy,    '09:30:00', 'Decaimiento y falta de apetito', 'CONFIRMADA'),
 (2, 3, 2, 2, 1, @hoy,    '11:00:00', 'Vacuna antirrabica anual',       'PENDIENTE'),
 (3, 2, 1, 3, 1, @pasado, '11:00:00', 'Control dermatologico',          'ATENDIDA'),
 (4, 4, 1, 1, 1, @lunes,  '09:00:00', 'Revision de cojera',             'PENDIENTE'),
 (5, 3, 1, 3, 1, @lunes,  '10:00:00', 'Control post vacuna',            'CONFIRMADA'),
 (6, 1, 2, 4, 1, @martes, '15:00:00', 'Extraccion de quiste',           'CANCELADA'),
 (7, 4, 2, 2, 1, @manana, '16:00:00', 'Vacuna octuple',                 'CONFIRMADA');

INSERT INTO historial_estado_reserva (id, reserva_id, estado_anterior, estado_nuevo, motivo, fecha_cambio) VALUES
 (1, 1, NULL,         'PENDIENTE',  'Reserva creada',                 NOW() - INTERVAL 3 DAY),
 (2, 1, 'PENDIENTE',  'CONFIRMADA', 'Confirmada por recepcion',       NOW() - INTERVAL 2 DAY),
 (3, 2, NULL,         'PENDIENTE',  'Reserva creada',                 NOW() - INTERVAL 1 DAY),
 (4, 3, NULL,         'PENDIENTE',  'Reserva creada',                 @pasado - INTERVAL 5 DAY),
 (5, 3, 'PENDIENTE',  'CONFIRMADA', 'Confirmada por recepcion',       @pasado - INTERVAL 4 DAY),
 (6, 3, 'CONFIRMADA', 'ATENDIDA',   'Atencion registrada',            @pasado + INTERVAL 11 HOUR),
 (7, 4, NULL,         'PENDIENTE',  'Reserva creada',                 NOW() - INTERVAL 1 DAY),
 (8, 5, NULL,         'PENDIENTE',  'Reserva creada',                 NOW() - INTERVAL 2 DAY),
 (9, 5, 'PENDIENTE',  'CONFIRMADA', 'Confirmada por telefono',        NOW() - INTERVAL 1 DAY),
 (10, 6, NULL,         'PENDIENTE',  'Reserva creada',                 NOW() - INTERVAL 4 DAY),
 (11, 6, 'PENDIENTE',  'CANCELADA',  'Cliente reprograma',             NOW() - INTERVAL 2 DAY),
 (12, 7, NULL,         'PENDIENTE',  'Reserva creada',                 NOW() - INTERVAL 5 DAY),
 (13, 7, 'PENDIENTE',  'CONFIRMADA', 'Confirmada por recepcion',       NOW() - INTERVAL 4 DAY);

-- ---------------------------------------------------------------- historial clinico (reserva 3)
INSERT INTO atencion (id, reserva_id, mascota_id, veterinario_id, fecha_atencion, motivo_consulta, diagnostico, peso_registrado, temperatura) VALUES
 (1, 3, 2, 1, @pasado + INTERVAL 11 HOUR + INTERVAL 10 MINUTE, 'Control dermatologico', 'Dermatitis alergica leve', 4.10, 38.6);

INSERT INTO tratamiento (id, atencion_id, descripcion, indicaciones, fecha_inicio, fecha_fin) VALUES
 (1, 1, 'Tratamiento antialergico', 'Administrar con comida', @pasado, @pasado + INTERVAL 10 DAY);

INSERT INTO medicamento_prescrito (id, tratamiento_id, codigo_externo, nombre, principio_activo, dosis, frecuencia, duracion_dias, via_administracion) VALUES
 (1, 1, 'MED-001', 'Apoquel 3.6 mg', 'Oclacitinib', '1 comprimido', 'Cada 12 horas', 10, 'Oral');

INSERT INTO pago (id, reserva_id, monto, metodo, estado, fecha_pago) VALUES
 (1, 3, 10000, 'DEBITO', 'PAGADO', @pasado + INTERVAL 11 HOUR + INTERVAL 40 MINUTE);

INSERT INTO vacuna (id, mascota_id, veterinario_id, nombre, lote, fecha_aplicacion, fecha_proxima_dosis) VALUES
 (1, 1, 1, 'Antirrabica', 'L-2025-01', @hoy - INTERVAL 360 DAY, @hoy + INTERVAL 5 DAY),
 (2, 3, 2, 'Octuple',     'L-2025-07', @hoy - INTERVAL 340 DAY, @hoy + INTERVAL 25 DAY);

-- ---------------------------------------------------------------- notificaciones
INSERT INTO notificacion (id, cliente_id, reserva_id, canal, tipo, destinatario, asunto, mensaje, estado, respuesta_api, creado_en, enviado_en) VALUES
 (1, 1, 1, 'EMAIL', 'CONFIRMACION', 'juan.rojas@gmail.com', 'Reserva confirmada',
  CONCAT('Su hora para Firulais el ', DATE_FORMAT(@hoy, '%d-%m-%Y'), ' a las 09:30 fue confirmada.'),
  'ENVIADA', NULL, NOW() - INTERVAL 2 DAY, NOW() - INTERVAL 2 DAY),
 (2, 2, 5, 'EMAIL', 'CONFIRMACION', 'maria.gonzalez@gmail.com', 'Reserva confirmada',
  CONCAT('Su hora para Rocky el ', DATE_FORMAT(@lunes, '%d-%m-%Y'), ' a las 10:00 fue confirmada.'),
  'FALLIDA', 'I/O error: Connection refused (API externa sin conexion)', NOW() - INTERVAL 1 DAY, NULL);

-- ---------------------------------------------------------------- resumen
SELECT 'Demo cargada' AS estado, @hoy AS hoy, @manana AS manana, @lunes AS proximo_lunes, @martes AS proximo_martes;
