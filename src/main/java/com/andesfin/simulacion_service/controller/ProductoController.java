package com.andesfin.simulacion_service.controller;

import com.andesfin.simulacion_service.dto.ProductoDTO;
import com.andesfin.simulacion_service.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping
    public ResponseEntity<List<ProductoDTO>> getProductosActivos() {
        List<ProductoDTO> productos = productoService.getProductosActivos();
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<ProductoDTO>> getProductosDisponibles(
            @RequestParam Double capitalMaximo) {
        List<ProductoDTO> productos = productoService.getProductosDisponiblesPorCapital(capitalMaximo);
        return ResponseEntity.ok(productos);
    }

    @PostMapping
    public ResponseEntity<ProductoDTO> createProducto(@RequestBody ProductoDTO productoDTO) {
        ProductoDTO nuevoProducto = productoService.createProducto(productoDTO);
        return ResponseEntity.ok(nuevoProducto);
    }
}