package com.andesfin.simulacion_service.service;

import com.andesfin.simulacion_service.model.ProductoSimulacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface ProductoSimulacionRepository extends JpaRepository<ProductoSimulacion, UUID> {
}
