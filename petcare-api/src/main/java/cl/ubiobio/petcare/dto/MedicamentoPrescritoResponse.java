package cl.ubiobio.petcare.dto;

import cl.ubiobio.petcare.model.entity.MedicamentoPrescrito;

/** Medicamento recetado dentro de un tratamiento. */
public record MedicamentoPrescritoResponse(
        Long id,
        String codigoExterno,
        String nombre,
        String principioActivo,
        String dosis,
        String frecuencia,
        Integer duracionDias,
        String viaAdministracion
) {
    public static MedicamentoPrescritoResponse de(MedicamentoPrescrito m) {
        return new MedicamentoPrescritoResponse(m.getId(), m.getCodigoExterno(), m.getNombre(),
                m.getPrincipioActivo(), m.getDosis(), m.getFrecuencia(), m.getDuracionDias(),
                m.getViaAdministracion());
    }
}
