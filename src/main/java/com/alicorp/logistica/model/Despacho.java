package com.alicorp.logistica.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data // Lombok genera los Getters, Setters y constructores automáticamente
@Entity
@Table(name = "despachos")
public class Despacho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dia_semana")
    private Integer diaSemana;

    @Column(name = "hora_llegada")
    private Integer horaLlegada;

    @Column(name = "toneladas_carga")
    private Double toneladasCarga;

    @Column(name = "tipo_cliente")
    private Integer tipoCliente;

    @Column(name = "indice_congestion")
    private Double indiceCongestion;

    @Column(name = "deathtime_predicho")
    private Integer deathtimePredicho;

    @Column(name = "deathtime_real")
    private Integer deathtimeReal; // Se llenaría después en la vida real

    @Column(name = "recomendacion_sistema")
    private String recomendacionSistema;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro = LocalDateTime.now();
}