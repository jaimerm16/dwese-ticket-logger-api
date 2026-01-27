package org.iesalixar.daw2.jaime.dwese_ticket_logger_api.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.iesalixar.daw2.jaime.dwese_ticket_logger_api.entities.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface RegionRepository extends JpaRepository<Region, Long> {

    Page<Region> findAll(Pageable pageable);

    Page<Region> findByNameContainingIgnoreCase(String name, Pageable pageable);


    long countByNameContainingIgnoreCase(String name);
    @Query("SELECT COUNT(r) > 0 FROM Region r WHERE r.code = :code")
    boolean existsRegionByCode(@Param("code") String code);
    @Query("SELECT COUNT(r) > 0 FROM Region r WHERE r.code = :code AND r.id != :id")
    boolean existsByCodeAndIdNot(@Param("code") String code, @Param("id") Long id);
}