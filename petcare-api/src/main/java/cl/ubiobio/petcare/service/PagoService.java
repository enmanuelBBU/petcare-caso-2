package cl.ubiobio.petcare.service;

import cl.ubiobio.petcare.dto.PagoRequest;
import cl.ubiobio.petcare.dto.PagoResponse;
import cl.ubiobio.petcare.exception.RegistroDuplicadoException;
import cl.ubiobio.petcare.exception.ReglaNegocioException;
import cl.ubiobio.petcare.exception.ResourceNotFoundException;
import cl.ubiobio.petcare.model.entity.Pago;
import cl.ubiobio.petcare.model.entity.Reserva;
import cl.ubiobio.petcare.model.enums.EstadoPago;
import cl.ubiobio.petcare.model.enums.EstadoReserva;
import cl.ubiobio.petcare.repository.PagoRepository;
import cl.ubiobio.petcare.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** Pagos de las reservas. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PagoService {

    private final PagoRepository pagoRepository;
    private final ReservaRepository reservaRepository;

    /**
     * Registra un pago PAGADO para la reserva.
     * - La reserva no puede estar CANCELADA ni tener ya un pago PAGADO (primero hay que anularlo).
     * - Monto por defecto = precio del tipo de servicio de la reserva.
     */
    @Transactional
    public PagoResponse registrar(PagoRequest req) {
        Reserva reserva = reservaRepository.findById(req.reservaId())
                .orElseThrow(() -> new ResourceNotFoundException("Reserva", req.reservaId()));
        if (reserva.getEstado() == EstadoReserva.CANCELADA) {
            throw new ReglaNegocioException("No se puede pagar una reserva CANCELADA");
        }
        if (pagoRepository.existsByReservaIdAndEstado(reserva.getId(), EstadoPago.PAGADO)) {
            throw new RegistroDuplicadoException("La reserva " + reserva.getId() + " ya tiene un pago registrado");
        }
        BigDecimal monto = req.monto();
        if (monto == null) {
            if (reserva.getTipoServicio() == null) {
                throw new ReglaNegocioException("La reserva no tiene tipo de servicio: indique el monto del pago");
            }
            monto = reserva.getTipoServicio().getPrecio();
        }
        Pago p = new Pago();
        p.setReserva(reserva);
        p.setMonto(monto);
        p.setMetodo(req.metodo());
        p.setEstado(EstadoPago.PAGADO);
        p.setFechaPago(LocalDateTime.now());
        return PagoResponse.de(pagoRepository.save(p));
    }

    /** Anula un pago (ej. devolucion). Deja de contar en los ingresos. */
    @Transactional
    public PagoResponse anular(Long id) {
        Pago p = pagoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Pago", id));
        if (p.getEstado() == EstadoPago.ANULADO) {
            throw new ReglaNegocioException("El pago " + id + " ya esta anulado");
        }
        p.setEstado(EstadoPago.ANULADO);
        return PagoResponse.de(pagoRepository.save(p));
    }

    public List<PagoResponse> listarPorReserva(Long reservaId) {
        if (!reservaRepository.existsById(reservaId)) {
            throw new ResourceNotFoundException("Reserva", reservaId);
        }
        return pagoRepository.findByReservaId(reservaId).stream().map(PagoResponse::de).toList();
    }
}
