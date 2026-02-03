package com.andesfin.simulacion_service.dto;


import lombok.*;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioDTO {
    private UUID id;
    private String nombre;
    private String email;
    private Double capitalDisponible;
}