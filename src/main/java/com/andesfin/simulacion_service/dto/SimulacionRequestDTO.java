package com.andesfin.simulacion_service.dto;


import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SimulacionRequestDTO {

    @NotNull(message = "El usuario ID es requerido")
    private UUID usuarioId;

    @NotNull(message = "El capital disponible es requerido")
    @Positive(message = "El capital debe ser mayor a 0")
    private Double capitalDisponible;

    @NotEmpty(message = "La lista de productos no puede estar vacía")
    private List<ProductoCandidatoDTO> productos;
}