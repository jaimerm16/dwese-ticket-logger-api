package org.iesalixar.daw2.jaime.dwese_ticket_logger_api.services;

import org.iesalixar.daw2.jaime.dwese_ticket_logger_api.dtos.RegionCreateDTO;
import org.iesalixar.daw2.jaime.dwese_ticket_logger_api.dtos.RegionDTO;
import org.iesalixar.daw2.jaime.dwese_ticket_logger_api.entities.Region;
import org.iesalixar.daw2.jaime.dwese_ticket_logger_api.mappers.RegionMapper;
import org.iesalixar.daw2.jaime.dwese_ticket_logger_api.repositories.RegionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    /**
     * Obtiene todas las regiones de la base de datos y las convierte a DTOs.
     *
     * @return Lista de objetos `RegionDTO` representando todas las regiones.
     */
    public List<RegionDTO> getAllRegions() {
        try {
            logger.info("Obteniendo todas las regiones...");
            List<Region> regions = regionRepository.findAll();
            logger.info("Se encontraron {} regiones.", regions.size());
            return regions.stream()
                    .map(regionMapper::toDTO)
                    .toList();
        } catch (Exception e) {
            logger.error("Error al obtener todas las regiones: {}", e.getMessage());
            throw new RuntimeException("Error al obtener todas las regiones.", e);
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
     * @return DTO de la región creada.
     * @throws IllegalArgumentException Si el código ya existe.
     */
    /*
    public RegionDTO createRegion(RegionCreateDTO regionCreateDTO, Locale locale) {
        if (regionRepository.existsRegionByCode(regionCreateDTO.getCode())) {
            String errorMessage = "Ya existe la región";
            throw new IllegalArgumentException(errorMessage);
        }

        // Procesar la imagen si se proporciona
        String fileName = null;
        if (regionCreateDTO.getImageFile() != null & !regionCreateDTO.getImageFile().isEmpty()) {
            fileName = fileStorageService.saveFile(regionCreateDTO.getImageFile());
            if (fileName == null) {
                throw new RuntimeException("Error al guardar la imagen");
            }
        }
        // Se convierte a Entity para almacenar en la base de datos
        Region region = regionMapper.toEntity(regionCreateDTO);
        region.setImage(fileName);

        Region savedRegion = regionRepository.save(region);

        // Se devuelve el DTO
        return regionMapper.toDTO(savedRegion);
    }
*/
    /**
     * Actualiza una región existente en la base de datos.
     *
     * @param id Identificador de la región a actualizar.
     * @param regionCreateDTO DTO que contiene los nuevos datos de la región.
     * @param locale Idioma para los mensajes de error.
     * @return DTO de la región actualizada.
     * @throws IllegalArgumentException Si la región no existe o el código ya está en uso.
     */
    public RegionDTO updateRegion(Long id, RegionCreateDTO regionCreateDTO, Locale locale) {
        Region existingRegion = regionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("La región no existe."));

        if (regionRepository.existsRegionByCodeAndNotId(regionCreateDTO.getCode(), id)) {
            String errorMessage = "Ya existe la región";
            throw new IllegalArgumentException(errorMessage);
        }

        existingRegion.setCode(regionCreateDTO.getCode());
        existingRegion.setName(regionCreateDTO.getName());
        Region updatedRegion = regionRepository.save(existingRegion);

        return regionMapper.toDTO(updatedRegion);
    }

    /**
     * Elimina una región específica por su ID.
     *
     * @param id Identificador único de la región.
     * @throws IllegalArgumentException Si la región no existe.
     */
    public void deleteRegion(Long id) {
        if (!regionRepository.existsById(id)) {
            throw new IllegalArgumentException("La región no existe.");
        }
        regionRepository.deleteById(id);
    }
}