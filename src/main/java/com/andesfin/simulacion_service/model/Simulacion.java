package com.andesfin.simulacion_service.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "simulaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Simulacion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "fecha_simulacion", nullable = false)
    private LocalDateTime fechaSimulacion;

    @Column(name = "capital_disponible", nullable = false, precision = 10, scale = 2)
    private Double capitalDisponible;

    @Column(name = "costo_total", nullable = false, precision = 10, scale = 2)
    private Double costoTotal;

    @Column(name = "ganancia_total", nullable = false, precision = 10, scale = 2)
    private Double gananciaTotal;

    @Column(name = "capital_restante", nullable = false, precision = 10, scale = 2)
    private Double capitalRestante;

    @Column(name = "retorno_total_porcentaje", precision = 5, scale = 2)
    private Double retornoTotalPorcentaje;

    @Column(name = "eficiencia_capital", precision = 5, scale = 2)
    private Double eficienciaCapital;

    @Column(name = "mensaje")
    private String mensaje;

    @Column(name = "productos_seleccionados", columnDefinition = "JSON")
    private String productosSeleccionados;
}
