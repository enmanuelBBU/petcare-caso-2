package cl.ubiobio.petcare.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuracion de las APIs externas (servidor Node en otro equipo/red).
 * Propiedades: petcare.apis.base-url y petcare.apis.timeout-ms
 */
@ConfigurationProperties(prefix = "petcare.apis")
public record ApisExternasProperties(String baseUrl, int timeoutMs) {
}
