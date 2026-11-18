package cl.ubiobio.petcare.service;

import cl.ubiobio.petcare.dto.ClienteRequest;
import cl.ubiobio.petcare.dto.ClienteResponse;
import cl.ubiobio.petcare.dto.MascotaResponse;
import cl.ubiobio.petcare.exception.RegistroDuplicadoException;
import cl.ubiobio.petcare.exception.ResourceNotFoundException;
import cl.ubiobio.petcare.model.entity.Cliente;
import cl.ubiobio.petcare.repository.ClienteRepository;
import cl.ubiobio.petcare.repository.MascotaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Gestion de clientes (CRUD) y consulta de sus mascotas. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final MascotaRepository mascotaRepository;

    public List<ClienteResponse> listar() {
        return clienteRepository.findAll().stream().map(ClienteResponse::de).toList();
    }

    public ClienteResponse buscarPorId(Long id) {
        return ClienteResponse.de(obtener(id));
    }

    public ClienteResponse buscarPorRut(String rut) {
        return clienteRepository.findByRut(rut)
                .map(ClienteResponse::de)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente con RUT " + rut + " no existe"));
    }

    @Transactional
    public ClienteResponse crear(ClienteRequest req) {
        if (clienteRepository.existsByRut(req.rut())) {
            throw new RegistroDuplicadoException("Ya existe un cliente con RUT " + req.rut());
        }
        if (clienteRepository.existsByEmail(req.email())) {
            throw new RegistroDuplicadoException("Ya existe un cliente con email " + req.email());
        }
        Cliente cliente = new Cliente();
        copiarDatos(req, cliente);
        return ClienteResponse.de(clienteRepository.saveAndFlush(cliente));
    }

    @Transactional
    public ClienteResponse actualizar(Long id, ClienteRequest req) {
        Cliente cliente = obtener(id);
        if (clienteRepository.existsByRutAndIdNot(req.rut(), id)) {
            throw new RegistroDuplicadoException("Ya existe otro cliente con RUT " + req.rut());
        }
        if (clienteRepository.existsByEmailAndIdNot(req.email(), id)) {
            throw new RegistroDuplicadoException("Ya existe otro cliente con email " + req.email());
        }
        copiarDatos(req, cliente);
        return ClienteResponse.de(clienteRepository.saveAndFlush(cliente));
    }

    /** Elimina el cliente junto con sus mascotas (y en cascada sus reservas e historial). */
    @Transactional
    public void eliminar(Long id) {
        clienteRepository.delete(obtener(id));
    }

    public List<MascotaResponse> listarMascotas(Long clienteId) {
        obtener(clienteId);
        return mascotaRepository.findByClienteId(clienteId).stream().map(MascotaResponse::de).toList();
    }

    Cliente obtener(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", id));
    }

    private void copiarDatos(ClienteRequest req, Cliente cliente) {
        cliente.setRut(req.rut().toUpperCase());
        cliente.setNombre(req.nombre().trim());
        cliente.setApellido(req.apellido().trim());
        cliente.setEmail(req.email().trim().toLowerCase());
        cliente.setTelefono(req.telefono().trim());
        cliente.setDireccion(req.direccion());
    }
}
