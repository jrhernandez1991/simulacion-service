package com.andesfin.simulacion_service.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "productos_financieros")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoFinanciero {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "costo", nullable = false, precision = 10, scale = 2)
    private Double costo;

    @Column(name = "porcentaje_retorno", nullable = false, precision = 5, scale = 2)
    private Double porcentajeRetorno;

    @Column(name = "activo", nullable = false)
    private Boolean activo;

    @Column(name = "riesgo")
    private Integer riesgo;
}