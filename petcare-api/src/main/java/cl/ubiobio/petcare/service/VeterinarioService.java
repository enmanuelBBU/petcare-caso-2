package cl.ubiobio.petcare.service;

import cl.ubiobio.petcare.repository.VeterinarioRepository;
import cl.ubiobio.petcare.repository.HorarioVeterinarioRepository;
import cl.ubiobio.petcare.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** Gestion de veterinarios, sus horarios y disponibilidad. */
@Service
@RequiredArgsConstructor
public class VeterinarioService {

    private final VeterinarioRepository veterinarioRepository;
    private final HorarioVeterinarioRepository horarioVeterinarioRepository;
    private final ReservaRepository reservaRepository;

    // TODO: CRUD de veterinarios
    // TODO: agregar/quitar bloques de horario semanal
    // TODO: calcularDisponibilidad(veterinarioId, fecha): bloques del dia menos reservas activas
}
