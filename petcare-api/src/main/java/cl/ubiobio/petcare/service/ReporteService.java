package cl.ubiobio.petcare.service;

import cl.ubiobio.petcare.dto.AgendaVeterinarioResponse;
import cl.ubiobio.petcare.dto.ReservaResponse;
import cl.ubiobio.petcare.dto.ResumenResponse;
import cl.ubiobio.petcare.exception.ReglaNegocioException;
import cl.ubiobio.petcare.model.entity.Reserva;
import cl.ubiobio.petcare.model.enums.EstadoPago;
import cl.ubiobio.petcare.model.enums.EstadoReserva;
import cl.ubiobio.petcare.repository.AtencionRepository;
import cl.ubiobio.petcare.repository.PagoRepository;
import cl.ubiobio.petcare.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Indicadores para el panel de administracion. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReporteService {

    private final ReservaRepository reservaRepository;
    private final AtencionRepository atencionRepository;
    private final PagoRepository pagoRepository;

    /**
     * Reservas por estado, atenciones realizadas, pagos e ingresos del periodo.
     * Sin fechas se usa el mes actual.
     */
    public ResumenResponse resumen(LocalDate desde, LocalDate hasta) {
        LocalDate hoy = LocalDate.now();
        LocalDate inicio = desde != null ? desde : hoy.withDayOfMonth(1);
        LocalDate fin = hasta != null ? hasta : hoy.withDayOfMonth(hoy.lengthOfMonth());
        if (fin.isBefore(inicio)) {
            throw new ReglaNegocioException("La fecha 'hasta' no puede ser anterior a 'desde'");
        }

        Map<EstadoReserva, Long> porEstado = new EnumMap<>(EstadoReserva.class);
        for (EstadoReserva e : EstadoReserva.values()) {
            porEstado.put(e, 0L);
        }
        for (Object[] fila : reservaRepository.contarPorEstado(inicio, fin)) {
            porEstado.put((EstadoReserva) fila[0], (Long) fila[1]);
        }
        long totalReservas = porEstado.values().stream().mapToLong(Long::longValue).sum();

        LocalDateTime desdeDt = inicio.atStartOfDay();
        LocalDateTime hastaDt = fin.atTime(LocalTime.MAX);
        long atenciones = atencionRepository.countByFechaAtencionBetween(desdeDt, hastaDt);
        long pagos = pagoRepository.countByEstadoAndFechaPagoBetween(EstadoPago.PAGADO, desdeDt, hastaDt);
        BigDecimal ingresos = pagoRepository.sumarMontos(EstadoPago.PAGADO, desdeDt, hastaDt);

        return new ResumenResponse(inicio, fin, totalReservas, porEstado, atenciones, pagos, ingresos);
    }

    /** Agenda del dia (por defecto hoy): reservas no canceladas agrupadas por veterinario. */
    public List<AgendaVeterinarioResponse> agenda(LocalDate fecha) {
        LocalDate dia = fecha != null ? fecha : LocalDate.now();
        Map<Long, List<Reserva>> porVeterinario = reservaRepository.buscar(null, dia, null).stream()
                .filter(r -> r.getEstado() != EstadoReserva.CANCELADA)
                .collect(Collectors.groupingBy(r -> r.getVeterinario().getId(), LinkedHashMap::new, Collectors.toList()));

        return porVeterinario.values().stream()
                .map(reservas -> {
                    var v = reservas.get(0).getVeterinario();
                    List<ReservaResponse> lista = reservas.stream().map(ReservaResponse::de).toList();
                    return new AgendaVeterinarioResponse(v.getId(), v.getNombre() + " " + v.getApellido(),
                            dia, lista.size(), lista);
                })
                .toList();
    }
}
