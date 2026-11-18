package cl.ubiobio.petcare.dto;

import cl.ubiobio.petcare.model.entity.Tratamiento;

import java.time.LocalDate;
import java.util.List;

/** Tratamiento con sus medicamentos recetados. */
public record TratamientoResponse(
        Long id,
        Long atencionId,
        String descripcion,
        String indicaciones,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        List<MedicamentoPrescritoResponse> medicamentos
) {
    public static TratamientoResponse de(Tratamiento t) {
        return new TratamientoResponse(t.getId(), t.getAtencion().getId(), t.getDescripcion(),
                t.getIndicaciones(), t.getFechaInicio(), t.getFechaFin(),
                t.getMedicamentos().stream().map(MedicamentoPrescritoResponse::de).toList());
    }
}
