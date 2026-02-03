package com.andesfin.simulacion_service.controller;

import com.andesfin.simulacion_service.dto.UsuarioDTO;
import com.andesfin.simulacion_service.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> getAllUsuarios() {
        List<UsuarioDTO> usuarios = usuarioService.getAllUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> getUsuarioById(@PathVariable UUID id) {
        return usuarioService.getUsuarioById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UsuarioDTO> createUsuario(@RequestBody UsuarioDTO usuarioDTO) {
        UsuarioDTO nuevoUsuario = usuarioService.createUsuario(usuarioDTO);
        return ResponseEntity.ok(nuevoUsuario);
    }

    @DeleteMapping("/{id}")

    public ResponseEntity<Void> deleteUsuario(@PathVariable UUID id) {
        usuarioService.deleteUsuario(id);
        return ResponseEntity.noContent().build();
    }
}