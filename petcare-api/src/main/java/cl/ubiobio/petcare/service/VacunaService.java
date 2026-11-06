package cl.ubiobio.petcare.service;

import cl.ubiobio.petcare.repository.VacunaRepository;
import cl.ubiobio.petcare.repository.MascotaRepository;
import cl.ubiobio.petcare.repository.VeterinarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** Carnet de vacunacion. */
@Service
@RequiredArgsConstructor
public class VacunaService {

    private final VacunaRepository vacunaRepository;
    private final MascotaRepository mascotaRepository;
    private final VeterinarioRepository veterinarioRepository;

    // TODO: registrar vacuna
    // TODO: listar por mascota
    // TODO: proximasDosis(dias): vacunas por vencer (para recordatorios)
}
