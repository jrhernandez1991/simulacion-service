package com.andesfin.simulacion_service.service;

import com.andesfin.simulacion_service.dto.UsuarioDTO;
import com.andesfin.simulacion_service.model.Usuario;
import com.andesfin.simulacion_service.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    private UsuarioDTO convertToDTO(Usuario usuario) {
        return UsuarioDTO.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .capitalDisponible(usuario.getCapitalDisponible())
                .build();
    }

    private Usuario convertToEntity(UsuarioDTO usuarioDTO) {
        return Usuario.builder()
                .id(usuarioDTO.getId())
                .nombre(usuarioDTO.getNombre())
                .email(usuarioDTO.getEmail())
                .capitalDisponible(usuarioDTO.getCapitalDisponible())
                .build();
    }

    public List<UsuarioDTO> getAllUsuarios() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<UsuarioDTO> getUsuarioById(UUID id) {
        return usuarioRepository.findById(id)
                .map(this::convertToDTO);
    }

    public UsuarioDTO createUsuario(UsuarioDTO usuarioDTO) {
        if (usuarioRepository.existsByEmail(usuarioDTO.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        Usuario usuario = convertToEntity(usuarioDTO);
        Usuario savedUsuario = usuarioRepository.save(usuario);
        return convertToDTO(savedUsuario);
    }

    public void deleteUsuario(UUID id) {
        usuarioRepository.deleteById(id);
    }

    public Optional<Usuario> findUsuarioEntityById(UUID id) {
        return usuarioRepository.findById(id);
    }
}