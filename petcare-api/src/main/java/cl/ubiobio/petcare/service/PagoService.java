package cl.ubiobio.petcare.service;

import cl.ubiobio.petcare.repository.PagoRepository;
import cl.ubiobio.petcare.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** Pagos de las reservas. */
@Service
@RequiredArgsConstructor
public class PagoService {

    private final PagoRepository pagoRepository;
    private final ReservaRepository reservaRepository;

    // TODO: registrar pago (monto por defecto = precio del tipo de servicio)
    // TODO: anular pago
    // TODO: listar pagos por reserva
}
