# PetCare – Sistema de Gestión de Atención Veterinaria

Estudio de Caso 2 · Desarrollo Web Orientado a Objeto · Universidad del Bío-Bío

## Estructura

```
caso 2 webOS/
├── docker-compose.yml          MySQL 8.4 para desarrollo
├── petcare-api/                Backend Spring Boot 4 (Java 17, Maven)
│   └── src/main/
│       ├── java/cl/ubiobio/petcare/
│       │   ├── controller/     Capa web (REST)            → /api/**
│       │   ├── service/        Reglas de negocio
│       │   ├── repository/     Acceso a datos (Spring Data JPA)
│       │   ├── model/entity/   Entidades JPA (15 tablas)
│       │   ├── model/enums/    Estados, roles, canales, etc.
│       │   ├── client/         Clientes HTTP de las APIs externas
│       │   ├── dto/            Objetos de entrada/salida
│       │   ├── config/         CORS, RestClient, propiedades
│       │   └── exception/      Excepciones + manejador global
│       └── resources/
│           ├── application.properties
│           ├── schema.sql      Creación de tablas (idempotente)
│           └── data.sql        Datos de prueba (idempotente)
└── apis-externas/              APIs Node/Express (se ejecutan en un 2° equipo/red)
    └── src/routes/             medicamentos · geo · notificaciones
```

## Cómo ejecutar

1. **Base de datos** (MySQL 8):
   ```bash
   docker compose up -d
   ```
   Con Podman: `podman run -d --name petcare-mysql -e MYSQL_DATABASE=petcare -e MYSQL_USER=petcare -e MYSQL_PASSWORD=petcare -e MYSQL_ROOT_PASSWORD=root -p 3306:3306 docker.io/library/mysql:8.4`

2. **APIs externas** (en el segundo equipo):
   ```bash
   cd apis-externas
   npm install
   npm start                    # escucha en 0.0.0.0:4000
   ```

3. **Backend** (Spring crea las tablas y carga los datos al arrancar):
   ```bash
   cd petcare-api
   ./mvnw spring-boot:run       # Windows: .\mvnw.cmd spring-boot:run
   ```
   Comprobar: `GET http://localhost:8080/api/estado`

Variables de entorno opcionales: `DB_URL`, `DB_USER`, `DB_PASSWORD`, `APIS_URL` (ej. `http://192.168.1.50:4000`, la IP del equipo con las APIs Node), `CORS_ORIGINS`, `PORT`.

## Funcionalidades

| # | Funcionalidad | Origen |
|---|---|---|
| 1 | CRUD de clientes con datos de contacto | Enunciado |
| 2 | CRUD de mascotas asociadas a un cliente / listar mascotas de un cliente | Enunciado |
| 3 | Solicitar, modificar y cancelar horas médicas | Enunciado |
| 4 | Impedir reservas duplicadas (mismo veterinario o mascota, día y hora) | Enunciado |
| 5 | Estados de reserva: PENDIENTE, CONFIRMADA, CANCELADA, ATENDIDA | Enunciado |
| 6 | Consulta de medicamentos en API externa al registrar un tratamiento | Enunciado |
| 7 | Geolocalización de la clínica (mapa) | Enunciado |
| 8 | Notificación al cliente al confirmar/cancelar una reserva | Enunciado |
| 9 | Gestión de veterinarios y especialidades | Adicional |
| 10 | Horarios semanales de veterinarios y consulta de disponibilidad por día | Adicional |
| 11 | Historial clínico: atenciones con diagnóstico, peso y temperatura | Adicional |
| 12 | Tratamientos con medicamentos prescritos (dosis, frecuencia, duración) | Adicional |
| 13 | Carnet de vacunación y vacunas próximas a vencer | Adicional |
| 14 | Catálogo de servicios con duración y precio | Adicional |
| 15 | Auditoría de cambios de estado de cada reserva | Adicional |
| 16 | Registro de notificaciones enviadas/fallidas y reenvío | Adicional |
| 17 | Recordatorios de citas del día siguiente y de vacunas | Adicional |
| 18 | Registro de pagos de las reservas | Adicional |
| 19 | Reportes: reservas por estado, atenciones e ingresos del período, agenda diaria | Adicional |
| 20 | Usuarios y roles (ADMIN, RECEPCIONISTA, VETERINARIO, CLIENTE) | Adicional (solo tabla por ahora) |

## Endpoints

Backend `http://<host>:8080`

| Recurso | Endpoints |
|---|---|
| Estado | `GET /api/estado` ✅ |
| Clientes | `GET/POST /api/clientes` · `GET/PUT/DELETE /api/clientes/{id}` · `GET /api/clientes/rut/{rut}` · `GET /api/clientes/{id}/mascotas` · `GET /api/clientes/{id}/reservas` · `GET /api/clientes/{id}/notificaciones` |
| Mascotas | `GET/POST /api/mascotas` · `GET/PUT/DELETE /api/mascotas/{id}` · `GET /api/mascotas/{id}/historial` · `GET /api/mascotas/{id}/reservas` · `GET /api/mascotas/{id}/vacunas` |
| Veterinarios | `GET/POST /api/veterinarios` · `GET/PUT/DELETE /api/veterinarios/{id}` · `GET/POST /api/veterinarios/{id}/horarios` · `DELETE /api/veterinarios/{id}/horarios/{horarioId}` · `GET /api/veterinarios/{id}/disponibilidad?fecha=` |
| Servicios | `GET/POST /api/servicios` · `PUT/DELETE /api/servicios/{id}` |
| Reservas | `GET /api/reservas?estado=&fecha=&veterinarioId=` · `POST /api/reservas` · `GET/PUT /api/reservas/{id}` · `PATCH /api/reservas/{id}/confirmar` · `PATCH /api/reservas/{id}/cancelar` · `PATCH /api/reservas/{id}/atender` · `GET /api/reservas/{id}/historial-estados` |
| Atenciones | `POST /api/atenciones` · `GET/PUT /api/atenciones/{id}` · `GET /api/atenciones/{id}/tratamientos` |
| Tratamientos | `POST /api/tratamientos` · `GET/PUT /api/tratamientos/{id}` · `POST /api/tratamientos/{id}/medicamentos` · `DELETE /api/tratamientos/{id}/medicamentos/{medicamentoId}` |
| Medicamentos | `GET /api/medicamentos/buscar?nombre=` · `GET /api/medicamentos/{codigo}` |
| Vacunas | `POST /api/vacunas` · `PUT/DELETE /api/vacunas/{id}` · `GET /api/vacunas/proximas?dias=30` |
| Clínicas | `GET /api/clinicas` · `GET/PUT /api/clinicas/{id}` · `GET /api/clinicas/{id}/ubicacion` |
| Notificaciones | `GET /api/notificaciones?estado=` · `POST /api/notificaciones/{id}/reenviar` · `POST /api/notificaciones/recordatorios` |
| Pagos | `POST /api/pagos` · `GET /api/pagos/reserva/{reservaId}` · `PATCH /api/pagos/{id}/anular` |
| Reportes | `GET /api/reportes/resumen?desde=&hasta=` · `GET /api/reportes/agenda?fecha=` |

APIs externas `http://<ip-equipo-2>:4000`

| Endpoint | Descripción |
|---|---|
| `GET /api/estado` ✅ | Estado del servicio |
| `GET /api/medicamentos?nombre=amox` ✅ | Busca por nombre o principio activo |
| `GET /api/medicamentos/{codigo}` ✅ | Detalle de un medicamento |
| `GET /api/geo/ubicacion?lat=&lng=` ✅ | URL de mapa (OpenStreetMap) y URL para embeber |
| `GET /api/geo/geocodificar?direccion=` | Pendiente (Nominatim) |
| `POST /api/notificaciones/email` ✅ | `{ para, asunto, mensaje }` (envío simulado; pendiente nodemailer) |
| `POST /api/notificaciones/sms` ✅ | `{ para, mensaje }` (envío simulado) |

✅ = ya responde. El resto está declarado en los controladores/servicios con `TODO`.

## Base de datos

15 tablas: `usuario`, `clinica`, `cliente`, `mascota`, `veterinario`, `horario_veterinario`, `tipo_servicio`, `reserva`, `historial_estado_reserva`, `atencion`, `tratamiento`, `medicamento_prescrito`, `vacuna`, `notificacion`, `pago`.

**Reservas duplicadas:** la tabla `reserva` tiene una columna generada `slot_activo` (1 si la reserva está activa, `NULL` si está CANCELADA) y los índices únicos `(veterinario_id, fecha, hora, slot_activo)` y `(mascota_id, fecha, hora, slot_activo)`. Así la BD rechaza dos reservas activas en el mismo horario, pero una reserva cancelada libera ese horario. El servicio también lo valida antes de guardar (`existsByVeterinarioIdAndFechaAndHoraAndEstadoNot`) para devolver un 409 con un mensaje claro.

Hibernate está en modo `validate`: si una entidad no coincide con `schema.sql`, la aplicación no arranca.
