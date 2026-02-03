package com.andesfin.simulacion_service.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoCandidatoDTO {

    @NotBlank(message = "El nombre del producto es requerido")
    private String nombre;

    @NotNull(message = "El precio es requerido")
    @Positive(message = "El precio debe ser mayor a 0")
    private Double precio;

    @NotNull(message = "El porcentaje de ganancia es requerido")
    @DecimalMin(value = "0.0", message = "El porcentaje no puede ser negativo")
    @DecimalMax(value = "100.0", message = "El porcentaje no puede exceder 100")
    private Double porcentajeGanancia;
}