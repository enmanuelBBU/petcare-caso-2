package cl.ubiobio.petcare.service;

import cl.ubiobio.petcare.repository.TipoServicioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** Catalogo de servicios con duracion y precio. */
@Service
@RequiredArgsConstructor
public class TipoServicioService {

    private final TipoServicioRepository tipoServicioRepository;

    // TODO: CRUD del catalogo
    // TODO: listarActivos
}
