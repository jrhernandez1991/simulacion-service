package com.andesfin.simulacion_service.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoSeleccionadoDTO {
    private String nombre;
    private Double precio;
    private Double porcentajeGanancia;
    private Double gananciaEsperada;
}