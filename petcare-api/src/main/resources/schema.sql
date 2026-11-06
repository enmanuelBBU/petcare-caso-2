-- =====================================================================
--  PetCare - Sistema de Gestion de Atencion Veterinaria
--  Esquema de base de datos (MySQL 8)
--  Se ejecuta automaticamente al iniciar Spring Boot (spring.sql.init.mode=always).
--  Es idempotente: usa CREATE TABLE IF NOT EXISTS.
-- =====================================================================

-- ---------------------------------------------------------------------
-- USUARIOS (acceso al sistema y roles)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS usuario (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    email           VARCHAR(120) NOT NULL,
    password_hash   VARCHAR(255) NOT NULL,
    rol             VARCHAR(20)  NOT NULL,
    activo          BOOLEAN      NOT NULL DEFAULT TRUE,
    creado_en       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_usuario PRIMARY KEY (id),
    CONSTRAINT uk_usuario_email UNIQUE (email),
    CONSTRAINT ck_usuario_rol CHECK (rol IN ('ADMIN', 'RECEPCIONISTA', 'VETERINARIO', 'CLIENTE'))
);

-- ---------------------------------------------------------------------
-- CLINICA (sede, con coordenadas para geolocalizacion)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS clinica (
    id              BIGINT        NOT NULL AUTO_INCREMENT,
    nombre          VARCHAR(120)  NOT NULL,
    direccion       VARCHAR(200)  NOT NULL,
    comuna          VARCHAR(80),
    ciudad          VARCHAR(80)   NOT NULL,
    telefono        VARCHAR(20),
    email           VARCHAR(120),
    latitud         DECIMAL(10,7),
    longitud        DECIMAL(10,7),
    hora_apertura   TIME,
    hora_cierre     TIME,
    CONSTRAINT pk_clinica PRIMARY KEY (id)
);

-- ---------------------------------------------------------------------
-- CLIENTES (duenos de mascotas)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS cliente (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    rut             VARCHAR(12)  NOT NULL,
    nombre          VARCHAR(80)  NOT NULL,
    apellido        VARCHAR(80)  NOT NULL,
    email           VARCHAR(120) NOT NULL,
    telefono        VARCHAR(20)  NOT NULL,
    direccion       VARCHAR(200),
    usuario_id      BIGINT,
    creado_en       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_cliente PRIMARY KEY (id),
    CONSTRAINT uk_cliente_rut UNIQUE (rut),
    CONSTRAINT uk_cliente_email UNIQUE (email),
    CONSTRAINT uk_cliente_usuario UNIQUE (usuario_id),
    CONSTRAINT fk_cliente_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id) ON DELETE SET NULL
);

-- ---------------------------------------------------------------------
-- MASCOTAS (cada cliente puede tener 1..N mascotas)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS mascota (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    cliente_id        BIGINT       NOT NULL,
    nombre            VARCHAR(80)  NOT NULL,
    especie           VARCHAR(40)  NOT NULL,
    raza              VARCHAR(80),
    sexo              VARCHAR(10),
    fecha_nacimiento  DATE,
    peso              DECIMAL(6,2),
    color             VARCHAR(40),
    microchip         VARCHAR(30),
    activo            BOOLEAN      NOT NULL DEFAULT TRUE,
    creado_en         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_mascota PRIMARY KEY (id),
    CONSTRAINT uk_mascota_microchip UNIQUE (microchip),
    CONSTRAINT fk_mascota_cliente FOREIGN KEY (cliente_id) REFERENCES cliente (id) ON DELETE CASCADE,
    CONSTRAINT ck_mascota_sexo CHECK (sexo IS NULL OR sexo IN ('MACHO', 'HEMBRA')),
    CONSTRAINT ck_mascota_peso CHECK (peso IS NULL OR peso > 0)
);

-- ---------------------------------------------------------------------
-- VETERINARIOS (profesionales)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS veterinario (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    rut             VARCHAR(12)  NOT NULL,
    nombre          VARCHAR(80)  NOT NULL,
    apellido        VARCHAR(80)  NOT NULL,
    email           VARCHAR(120) NOT NULL,
    telefono        VARCHAR(20),
    especialidad    VARCHAR(80),
    clinica_id      BIGINT,
    usuario_id      BIGINT,
    activo          BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT pk_veterinario PRIMARY KEY (id),
    CONSTRAINT uk_veterinario_rut UNIQUE (rut),
    CONSTRAINT uk_veterinario_email UNIQUE (email),
    CONSTRAINT uk_veterinario_usuario UNIQUE (usuario_id),
    CONSTRAINT fk_veterinario_clinica FOREIGN KEY (clinica_id) REFERENCES clinica (id) ON DELETE SET NULL,
    CONSTRAINT fk_veterinario_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id) ON DELETE SET NULL
);

-- ---------------------------------------------------------------------
-- HORARIOS DE ATENCION DE CADA VETERINARIO (disponibilidad semanal)
-- dia_semana: 1 = lunes ... 7 = domingo
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS horario_veterinario (
    id              BIGINT  NOT NULL AUTO_INCREMENT,
    veterinario_id  BIGINT  NOT NULL,
    dia_semana      INT     NOT NULL,
    hora_inicio     TIME    NOT NULL,
    hora_fin        TIME    NOT NULL,
    CONSTRAINT pk_horario_veterinario PRIMARY KEY (id),
    CONSTRAINT uk_horario_vet_dia UNIQUE (veterinario_id, dia_semana, hora_inicio),
    CONSTRAINT fk_horario_veterinario FOREIGN KEY (veterinario_id) REFERENCES veterinario (id) ON DELETE CASCADE,
    CONSTRAINT ck_horario_dia CHECK (dia_semana BETWEEN 1 AND 7),
    CONSTRAINT ck_horario_rango CHECK (hora_fin > hora_inicio)
);

-- ---------------------------------------------------------------------
-- TIPOS DE SERVICIO (consulta general, vacunacion, cirugia, ...)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS tipo_servicio (
    id                BIGINT        NOT NULL AUTO_INCREMENT,
    nombre            VARCHAR(80)   NOT NULL,
    descripcion       VARCHAR(255),
    duracion_minutos  INT           NOT NULL DEFAULT 30,
    precio            DECIMAL(10,2) NOT NULL DEFAULT 0,
    activo            BOOLEAN       NOT NULL DEFAULT TRUE,
    CONSTRAINT pk_tipo_servicio PRIMARY KEY (id),
    CONSTRAINT uk_tipo_servicio_nombre UNIQUE (nombre),
    CONSTRAINT ck_tipo_servicio_duracion CHECK (duracion_minutos > 0),
    CONSTRAINT ck_tipo_servicio_precio CHECK (precio >= 0)
);

-- ---------------------------------------------------------------------
-- RESERVAS (horas medicas)
-- slot_activo vale 1 si la reserva ocupa el horario y NULL si esta CANCELADA.
-- Como MySQL permite varios NULL en un indice UNIQUE, una reserva cancelada
-- libera el horario, pero NO se pueden crear dos reservas activas para el
-- mismo veterinario (o la misma mascota) en el mismo dia y hora.
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS reserva (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    mascota_id        BIGINT       NOT NULL,
    veterinario_id    BIGINT       NOT NULL,
    tipo_servicio_id  BIGINT,
    clinica_id        BIGINT,
    fecha             DATE         NOT NULL,
    hora              TIME         NOT NULL,
    motivo            VARCHAR(255),
    estado            VARCHAR(15)  NOT NULL DEFAULT 'PENDIENTE',
    observaciones     VARCHAR(500),
    slot_activo       TINYINT GENERATED ALWAYS AS (CASE WHEN estado = 'CANCELADA' THEN NULL ELSE 1 END) STORED,
    creado_en         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_reserva PRIMARY KEY (id),
    CONSTRAINT uk_reserva_veterinario_slot UNIQUE (veterinario_id, fecha, hora, slot_activo),
    CONSTRAINT uk_reserva_mascota_slot UNIQUE (mascota_id, fecha, hora, slot_activo),
    CONSTRAINT fk_reserva_mascota FOREIGN KEY (mascota_id) REFERENCES mascota (id) ON DELETE CASCADE,
    CONSTRAINT fk_reserva_veterinario FOREIGN KEY (veterinario_id) REFERENCES veterinario (id),
    CONSTRAINT fk_reserva_tipo_servicio FOREIGN KEY (tipo_servicio_id) REFERENCES tipo_servicio (id) ON DELETE SET NULL,
    CONSTRAINT fk_reserva_clinica FOREIGN KEY (clinica_id) REFERENCES clinica (id) ON DELETE SET NULL,
    CONSTRAINT ck_reserva_estado CHECK (estado IN ('PENDIENTE', 'CONFIRMADA', 'CANCELADA', 'ATENDIDA'))
);

-- ---------------------------------------------------------------------
-- HISTORIAL DE CAMBIOS DE ESTADO DE UNA RESERVA (auditoria)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS historial_estado_reserva (
    id               BIGINT       NOT NULL AUTO_INCREMENT,
    reserva_id       BIGINT       NOT NULL,
    estado_anterior  VARCHAR(15),
    estado_nuevo     VARCHAR(15)  NOT NULL,
    motivo           VARCHAR(255),
    fecha_cambio     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_historial_estado_reserva PRIMARY KEY (id),
    CONSTRAINT fk_historial_reserva FOREIGN KEY (reserva_id) REFERENCES reserva (id) ON DELETE CASCADE
);

-- ---------------------------------------------------------------------
-- ATENCIONES (ficha / historial clinico de la mascota)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS atencion (
    id                BIGINT        NOT NULL AUTO_INCREMENT,
    reserva_id        BIGINT,
    mascota_id        BIGINT        NOT NULL,
    veterinario_id    BIGINT        NOT NULL,
    fecha_atencion    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    motivo_consulta   VARCHAR(255),
    anamnesis         VARCHAR(1000),
    diagnostico       VARCHAR(1000) NOT NULL,
    peso_registrado   DECIMAL(6,2),
    temperatura       DECIMAL(4,1),
    observaciones     VARCHAR(1000),
    CONSTRAINT pk_atencion PRIMARY KEY (id),
    CONSTRAINT uk_atencion_reserva UNIQUE (reserva_id),
    CONSTRAINT fk_atencion_reserva FOREIGN KEY (reserva_id) REFERENCES reserva (id) ON DELETE SET NULL,
    CONSTRAINT fk_atencion_mascota FOREIGN KEY (mascota_id) REFERENCES mascota (id) ON DELETE CASCADE,
    CONSTRAINT fk_atencion_veterinario FOREIGN KEY (veterinario_id) REFERENCES veterinario (id)
);

-- ---------------------------------------------------------------------
-- TRATAMIENTOS (asociados a una atencion)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS tratamiento (
    id            BIGINT        NOT NULL AUTO_INCREMENT,
    atencion_id   BIGINT        NOT NULL,
    descripcion   VARCHAR(500)  NOT NULL,
    indicaciones  VARCHAR(1000),
    fecha_inicio  DATE          NOT NULL,
    fecha_fin     DATE,
    CONSTRAINT pk_tratamiento PRIMARY KEY (id),
    CONSTRAINT fk_tratamiento_atencion FOREIGN KEY (atencion_id) REFERENCES atencion (id) ON DELETE CASCADE,
    CONSTRAINT ck_tratamiento_fechas CHECK (fecha_fin IS NULL OR fecha_fin >= fecha_inicio)
);

-- ---------------------------------------------------------------------
-- MEDICAMENTOS PRESCRITOS (datos consultados en la API externa de medicamentos)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS medicamento_prescrito (
    id                  BIGINT        NOT NULL AUTO_INCREMENT,
    tratamiento_id      BIGINT        NOT NULL,
    codigo_externo      VARCHAR(60),
    nombre              VARCHAR(120)  NOT NULL,
    principio_activo    VARCHAR(120),
    dosis               VARCHAR(60)   NOT NULL,
    frecuencia          VARCHAR(60)   NOT NULL,
    duracion_dias       INT,
    via_administracion  VARCHAR(40),
    CONSTRAINT pk_medicamento_prescrito PRIMARY KEY (id),
    CONSTRAINT fk_medicamento_tratamiento FOREIGN KEY (tratamiento_id) REFERENCES tratamiento (id) ON DELETE CASCADE,
    CONSTRAINT ck_medicamento_duracion CHECK (duracion_dias IS NULL OR duracion_dias > 0)
);

-- ---------------------------------------------------------------------
-- VACUNAS (carnet de vacunacion de la mascota)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS vacuna (
    id                   BIGINT       NOT NULL AUTO_INCREMENT,
    mascota_id           BIGINT       NOT NULL,
    veterinario_id       BIGINT,
    nombre               VARCHAR(100) NOT NULL,
    lote                 VARCHAR(40),
    fecha_aplicacion     DATE         NOT NULL,
    fecha_proxima_dosis  DATE,
    CONSTRAINT pk_vacuna PRIMARY KEY (id),
    CONSTRAINT fk_vacuna_mascota FOREIGN KEY (mascota_id) REFERENCES mascota (id) ON DELETE CASCADE,
    CONSTRAINT fk_vacuna_veterinario FOREIGN KEY (veterinario_id) REFERENCES veterinario (id) ON DELETE SET NULL,
    CONSTRAINT ck_vacuna_fechas CHECK (fecha_proxima_dosis IS NULL OR fecha_proxima_dosis > fecha_aplicacion)
);

-- ---------------------------------------------------------------------
-- NOTIFICACIONES (registro de mensajes enviados por la API externa)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS notificacion (
    id              BIGINT        NOT NULL AUTO_INCREMENT,
    cliente_id      BIGINT        NOT NULL,
    reserva_id      BIGINT,
    canal           VARCHAR(10)   NOT NULL,
    tipo            VARCHAR(15)   NOT NULL,
    destinatario    VARCHAR(120)  NOT NULL,
    asunto          VARCHAR(150),
    mensaje         VARCHAR(1000) NOT NULL,
    estado          VARCHAR(10)   NOT NULL DEFAULT 'PENDIENTE',
    respuesta_api   VARCHAR(500),
    creado_en       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    enviado_en      DATETIME,
    CONSTRAINT pk_notificacion PRIMARY KEY (id),
    CONSTRAINT fk_notificacion_cliente FOREIGN KEY (cliente_id) REFERENCES cliente (id) ON DELETE CASCADE,
    CONSTRAINT fk_notificacion_reserva FOREIGN KEY (reserva_id) REFERENCES reserva (id) ON DELETE SET NULL,
    CONSTRAINT ck_notificacion_canal CHECK (canal IN ('EMAIL', 'SMS', 'WHATSAPP')),
    CONSTRAINT ck_notificacion_tipo CHECK (tipo IN ('CONFIRMACION', 'CANCELACION', 'MODIFICACION', 'RECORDATORIO')),
    CONSTRAINT ck_notificacion_estado CHECK (estado IN ('PENDIENTE', 'ENVIADA', 'FALLIDA'))
);

-- ---------------------------------------------------------------------
-- PAGOS (cobro de la atencion / reserva)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS pago (
    id          BIGINT        NOT NULL AUTO_INCREMENT,
    reserva_id  BIGINT        NOT NULL,
    monto       DECIMAL(10,2) NOT NULL,
    metodo      VARCHAR(15)   NOT NULL,
    estado      VARCHAR(12)   NOT NULL DEFAULT 'PENDIENTE',
    fecha_pago  DATETIME,
    CONSTRAINT pk_pago PRIMARY KEY (id),
    CONSTRAINT fk_pago_reserva FOREIGN KEY (reserva_id) REFERENCES reserva (id) ON DELETE CASCADE,
    CONSTRAINT ck_pago_monto CHECK (monto >= 0),
    CONSTRAINT ck_pago_metodo CHECK (metodo IN ('EFECTIVO', 'DEBITO', 'CREDITO', 'TRANSFERENCIA')),
    CONSTRAINT ck_pago_estado CHECK (estado IN ('PENDIENTE', 'PAGADO', 'ANULADO'))
);
