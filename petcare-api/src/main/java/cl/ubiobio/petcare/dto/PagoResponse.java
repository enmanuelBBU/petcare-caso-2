package cl.ubiobio.petcare.dto;

import cl.ubiobio.petcare.model.entity.Cliente;
import cl.ubiobio.petcare.model.entity.Pago;
import cl.ubiobio.petcare.model.entity.Reserva;
import cl.ubiobio.petcare.model.enums.EstadoPago;
import cl.ubiobio.petcare.model.enums.MetodoPago;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Pago devuelto por la API, con los datos basicos de la reserva pagada. */
public record PagoResponse(
        Long id,
        Long reservaId,
        String mascotaNombre,
        String clienteNombre,
        String tipoServicioNombre,
        BigDecimal monto,
        MetodoPago metodo,
        EstadoPago estado,
        LocalDateTime fechaPago
) {
    public static PagoResponse de(Pago p) {
        Reserva r = p.getReserva();
        Cliente c = r.getMascota().getCliente();
        return new PagoResponse(p.getId(), r.getId(), r.getMascota().getNombre(),
                c.getNombre() + " " + c.getApellido(),
                r.getTipoServicio() != null ? r.getTipoServicio().getNombre() : null,
                p.getMonto(), p.getMetodo(), p.getEstado(), p.getFechaPago());
    }
}
