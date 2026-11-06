package cl.ubiobio.petcare.model.entity;

import cl.ubiobio.petcare.model.enums.CanalNotificacion;
import cl.ubiobio.petcare.model.enums.EstadoNotificacion;
import cl.ubiobio.petcare.model.enums.TipoNotificacion;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/** Mensaje enviado (o por enviar) a un cliente mediante la API externa de notificaciones. */
@Entity
@Table(name = "notificacion")
@Getter
@Setter
@NoArgsConstructor
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserva_id")
    private Reserva reserva;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private CanalNotificacion canal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private TipoNotificacion tipo;

    @Column(nullable = false, length = 120)
    private String destinatario;

    @Column(length = 150)
    private String asunto;

    @Column(nullable = false, length = 1000)
    private String mensaje;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private EstadoNotificacion estado = EstadoNotificacion.PENDIENTE;

    @Column(name = "respuesta_api", length = 500)
    private String respuestaApi;

    @CreationTimestamp
    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "enviado_en")
    private LocalDateTime enviadoEn;
}
