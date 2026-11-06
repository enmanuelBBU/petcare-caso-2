package cl.ubiobio.petcare.service;

import cl.ubiobio.petcare.repository.MascotaRepository;
import cl.ubiobio.petcare.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** Gestion de mascotas asociadas a un cliente. */
@Service
@RequiredArgsConstructor
public class MascotaService {

    private final MascotaRepository mascotaRepository;
    private final ClienteRepository clienteRepository;

    // TODO: listar, buscarPorId
    // TODO: crear (asociar a cliente existente)
    // TODO: actualizar, eliminar
    // TODO: historialClinico (atenciones + vacunas)
}
