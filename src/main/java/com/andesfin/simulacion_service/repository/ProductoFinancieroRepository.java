package com.andesfin.simulacion_service.repository;

import com.andesfin.simulacion_service.model.ProductoFinanciero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface ProductoFinancieroRepository extends JpaRepository<ProductoFinanciero, UUID> {
    List<ProductoFinanciero> findByActivoTrue();

    @Query("SELECT p FROM ProductoFinanciero p WHERE p.activo = true AND p.costo <= :costoMaximo")
    List<ProductoFinanciero> findProductosDisponiblesPorCapital(Double costoMaximo);
}