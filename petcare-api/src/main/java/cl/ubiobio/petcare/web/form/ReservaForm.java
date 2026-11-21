package cl.ubiobio.petcare.web.form;

import cl.ubiobio.petcare.dto.ReservaModificarRequest;
import cl.ubiobio.petcare.dto.ReservaRequest;
import cl.ubiobio.petcare.dto.ReservaResponse;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

/** Formulario para solicitar o modificar una reserva; mismas validaciones que ReservaRequest. */
@Data
@NoArgsConstructor
public class ReservaForm {

    @NotNull(message = "Seleccione la mascota")
    private Long mascotaId;

    @NotNull(message = "Seleccione el veterinario")
    private Long veterinarioId;

    private Long tipoServicioId;

    @NotNull(message = "La fecha es obligatoria")
    @FutureOrPresent(message = "La fecha no puede ser pasada")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fecha;

    @NotNull(message = "La hora es obligatoria")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime hora;

    @Size(max = 255)
    private String motivo;

    public static ReservaForm de(ReservaResponse r) {
        ReservaForm f = new ReservaForm();
        f.mascotaId = r.mascotaId();
        f.veterinarioId = r.veterinarioId();
        f.tipoServicioId = r.tipoServicioId();
        f.fecha = r.fecha();
        f.hora = r.hora();
        f.motivo = r.motivo();
        return f;
    }

    public ReservaRequest toRequest() {
        return new ReservaRequest(mascotaId, veterinarioId, tipoServicioId, null, fecha, hora, vacioANull(motivo));
    }

    public ReservaModificarRequest toModificarRequest() {
        return new ReservaModificarRequest(veterinarioId, tipoServicioId, fecha, hora, vacioANull(motivo));
    }

    private static String vacioANull(String s) {
        return s == null || s.isBlank() ? null : s;
    }
}
