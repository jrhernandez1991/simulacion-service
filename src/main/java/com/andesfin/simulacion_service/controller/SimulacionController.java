package com.andesfin.simulacion_service.controller;

import com.andesfin.simulacion_service.dto.SimulacionRequestDTO;
import com.andesfin.simulacion_service.dto.SimulacionResponseDTO;
import com.andesfin.simulacion_service.dto.SimulacionResumenDTO;
import com.andesfin.simulacion_service.service.SimulacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/simulaciones")
@RequiredArgsConstructor
@Tag(name = "Simulaciones", description = "API para simulaciones de inversión")
public class SimulacionController {

    private final SimulacionService simulacionService;

    @PostMapping
    @Operation(summary = "Realizar una simulación de inversión")
    public ResponseEntity<?> realizarSimulacion(@Valid @RequestBody SimulacionRequestDTO request) {
        try {
            SimulacionResponseDTO respuesta = simulacionService.realizarSimulacion(request);
            return ResponseEntity.ok(respuesta);
        } catch (RuntimeException e) {
            // Manejo específico para fondos insuficientes
            if (e.getMessage().contains("Fondos insuficientes")) {
                return ResponseEntity.badRequest()
                        .body(crearRespuestaErrorFondos(e.getMessage(), request.getCapitalDisponible()));
            }

            // Para otros errores
            return ResponseEntity.badRequest()
                    .body(crearRespuestaError(e.getMessage()));
        }
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Consultar simulaciones de un usuario")
    public ResponseEntity<List<SimulacionResumenDTO>> getSimulacionesPorUsuario(
            @PathVariable UUID usuarioId) {
        List<SimulacionResumenDTO> simulaciones = simulacionService.getSimulacionesPorUsuario(usuarioId);
        return ResponseEntity.ok(simulaciones);
    }

    private Object crearRespuestaError(String mensaje) {
        return new Object() {
            public final String error = "Error en la simulación";
            public final String detalle = mensaje;
            public final String timestamp = java.time.LocalDateTime.now().toString();
        };
    }

    private Object crearRespuestaErrorFondos(String mensaje, Double capitalDisponible) {
        return new Object() {
            public final String error = "Fondos insuficientes";
            public final String detalle = mensaje;
            public final Double capital_disponible = capitalDisponible;
            public final String recomendacion = "Aumente su capital o consulte productos con menor inversión mínima";
            public final String timestamp = java.time.LocalDateTime.now().toString();
        };
    }
}
