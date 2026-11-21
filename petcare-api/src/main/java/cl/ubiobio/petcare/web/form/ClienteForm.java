package cl.ubiobio.petcare.web.form;

import cl.ubiobio.petcare.dto.ClienteRequest;
import cl.ubiobio.petcare.dto.ClienteResponse;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Formulario de cliente (mutable para th:field); mismas validaciones que ClienteRequest. */
@Data
@NoArgsConstructor
public class ClienteForm {

    @NotBlank(message = "El RUT es obligatorio")
    @Pattern(regexp = "^\\d{7,8}-[\\dkK]$", message = "El RUT debe tener el formato 12345678-9")
    private String rut;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 80)
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 80)
    private String apellido;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no es valido")
    @Size(max = 120)
    private String email;

    @NotBlank(message = "El telefono es obligatorio")
    @Size(max = 20)
    private String telefono;

    @Size(max = 200)
    private String direccion;

    public static ClienteForm de(ClienteResponse c) {
        ClienteForm f = new ClienteForm();
        f.rut = c.rut();
        f.nombre = c.nombre();
        f.apellido = c.apellido();
        f.email = c.email();
        f.telefono = c.telefono();
        f.direccion = c.direccion();
        return f;
    }

    public ClienteRequest toRequest() {
        return new ClienteRequest(rut, nombre, apellido, email, telefono, direccion);
    }
}
