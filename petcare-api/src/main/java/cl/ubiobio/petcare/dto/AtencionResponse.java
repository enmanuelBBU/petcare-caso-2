package cl.ubiobio.petcare.dto;

import cl.ubiobio.petcare.model.entity.Atencion;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** Atencion (ficha clinica) con sus tratamientos: es la unidad del historial medico de la mascota. */
public record AtencionResponse(
        Long id,
        Long reservaId,
        Long mascotaId,
        String mascotaNombre,
        Long veterinarioId,
        String veterinarioNombre,
        LocalDateTime fechaAtencion,
        String motivoConsulta,
        String anamnesis,
        String diagnostico,
        BigDecimal pesoRegistrado,
        BigDecimal temperatura,
        String observaciones,
        List<TratamientoResponse> tratamientos
) {
    public static AtencionResponse de(Atencion a) {
        return new AtencionResponse(a.getId(),
                a.getReserva() != null ? a.getReserva().getId() : null,
                a.getMascota().getId(), a.getMascota().getNombre(),
                a.getVeterinario().getId(), a.getVeterinario().getNombre() + " " + a.getVeterinario().getApellido(),
                a.getFechaAtencion(), a.getMotivoConsulta(), a.getAnamnesis(), a.getDiagnostico(),
                a.getPesoRegistrado(), a.getTemperatura(), a.getObservaciones(),
                a.getTratamientos().stream().map(TratamientoResponse::de).toList());
    }
}
