package cl.ubiobio.petcare.web.form;

import cl.ubiobio.petcare.dto.MedicamentoPrescritoRequest;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Fila de medicamento recetado dentro del formulario de tratamiento. */
@Data
@NoArgsConstructor
public class MedicamentoForm {

    private String codigoExterno;
    private String nombre;
    private String dosis;
    private String frecuencia;
    private Integer duracionDias;
    private String viaAdministracion;

    /** Fila agregada y no llenada: se ignora al guardar. */
    public boolean vacio() {
        return enBlanco(nombre) && enBlanco(codigoExterno) && enBlanco(dosis) && enBlanco(frecuencia);
    }

    public MedicamentoPrescritoRequest toRequest() {
        return new MedicamentoPrescritoRequest(AtencionForm.vacioANull(codigoExterno), AtencionForm.vacioANull(nombre),
                dosis, frecuencia, duracionDias, AtencionForm.vacioANull(viaAdministracion));
    }

    static boolean enBlanco(String s) {
        return s == null || s.isBlank();
    }
}
