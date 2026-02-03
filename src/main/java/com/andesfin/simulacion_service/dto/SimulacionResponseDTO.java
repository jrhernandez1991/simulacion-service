package com.andesfin.simulacion_service.dto;


import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SimulacionResponseDTO {
    private UUID id;
    private UUID usuarioId;
    private LocalDateTime fechaSimulacion;
    private Double capitalDisponible;
    private List<ProductoSeleccionadoDTO> productosSeleccionados;
    private Double costoTotal;
    private Double capitalRestante;
    private Double gananciaTotal;
    private Double retornoTotalPorcentaje;
    private Double eficienciaCapital;
    private String mensaje;
}
