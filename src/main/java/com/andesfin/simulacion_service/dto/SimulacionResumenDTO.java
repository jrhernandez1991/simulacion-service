package com.andesfin.simulacion_service.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SimulacionResumenDTO {
    private UUID id;
    private UUID usuarioId;
    private LocalDateTime fechaSimulacion;
    private Double capitalDisponible;
    private Double gananciaTotal;
    private Integer cantidadProductos;
    private Double retornoPorcentaje;
}