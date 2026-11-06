package cl.ubiobio.petcare.service;

import cl.ubiobio.petcare.client.MedicamentoApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** Consulta de medicamentos en la API externa. */
@Service
@RequiredArgsConstructor
public class MedicamentoService {

    private final MedicamentoApiClient medicamentoApiClient;

    // TODO: buscarPorNombre(nombre)
    // TODO: obtenerDetalle(codigo)
}
