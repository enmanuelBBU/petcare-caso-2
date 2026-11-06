package cl.ubiobio.petcare.service;

import cl.ubiobio.petcare.repository.ReservaRepository;
import cl.ubiobio.petcare.repository.MascotaRepository;
import cl.ubiobio.petcare.repository.VeterinarioRepository;
import cl.ubiobio.petcare.repository.HorarioVeterinarioRepository;
import cl.ubiobio.petcare.repository.HistorialEstadoReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** Reglas de negocio de las horas medicas. */
@Service
@RequiredArgsConstructor
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final MascotaRepository mascotaRepository;
    private final VeterinarioRepository veterinarioRepository;
    private final HorarioVeterinarioRepository horarioVeterinarioRepository;
    private final HistorialEstadoReservaRepository historialEstadoReservaRepository;

    // TODO: crear: validar horario del veterinario e impedir duplicados (existsBy...EstadoNot CANCELADA)
    // TODO: modificar fecha/hora (re-validar duplicados)
    // TODO: confirmar / cancelar / marcarAtendida: validar transicion de estado y registrar HistorialEstadoReserva
    // TODO: al confirmar o cancelar: llamar a NotificacionService
    // TODO: listar por estado, fecha, mascota o cliente
}
