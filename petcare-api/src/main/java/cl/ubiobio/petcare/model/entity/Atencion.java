package cl.ubiobio.petcare.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Ficha clinica de una consulta: conforma el historial medico de la mascota. */
@Entity
@Table(name = "atencion")
@Getter
@Setter
@NoArgsConstructor
public class Atencion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserva_id", unique = true)
    private Reserva reserva;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mascota_id", nullable = false)
    private Mascota mascota;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "veterinario_id", nullable = false)
    private Veterinario veterinario;

    @Column(name = "fecha_atencion", nullable = false)
    private LocalDateTime fechaAtencion = LocalDateTime.now();

    @Column(name = "motivo_consulta", length = 255)
    private String motivoConsulta;

    @Column(length = 1000)
    private String anamnesis;

    @Column(nullable = false, length = 1000)
    private String diagnostico;

    @Column(name = "peso_registrado", precision = 6, scale = 2)
    private BigDecimal pesoRegistrado;

    @Column(precision = 4, scale = 1)
    private BigDecimal temperatura;

    @Column(length = 1000)
    private String observaciones;

    @OneToMany(mappedBy = "atencion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tratamiento> tratamientos = new ArrayList<>();
}
