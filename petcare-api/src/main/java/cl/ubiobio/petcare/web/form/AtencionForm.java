package cl.ubiobio.petcare.web.form;

import cl.ubiobio.petcare.dto.AtencionRequest;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** Formulario de atencion de una reserva; mascota y veterinario se toman de la reserva en el service. */
@Data
@NoArgsConstructor
public class AtencionForm {

    @NotNull(message = "La reserva es obligatoria")
    private Long reservaId;

    @Size(max = 255)
    private String motivoConsulta;

    @Size(max = 1000)
    private String anamnesis;

    @NotBlank(message = "El diagnostico es obligatorio")
    @Size(max = 1000)
    private String diagnostico;

    @DecimalMin(value = "0.01", message = "El peso debe ser mayor a 0")
    private BigDecimal pesoRegistrado;

    @DecimalMin(value = "30.0", message = "Temperatura fuera de rango")
    @DecimalMax(value = "45.0", message = "Temperatura fuera de rango")
    private BigDecimal temperatura;

    @Size(max = 1000)
    private String observaciones;

    public AtencionRequest toRequest() {
        return new AtencionRequest(reservaId, null, null, vacioANull(motivoConsulta), vacioANull(anamnesis),
                diagnostico, pesoRegistrado, temperatura, vacioANull(observaciones));
    }

    static String vacioANull(String s) {
        return s == null || s.isBlank() ? null : s;
    }
}
