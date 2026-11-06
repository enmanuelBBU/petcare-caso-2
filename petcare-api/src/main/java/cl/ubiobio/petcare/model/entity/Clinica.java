package cl.ubiobio.petcare.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalTime;

/** Sede de la clinica. Latitud/longitud se usan para la geolocalizacion. */
@Entity
@Table(name = "clinica")
@Getter
@Setter
@NoArgsConstructor
public class Clinica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(nullable = false, length = 200)
    private String direccion;

    @Column(length = 80)
    private String comuna;

    @Column(nullable = false, length = 80)
    private String ciudad;

    @Column(length = 20)
    private String telefono;

    @Column(length = 120)
    private String email;

    @Column(precision = 10, scale = 7)
    private BigDecimal latitud;

    @Column(precision = 10, scale = 7)
    private BigDecimal longitud;

    @Column(name = "hora_apertura")
    private LocalTime horaApertura;

    @Column(name = "hora_cierre")
    private LocalTime horaCierre;
}
