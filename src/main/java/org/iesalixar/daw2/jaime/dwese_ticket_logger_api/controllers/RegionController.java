package org.iesalixar.daw2.jaime.dwese_ticket_logger_api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.iesalixar.daw2.jaime.dwese_ticket_logger_api.dtos.RegionCreateDTO;
import org.iesalixar.daw2.jaime.dwese_ticket_logger_api.dtos.RegionDTO;
import org.iesalixar.daw2.jaime.dwese_ticket_logger_api.mappers.RegionMapper;
import org.iesalixar.daw2.jaime.dwese_ticket_logger_api.repositories.RegionRepository;
import org.iesalixar.daw2.jaime.dwese_ticket_logger_api.entities.Region;
import org.iesalixar.daw2.jaime.dwese_ticket_logger_api.services.FileStorageService;
import org.iesalixar.daw2.jaime.dwese_ticket_logger_api.services.RegionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Controlador que maneja las operaciones CRUD para la entidad `Region`.
 * Utiliza `RegionDAO` para interactuar con la base de datos.
 */

@RestController
@RequestMapping("/api/regions")
public class RegionController {

    @Autowired
    private MessageSource messageSource;
    private static final Logger logger = LoggerFactory.getLogger(RegionController.class);

    // DAO para gestionar las operaciones de las regiones en la base de datos
    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private RegionMapper regionMapper;

    @Autowired
    private RegionService regionService;

    @Autowired
    private FileStorageService fileStorageService;

    @Operation(summary = "Obtener todas las regiones", description = "Devuelve una lista de todas las regiones " +
            "disponibles en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de regiones recuperada exitosamente",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = RegionDTO.class)))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<Page<RegionDTO>> getAllRegions(
            @PageableDefault(size = 10, sort = "name") Pageable pageable
    ) {
        logger.info("Solicitando todas las regiones con paginación: página {}, tamaño {}",
                pageable.getPageNumber(), pageable.getPageSize());

        try {
            Page<RegionDTO> regions = regionService.getAllRegions(pageable);
            logger.info("Se han encontrado {} regiones.", regions.getTotalElements());
            return ResponseEntity.ok(regions);
        } catch (Exception e) {
            logger.error("Error al listar las regiones: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Operation(summary = "Obtener una región por ID", description = "Recupera una región " +
            "específica según su identificador único.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Región encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RegionDTO.class))),
            @ApiResponse(responseCode = "404", description = "Región no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getRegionById(@PathVariable Long id) {
        logger.info("Buscando region con ID {}", id);
        try{
            Optional<RegionDTO> regionDTO = regionService.getRegionById(id);
            if (regionDTO.isPresent()) {
                logger.info("Rengion con ID {} encontrada", id);
                return ResponseEntity.ok(regionDTO.get());
            } else {
                logger.info("No se encontró ninguna región con ID {}", id);
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body("La región no existe.");
            }

        } catch (Exception e) {
            logger.info("Error al buscar la region con ID {}:  {}", id, e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al buscar la región");
        }
    }

    @Operation(summary = "Crear una nueva región", description = "Permite registrar una nueva región en la base de datos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Región creada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RegionDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos proporcionados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<?> createRegion(@Valid @RequestBody RegionCreateDTO regionCreateDTO, Locale locale) {
        try {
            RegionDTO createdRegion = regionService.createRegion(regionCreateDTO, locale);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdRegion);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error al crear la región: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear la región.");
        }
    }

    @Operation(summary = "Actualizar una región", description = "Permite actualizar los datos de una región existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Región actualizada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RegionDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRegion(@PathVariable Long id, @Valid @RequestBody RegionCreateDTO regionCreateDTO, Locale locale) {
        try {
            RegionDTO updatedRegion = regionService.updateRegion(id, regionCreateDTO, locale);
            return ResponseEntity.ok(updatedRegion);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error al actualizar la región con ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar la región.");
        }
    }

    @Operation(summary = "Eliminar una región", description = "Permite eliminar una región específica de la base de datos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Región eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Región no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRegion(@PathVariable Long id) {
        try {
            regionService.deleteRegion(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error al eliminar la región con ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar la región.");
        }
    }

    //Imagenes
    @PostMapping(value = "/{id}/upload", consumes = "multipart/form-data")
    public ResponseEntity<?> upload(@PathVariable Long id, @RequestParam("images") MultipartFile images) {
        logger.info("Insertando nueva imagen de region con id : {}", id);
        try {
            return regionService.upload(images, id);
        } catch (Exception e) {
            logger.error("Error al insertar la imagen de region: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @RequestMapping(value = "/{images}/images")
    public ResponseEntity<?> readImage(@PathVariable String images) {
        logger.info("Obteniendo imagen de region: {}", images);
        try {
            return fileStorageService.readImage(images);
        } catch (Exception e) {
            logger.error("Error al insertar la imagen de la region: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/{id}/images")
    public ResponseEntity<?> deleteRegionImage(@PathVariable Long id) {
        logger.info("Eliminando la imagen producto con id: {}", id);
        return regionService.deleteRegionImage(id);
    }

}