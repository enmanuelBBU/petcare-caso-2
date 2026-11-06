package cl.ubiobio.petcare.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Cliente de la API externa de medicamentos (apis-externas, Node).
 *   GET {base-url}/api/medicamentos?nombre={nombre}
 *   GET {base-url}/api/medicamentos/{codigo}
 */
@Component
@RequiredArgsConstructor
public class MedicamentoApiClient {

    private final RestClient apisExternasRestClient;

    // TODO: buscarPorNombre(String nombre) -> List<MedicamentoExternoDto>
    // TODO: obtenerPorCodigo(String codigo) -> MedicamentoExternoDto
    // TODO: envolver RestClientException en ApiExternaException
}
