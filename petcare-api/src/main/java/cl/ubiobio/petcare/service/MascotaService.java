package cl.ubiobio.petcare.service;

import cl.ubiobio.petcare.dto.MascotaRequest;
import cl.ubiobio.petcare.dto.MascotaResponse;
import cl.ubiobio.petcare.exception.ResourceNotFoundException;
import cl.ubiobio.petcare.model.entity.Cliente;
import cl.ubiobio.petcare.model.entity.Mascota;
import cl.ubiobio.petcare.repository.ClienteRepository;
import cl.ubiobio.petcare.repository.MascotaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Gestion de mascotas asociadas a un cliente. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MascotaService {

    private final MascotaRepository mascotaRepository;
    private final ClienteRepository clienteRepository;

    public List<MascotaResponse> listar() {
        return mascotaRepository.findAll().stream().map(MascotaResponse::de).toList();
    }

    public MascotaResponse buscarPorId(Long id) {
        return MascotaResponse.de(obtener(id));
    }

    @Transactional
    public MascotaResponse crear(MascotaRequest req) {
        Mascota mascota = new Mascota();
        copiarDatos(req, mascota);
        obtenerCliente(req.clienteId()).agregarMascota(mascota);
        return MascotaResponse.de(mascotaRepository.saveAndFlush(mascota));
    }

    /** Actualiza los datos de la mascota; si cambia el clienteId, la mascota se traspasa a otro dueno. */
    @Transactional
    public MascotaResponse actualizar(Long id, MascotaRequest req) {
        Mascota mascota = obtener(id);
        copiarDatos(req, mascota);
        if (!mascota.getCliente().getId().equals(req.clienteId())) {
            mascota.setCliente(obtenerCliente(req.clienteId()));
        }
        return MascotaResponse.de(mascotaRepository.saveAndFlush(mascota));
    }

    @Transactional
    public void eliminar(Long id) {
        Mascota mascota = obtener(id);
        mascota.getCliente().quitarMascota(mascota);
        mascotaRepository.delete(mascota);
    }

    Mascota obtener(Long id) {
        return mascotaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mascota", id));
    }

    private Cliente obtenerCliente(Long clienteId) {
        return clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", clienteId));
    }

    private void copiarDatos(MascotaRequest req, Mascota mascota) {
        mascota.setNombre(req.nombre().trim());
        mascota.setEspecie(req.especie().trim());
        mascota.setRaza(req.raza());
        mascota.setSexo(req.sexo());
        mascota.setFechaNacimiento(req.fechaNacimiento());
        mascota.setPeso(req.peso());
        mascota.setColor(req.color());
        mascota.setMicrochip(req.microchip() == null || req.microchip().isBlank() ? null : req.microchip().trim());
    }
}
