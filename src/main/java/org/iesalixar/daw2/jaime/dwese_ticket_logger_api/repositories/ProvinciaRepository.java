package org.iesalixar.daw2.JaimeRamirezMuela.dwese_ticket_logger_webapp.repositories;
import java.util.List;
import java.util.Optional;

import org.iesalixar.daw2.JaimeRamirezMuela.dwese_ticket_logger_webapp.entities.Provincia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProvinciaRepository extends JpaRepository<Provincia, Long> {
    Page<Provincia> findAll(Pageable pageable);

    Page<Provincia> findByNameContainingIgnoreCase(String name, Pageable pageable);

    long countByNameContainingIgnoreCase(String name);

    boolean existsProvinciaByCode(String code);
    @Query("SELECT COUNT(p) > 0 FROM Provincia p WHERE p.code = :code AND p.id != :id")
    boolean existsProvinciaByCodeAndNotId(@Param("code") String code, @Param("id") Long id);
}