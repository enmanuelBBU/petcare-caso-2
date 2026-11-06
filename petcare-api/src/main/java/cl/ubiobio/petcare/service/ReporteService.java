package cl.ubiobio.petcare.service;

import cl.ubiobio.petcare.repository.ReservaRepository;
import cl.ubiobio.petcare.repository.AtencionRepository;
import cl.ubiobio.petcare.repository.PagoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** Indicadores para el panel de administracion. */
@Service
@RequiredArgsConstructor
public class ReporteService {

    private final ReservaRepository reservaRepository;
    private final AtencionRepository atencionRepository;
    private final PagoRepository pagoRepository;

    // TODO: resumen: reservas por estado, atenciones del mes, ingresos del periodo
    // TODO: agenda del dia por veterinario
}
