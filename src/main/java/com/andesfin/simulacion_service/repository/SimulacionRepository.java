package com.andesfin.simulacion_service.repository;

import com.andesfin.simulacion_service.model.Simulacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface SimulacionRepository extends JpaRepository<Simulacion, UUID> {
    List<Simulacion> findByUsuarioId(UUID usuarioId);

    @Query("SELECT s FROM Simulacion s WHERE s.usuario.id = :usuarioId ORDER BY s.fechaSimulacion DESC")
    List<Simulacion> findSimulacionesPorUsuarioOrdenadas(UUID usuarioId);
}