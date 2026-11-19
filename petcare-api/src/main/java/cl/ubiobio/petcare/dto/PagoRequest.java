package cl.ubiobio.petcare.dto;

import cl.ubiobio.petcare.model.enums.MetodoPago;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/** Pago de una reserva. Si no se indica monto, se cobra el precio del tipo de servicio de la reserva. */
public record PagoRequest(
        @NotNull(message = "El reservaId es obligatorio")
        Long reservaId,

        @DecimalMin(value = "0", message = "El monto no puede ser negativo")
        BigDecimal monto,

        @NotNull(message = "El metodo de pago es obligatorio (EFECTIVO, DEBITO, CREDITO o TRANSFERENCIA)")
        MetodoPago metodo
) {
}
