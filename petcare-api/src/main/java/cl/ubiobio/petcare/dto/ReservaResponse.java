package cl.ubiobio.petcare.dto;

import cl.ubiobio.petcare.model.entity.Cliente;
import cl.ubiobio.petcare.model.entity.Reserva;
import cl.ubiobio.petcare.model.entity.Veterinario;
import cl.ubiobio.petcare.model.enums.EstadoReserva;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/** Reserva devuelta por la API, con los nombres de mascota, cliente, veterinario y servicio. */
public record ReservaResponse(
        Long id,
        LocalDate fecha,
        LocalTime hora,
        EstadoReserva estado,
        String motivo,
        String observaciones,
        Long mascotaId,
        String mascotaNombre,
        Long clienteId,
        String clienteNombre,
        Long veterinarioId,
        String veterinarioNombre,
        Long tipoServicioId,
        String tipoServicioNombre,
        Long clinicaId,
        LocalDateTime creadoEn,
        LocalDateTime actualizadoEn
) {
    public static ReservaResponse de(Reserva r) {
        Cliente c = r.getMascota().getCliente();
        Veterinario v = r.getVeterinario();
        return new ReservaResponse(
                r.getId(), r.getFecha(), r.getHora(), r.getEstado(), r.getMotivo(), r.getObservaciones(),
                r.getMascota().getId(), r.getMascota().getNombre(),
                c.getId(), c.getNombre() + " " + c.getApellido(),
                v.getId(), v.getNombre() + " " + v.getApellido(),
                r.getTipoServicio() != null ? r.getTipoServicio().getId() : null,
                r.getTipoServicio() != null ? r.getTipoServicio().getNombre() : null,
                r.getClinica() != null ? r.getClinica().getId() : null,
                r.getCreadoEn(), r.getActualizadoEn());
    }
}
