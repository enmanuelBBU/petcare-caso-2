package cl.ubiobio.petcare.dto;

import cl.ubiobio.petcare.model.entity.TipoServicio;

import java.math.BigDecimal;

/** Servicio del catalogo devuelto por la API. */
public record TipoServicioResponse(
        Long id,
        String nombre,
        String descripcion,
        Integer duracionMinutos,
        BigDecimal precio,
        Boolean activo
) {
    public static TipoServicioResponse de(TipoServicio t) {
        return new TipoServicioResponse(t.getId(), t.getNombre(), t.getDescripcion(), t.getDuracionMinutos(),
                t.getPrecio(), t.getActivo());
    }
}
