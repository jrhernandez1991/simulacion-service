package com.andesfin.simulacion_service.service;

import com.andesfin.simulacion_service.dto.ProductoDTO;
import com.andesfin.simulacion_service.model.ProductoFinanciero;
import com.andesfin.simulacion_service.repository.ProductoFinancieroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductoService {

    private final ProductoFinancieroRepository productoRepository;

    private ProductoDTO convertToDTO(ProductoFinanciero producto) {
        return ProductoDTO.builder()
                .id(producto.getId())
                .nombre(producto.getNombre())
                .descripcion(producto.getDescripcion())
                .costo(producto.getCosto())
                .porcentajeRetorno(producto.getPorcentajeRetorno())
                .activo(producto.getActivo())
                .riesgo(producto.getRiesgo())
                .build();
    }

    private ProductoFinanciero convertToEntity(ProductoDTO productoDTO) {
        return ProductoFinanciero.builder()
                .id(productoDTO.getId())
                .nombre(productoDTO.getNombre())
                .descripcion(productoDTO.getDescripcion())
                .costo(productoDTO.getCosto())
                .porcentajeRetorno(productoDTO.getPorcentajeRetorno())
                .activo(productoDTO.getActivo())
                .riesgo(productoDTO.getRiesgo())
                .build();
    }

    public List<ProductoDTO> getAllProductos() {
        return productoRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ProductoDTO> getProductosActivos() {
        return productoRepository.findByActivoTrue()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ProductoDTO> getProductosDisponiblesPorCapital(Double capitalMaximo) {
        return productoRepository.findProductosDisponiblesPorCapital(capitalMaximo)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ProductoDTO createProducto(ProductoDTO productoDTO) {
        ProductoFinanciero producto = convertToEntity(productoDTO);
        ProductoFinanciero savedProducto = productoRepository.save(producto);
        return convertToDTO(savedProducto);
    }
}