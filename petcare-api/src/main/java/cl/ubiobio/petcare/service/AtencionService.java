package cl.ubiobio.petcare.service;

import cl.ubiobio.petcare.repository.AtencionRepository;
import cl.ubiobio.petcare.repository.ReservaRepository;
import cl.ubiobio.petcare.repository.MascotaRepository;
import cl.ubiobio.petcare.repository.VeterinarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** Registro de atenciones (ficha clinica). */
@Service
@RequiredArgsConstructor
public class AtencionService {

    private final AtencionRepository atencionRepository;
    private final ReservaRepository reservaRepository;
    private final MascotaRepository mascotaRepository;
    private final VeterinarioRepository veterinarioRepository;

    // TODO: registrar atencion (desde reserva: marca la reserva ATENDIDA)
    // TODO: actualizar diagnostico/observaciones
    // TODO: historialPorMascota
}
