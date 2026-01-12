package org.iesalixar.daw2.jaime.dwese_ticket_logger_api.controllers;
import jakarta.validation.Valid;
import org.iesalixar.daw2.jaime.dwese_ticket_logger_api.repositories.RegionRepository;
import org.iesalixar.daw2.jaime.dwese_ticket_logger_api.repositories.ProvinciaRepository;
import org.iesalixar.daw2.jaime.dwese_ticket_logger_api.entities.Region;
import org.iesalixar.daw2.jaime.dwese_ticket_logger_api.services.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Controlador REST que maneja las operaciones CRUD para la entidad Region.
 * Expone endpoints para gestionar regiones mediante peticiones HTTP.
 **/
@RestController
@RequestMapping("/api/regions") // Prefijo común para todas las rutas del controlador
public class RegionController {
    private static final Logger logger =
            LoggerFactory.getLogger(RegionController.class);
    // DAO para gestionar las operaciones de las regiones en la base de datos
    @Autowired
    private RegionRepository regionRepository;
    @Autowired
    private ProvinciaRepository provinciaRepository;
    @Autowired
    private FileStorageService fileStorageService;

    /**
     * Lista todas las regiones almacenadas en la base de datos.
     *
     * @return ResponseEntity con la lista de regiones o un error en caso de fallo.
     **/
    @GetMapping
    public ResponseEntity<List<Region>> getAllRegions() {
        logger.info("Solicitando la lista de todas las regiones...");
        try {
            List<Region> regions = regionRepository.findAll();
            logger.info("Se han encontrado regiones.", regions.size());
            return ResponseEntity.ok(regions);
        } catch (Exception e) {
            logger.error("Error al listar las regiones: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Obtiene una región especifica por su ID.
     *
     * @param id ID de la región solicitada.
     * @return ResponseEntity con la región encontrada o un mensaje de error si no existe.
     */

    @GetMapping("/{id}")
    public ResponseEntity<Region> getRegionById(@PathVariable Long id) {
        logger.info("Buscando región con ID {}", id);
        try {
            Optional<Region> region = regionRepository.findById(id);
            if (region.isPresent()) {
                logger.info("Región con ID {} encontrada: ", id, region.get());
                return ResponseEntity.ok(region.get());
            } else {
                logger.warn("No se encontró ninguna región con ID {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (Exception e) {
            logger.error("Error al buscar la región con ID: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Crea una nueva región en la base de dates.
     *
     * @param region Objeto JSON que representa la nueva región.
     * @param locale Idiona de las mensajes de error.
     * @return ResponseEntity con la región creada o un mensaje de error,
     */
    @PostMapping
    public ResponseEntity<?> createRegion(@Valid @RequestBody Region region, Locale locale) {
        logger.info("Insertando nueva región con código {}", region.getCode());
        try {
            // Validar si el código ya existe
            if (regionRepository.existsRegionByCode(region.getCode())) {
                String errorMessage = messageSource.getMessage("msg.region-controller.insert.codeExist", null, locale);
                logger.warn("Error al crear región: {}", errorMessage);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
            }
            // Guardar la nueva región
            Region savedRegion = regionRepository.save(region);
            logger.info("Región creada exitosamente con ID {}", savedRegion.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedRegion);
        } catch (Exception e) {
            logger.error("Error al crear la región: 1", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear la región.");
        }
    }

    /**
     * Actualiza una región existente par su ID.
     *
     * @param id     ID de la región a actualizon.
     * @param region Dojeto JSON con las nuevas datos,
     * @param locale Idioma de los mensajes de error.
     * @return ResponseEntity con la región actualizade o un mensaje de error.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRegion(@PathVariable Long id, @Valid @RequestBody Region region, Locale locale) {
        logger.info("Actualizando región con ID {}", id);
        try {
            // Verificar si la región existe
            Optional<Region> existingRegion = regionRepository.findById(id);
            if (!existingRegion.isPresent()) {
                logger.warn("No se encontró ninguna región con ID {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("La región no existe.");
            }
            // Validar si el código va pertenece a otra región
            if (regionRepository.existsRegionByCodeAndNotId(region.getCode(), id)) {
                String errorMessage = messageSource.getMessage("msg.region-controller.update.codeExist", null, locale);
                logger.warn("Error al actualizar región: ", errorMessage);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
            }
            // Actualizar la región
            region.setId(id); // Asegurarse de que el ID no cambie
            Region updatedRegion = regionRepository.save(region);
            logger.info("Región con ID {} actualizada exitosamente.", id);
            return ResponseEntity.ok(updatedRegion);
        } catch (Exception e) {
            logger.error("Error al actualizar la región con ID (): {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar la región.");
        }
    }

    /**
     * Elimina una región especifica por su ID.
     *
     * @param id ID de la región a eliminar.
     * @return ResponseEntity indicando el resultado de la operación.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRegion(@PathVariable Long id) {
        logger.info("Eliminando región con ID {}", id);
        try {
            // Verificar si la región existe
            if (!regionRepository.existsById(id)) {
                logger.warn("No se encontró ninguna región con ID {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("La región no existe.");
            }
            // Eliminar la región
            regionRepository.deleteById(id);
            logger.info("Región con ID {} eliminada exitosamente.", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error al eliminar la región con ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar la región.");
        }
    }
}