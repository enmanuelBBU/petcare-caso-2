package cl.ubiobio.petcare.client;

import cl.ubiobio.petcare.dto.MedicamentoExternoDto;
import cl.ubiobio.petcare.exception.ApiExternaException;
import cl.ubiobio.petcare.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

/**
 * Cliente de la API externa de medicamentos (apis-externas, Node).
 *   GET {base-url}/api/medicamentos?nombre={nombre}
 *   GET {base-url}/api/medicamentos/{codigo}
 */
@Component
@RequiredArgsConstructor
public class MedicamentoApiClient {

    private final RestClient apisExternasRestClient;

    public List<MedicamentoExternoDto> buscarPorNombre(String nombre) {
        try {
            List<MedicamentoExternoDto> resultado = apisExternasRestClient.get()
                    .uri(b -> b.path("/api/medicamentos").queryParam("nombre", nombre).build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<MedicamentoExternoDto>>() { });
            return resultado != null ? resultado : List.of();
        } catch (RestClientException e) {
            throw new ApiExternaException("No se pudo consultar la API de medicamentos: " + e.getMessage(), e);
        }
    }

    public MedicamentoExternoDto obtenerPorCodigo(String codigo) {
        try {
            return apisExternasRestClient.get()
                    .uri("/api/medicamentos/{codigo}", codigo)
                    .retrieve()
                    .body(MedicamentoExternoDto.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("Medicamento " + codigo + " no existe en la API externa");
        } catch (RestClientException e) {
            throw new ApiExternaException("No se pudo consultar la API de medicamentos: " + e.getMessage(), e);
        }
    }
}
