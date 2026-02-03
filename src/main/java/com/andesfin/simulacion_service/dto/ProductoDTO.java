package com.andesfin.simulacion_service.dto;

import lombok.*;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoDTO {
    private UUID id;
    private String nombre;
    private String descripcion;
    private Double costo;
    private Double porcentajeRetorno;
    private Boolean activo;
    private Integer riesgo;
}