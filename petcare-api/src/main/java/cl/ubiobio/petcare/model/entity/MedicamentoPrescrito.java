package cl.ubiobio.petcare.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Medicamento recetado dentro de un tratamiento (datos obtenidos de la API externa). */
@Entity
@Table(name = "medicamento_prescrito")
@Getter
@Setter
@NoArgsConstructor
public class MedicamentoPrescrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tratamiento_id", nullable = false)
    private Tratamiento tratamiento;

    /** Identificador del medicamento en la API externa. */
    @Column(name = "codigo_externo", length = 60)
    private String codigoExterno;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(name = "principio_activo", length = 120)
    private String principioActivo;

    @Column(nullable = false, length = 60)
    private String dosis;

    @Column(nullable = false, length = 60)
    private String frecuencia;

    @Column(name = "duracion_dias")
    private Integer duracionDias;

    @Column(name = "via_administracion", length = 40)
    private String viaAdministracion;
}
