package cl.ubiobio.petcare.service;

import cl.ubiobio.petcare.client.MedicamentoApiClient;
import cl.ubiobio.petcare.dto.MedicamentoExternoDto;
import cl.ubiobio.petcare.exception.ReglaNegocioException;
import cl.ubiobio.petcare.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/** Consulta de medicamentos en la API externa. */
@Service
@RequiredArgsConstructor
public class MedicamentoService {

    private final MedicamentoApiClient medicamentoApiClient;

    public List<MedicamentoExternoDto> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new ReglaNegocioException("Debe indicar el nombre del medicamento a buscar");
        }
        return medicamentoApiClient.buscarPorNombre(nombre.trim());
    }

    public MedicamentoExternoDto obtenerDetalle(String codigo) {
        return medicamentoApiClient.obtenerPorCodigo(codigo);
    }

    /**
     * Obtiene un unico medicamento a partir de su nombre (usado al prescribir sin codigo).
     * Si el nombre coincide exactamente o hay un solo resultado se usa ese; si hay varios se pide el codigo.
     */
    public MedicamentoExternoDto resolverPorNombre(String nombre) {
        List<MedicamentoExternoDto> resultados = buscarPorNombre(nombre);
        if (resultados.isEmpty()) {
            throw new ResourceNotFoundException("No se encontro ningun medicamento con el nombre '" + nombre + "'");
        }
        return resultados.stream()
                .filter(m -> m.nombre().equalsIgnoreCase(nombre.trim()))
                .findFirst()
                .orElseGet(() -> {
                    if (resultados.size() == 1) {
                        return resultados.get(0);
                    }
                    String opciones = resultados.stream()
                            .map(m -> m.codigo() + " (" + m.nombre() + ")")
                            .collect(Collectors.joining(", "));
                    throw new ReglaNegocioException("Hay varios medicamentos para '" + nombre
                            + "', indique el codigoExterno: " + opciones);
                });
    }
}
