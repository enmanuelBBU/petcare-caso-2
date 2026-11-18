package cl.ubiobio.petcare.dto;

import java.util.List;

/** Medicamento tal como lo entrega la API externa de medicamentos (Node). */
public record MedicamentoExternoDto(
        String codigo,
        String nombre,
        String principioActivo,
        String presentacion,
        List<String> especies,
        String indicaciones
) {
}
