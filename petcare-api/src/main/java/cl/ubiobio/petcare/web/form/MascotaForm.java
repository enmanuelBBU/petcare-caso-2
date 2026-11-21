package cl.ubiobio.petcare.web.form;

import cl.ubiobio.petcare.dto.MascotaRequest;
import cl.ubiobio.petcare.dto.MascotaResponse;
import cl.ubiobio.petcare.model.enums.Sexo;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Formulario de mascota; mismas validaciones que MascotaRequest. */
@Data
@NoArgsConstructor
public class MascotaForm {

    @NotNull(message = "Seleccione el dueño")
    private Long clienteId;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 80)
    private String nombre;

    @NotBlank(message = "La especie es obligatoria")
    @Size(max = 40)
    private String especie;

    @Size(max = 80)
    private String raza;

    private Sexo sexo;

    @PastOrPresent(message = "La fecha de nacimiento no puede ser futura")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaNacimiento;

    @DecimalMin(value = "0.01", message = "El peso debe ser mayor a 0")
    private BigDecimal peso;

    @Size(max = 40)
    private String color;

    @Size(max = 30)
    private String microchip;

    public static MascotaForm de(MascotaResponse m) {
        MascotaForm f = new MascotaForm();
        f.clienteId = m.clienteId();
        f.nombre = m.nombre();
        f.especie = m.especie();
        f.raza = m.raza();
        f.sexo = m.sexo();
        f.fechaNacimiento = m.fechaNacimiento();
        f.peso = m.peso();
        f.color = m.color();
        f.microchip = m.microchip();
        return f;
    }

    public MascotaRequest toRequest() {
        return new MascotaRequest(clienteId, nombre, especie, vacioANull(raza), sexo, fechaNacimiento, peso,
                vacioANull(color), vacioANull(microchip));
    }

    private static String vacioANull(String s) {
        return s == null || s.isBlank() ? null : s;
    }
}
