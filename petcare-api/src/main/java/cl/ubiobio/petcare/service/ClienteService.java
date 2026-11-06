package cl.ubiobio.petcare.service;

import cl.ubiobio.petcare.repository.ClienteRepository;
import cl.ubiobio.petcare.repository.MascotaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** Gestion de clientes (CRUD) y consulta de sus mascotas. */
@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final MascotaRepository mascotaRepository;

    // TODO: listar, buscarPorId, buscarPorRut
    // TODO: crear (validar RUT y email unicos)
    // TODO: actualizar, eliminar
    // TODO: listarMascotasDelCliente
}
