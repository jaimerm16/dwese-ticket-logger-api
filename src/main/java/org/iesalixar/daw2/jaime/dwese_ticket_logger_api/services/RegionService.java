package org.iesalixar.daw2.jaime.dwese_ticket_logger_api.services;

import jakarta.validation.Valid;
import org.iesalixar.daw2.jaime.dwese_ticket_logger_api.dtos.RegionCreateDTO;
import org.iesalixar.daw2.jaime.dwese_ticket_logger_api.dtos.RegionDTO;
import org.iesalixar.daw2.jaime.dwese_ticket_logger_api.entities.Region;
import org.iesalixar.daw2.jaime.dwese_ticket_logger_api.mappers.RegionMapper;
import org.iesalixar.daw2.jaime.dwese_ticket_logger_api.repositories.RegionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class RegionService {

    private static final Logger logger = LoggerFactory.getLogger(RegionService.class);

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private RegionMapper regionMapper;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * Obtiene todas las regiones de la base de datos y las convierte a DTOs.
     * * @return Lista de objetos `RegionDTO` representando todas las regiones.
     */
    public Page<RegionDTO> getAllRegions(Pageable pageable) {
        logger.info("Solicitando todas las regiones con paginación: página {}, tamaño {}",
                pageable.getPageNumber(), pageable.getPageSize());

        try {
            Page<Region> regions = regionRepository.findAll(pageable);
            logger.info("Se han encontrado {} regiones en la página actual.", regions.getNumberOfElements());
            return regions.map(regionMapper::toDTO);
        } catch (Exception e) {
            logger.error("Error al obtener la lista paginada de regiones: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Busca una región específica por su ID.
     *
     * @param id Identificador único de la región.
     * @return Un Optional que contiene un `RegionDTO` si la región existe.
     */
    public Optional<RegionDTO> getRegionById(Long id) {
        try {
            logger.info("Buscando región con ID {}", id);
            return regionRepository.findById(id).map(regionMapper::toDTO);
        } catch (Exception e) {
            logger.error("Error al buscar región con ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Error al buscar la región.", e);
        }
    }

    /**
     * Crea una nueva región en la base de datos.
     *
     * @param regionCreateDTO DTO que contiene los datos de la región a crear.
     * @param locale Idioma para los mensajes de error.
     * @return Respuesta HTTP con el estado de la operación.
     */
    public RegionDTO createRegion(RegionCreateDTO regionCreateDTO, Locale locale) {
        if (regionRepository.existsRegionByCode(regionCreateDTO.getCode())) {
            String errorMessage = messageSource.getMessage("msg.region-controller.insert.codeExist", null, locale);
            throw new IllegalArgumentException(errorMessage);
        }

        // Se convierte a Entity para almacenar en la base de datos
        Region region = regionMapper.toEntity(regionCreateDTO);
        Region savedRegion = regionRepository.save(region);

        // Se devuelve el DTO
        return regionMapper.toDTO(savedRegion);
    }

    /**
     * Actualiza una región existente en la base de datos.
     * * @param id Identificador de la región a actualizar.
     * @param regionCreateDTO DTO que contiene los nuevos datos de la región.
     * @param locale Idioma para los mensajes de error.
     * @return DTO de la región actualizada.
     * @throws IllegalArgumentException Si la región no existe o el código ya está en uso.
     */
    public RegionDTO updateRegion(Long id, RegionCreateDTO regionCreateDTO, Locale locale) {
        Region existingRegion = regionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("La región no existe."));

        if (regionRepository.existsByCodeAndIdNot(regionCreateDTO.getCode(), id)) {
            String errorMessage = messageSource.getMessage("msg.region-controller.update.codeExist", null, locale);
            throw new IllegalArgumentException(errorMessage);
        }

        existingRegion.setCode(regionCreateDTO.getCode());
        existingRegion.setName(regionCreateDTO.getName());

        Region updatedRegion = regionRepository.save(existingRegion);

        return regionMapper.toDTO(updatedRegion);
    }

    public void deleteRegion(Long id) {
        if(!regionRepository.existsById(id)) {
            throw new IllegalArgumentException("La region no existe.");
        }
        regionRepository.deleteById(id);
    }

    public ResponseEntity<?> upload(MultipartFile file, Long id) {
        String filename = null;
        Optional<Region> regionOpt = regionRepository.findById(id);
        if (regionOpt.isPresent()) {
            Region region = regionOpt.get();
            if (file != null && !file.isEmpty()) {
                filename = fileStorageService.saveFile(file);
                if (filename == null) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
                }
                region.setImage(filename);
                regionRepository.save(region);
                return ResponseEntity.ok("Imagen subida correctamente");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    public ResponseEntity<?> deleteRegionImage(Long id) {
        try {
            if (regionRepository.findById(id).isPresent()) {
                Optional<Region> regionOpt = regionRepository.findById(id);
                if (regionOpt.isPresent()) {
                    fileStorageService.deleteFile(regionOpt.get().getImage());
                    regionOpt.get().setImage(null);
                    regionRepository.save(regionOpt.get());
                }
                logger.info("Eliminada la imagen del producto con id: {} satisfactoriamente", id);
                return ResponseEntity.ok().body(null);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error al eliminar la imagen del producto: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}