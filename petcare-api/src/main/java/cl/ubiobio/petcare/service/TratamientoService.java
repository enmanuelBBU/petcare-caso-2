package cl.ubiobio.petcare.service;

import cl.ubiobio.petcare.repository.TratamientoRepository;
import cl.ubiobio.petcare.repository.AtencionRepository;
import cl.ubiobio.petcare.repository.MedicamentoPrescritoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** Tratamientos y medicamentos prescritos. */
@Service
@RequiredArgsConstructor
public class TratamientoService {

    private final TratamientoRepository tratamientoRepository;
    private final AtencionRepository atencionRepository;
    private final MedicamentoPrescritoRepository medicamentoPrescritoRepository;

    // TODO: crear tratamiento para una atencion
    // TODO: agregar medicamento prescrito (datos traidos de MedicamentoService)
    // TODO: listar tratamientos por atencion o mascota
}
