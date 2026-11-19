package cl.ubiobio.petcare.dto;

import cl.ubiobio.petcare.model.entity.Cliente;
import cl.ubiobio.petcare.model.entity.Vacuna;
import cl.ubiobio.petcare.model.entity.Veterinario;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Vacuna del carnet de una mascota.
 * diasParaProximaDosis es negativo si la dosis ya vencio (null si no hay proxima dosis).
 */
public record VacunaResponse(
        Long id,
        Long mascotaId,
        String mascotaNombre,
        Long clienteId,
        String clienteNombre,
        Long veterinarioId,
        String veterinarioNombre,
        String nombre,
        String lote,
        LocalDate fechaAplicacion,
        LocalDate fechaProximaDosis,
        Long diasParaProximaDosis
) {
    public static VacunaResponse de(Vacuna v) {
        Cliente c = v.getMascota().getCliente();
        Veterinario vet = v.getVeterinario();
        return new VacunaResponse(v.getId(),
                v.getMascota().getId(), v.getMascota().getNombre(),
                c.getId(), c.getNombre() + " " + c.getApellido(),
                vet != null ? vet.getId() : null,
                vet != null ? vet.getNombre() + " " + vet.getApellido() : null,
                v.getNombre(), v.getLote(), v.getFechaAplicacion(), v.getFechaProximaDosis(),
                v.getFechaProximaDosis() != null
                        ? ChronoUnit.DAYS.between(LocalDate.now(), v.getFechaProximaDosis())
                        : null);
    }
}
