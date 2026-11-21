package cl.ubiobio.petcare.web.form;

import cl.ubiobio.petcare.dto.TratamientoRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.BindingResult;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Formulario de tratamiento con sus filas de medicamentos (las filas vacias se descartan). */
@Data
@NoArgsConstructor
public class TratamientoForm {

    @NotNull(message = "La atencion es obligatoria")
    private Long atencionId;

    @NotBlank(message = "La descripcion es obligatoria")
    @Size(max = 500)
    private String descripcion;

    @Size(max = 1000)
    private String indicaciones;

    @NotNull(message = "La fecha de inicio es obligatoria")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaInicio = LocalDate.now();

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaFin;

    private List<MedicamentoForm> medicamentos = new ArrayList<>();

    /** Filas con datos deben indicar medicamento, dosis y frecuencia. */
    public void validarMedicamentos(BindingResult br) {
        for (int i = 0; i < medicamentos.size(); i++) {
            MedicamentoForm m = medicamentos.get(i);
            if (m == null || m.vacio()) {
                continue;
            }
            String base = "medicamentos[" + i + "].";
            if (MedicamentoForm.enBlanco(m.getNombre()) && MedicamentoForm.enBlanco(m.getCodigoExterno())) {
                br.rejectValue(base + "nombre", "NotBlank", "Indique el medicamento");
            }
            if (MedicamentoForm.enBlanco(m.getDosis())) {
                br.rejectValue(base + "dosis", "NotBlank", "La dosis es obligatoria");
            }
            if (MedicamentoForm.enBlanco(m.getFrecuencia())) {
                br.rejectValue(base + "frecuencia", "NotBlank", "La frecuencia es obligatoria");
            }
            if (m.getDuracionDias() != null && m.getDuracionDias() <= 0) {
                br.rejectValue(base + "duracionDias", "Positive", "La duracion debe ser mayor a 0");
            }
        }
    }

    public TratamientoRequest toRequest() {
        return new TratamientoRequest(atencionId, descripcion, AtencionForm.vacioANull(indicaciones), fechaInicio,
                fechaFin, medicamentos.stream()
                .filter(m -> m != null && !m.vacio())
                .map(MedicamentoForm::toRequest)
                .toList());
    }
}
