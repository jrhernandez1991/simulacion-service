package com.andesfin.simulacion_service.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "productos_simulacion")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoSimulacion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "simulacion_id", nullable = false)
    private Simulacion simulacion;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private ProductoFinanciero producto;

    @Column(name = "costo", precision = 10, scale = 2)
    private Double costo;

    @Column(name = "porcentaje_ganancia", precision = 5, scale = 2)
    private Double porcentajeGanancia;

    @Column(name = "ganancia_esperada", precision = 10, scale = 2)
    private Double gananciaEsperada;
}