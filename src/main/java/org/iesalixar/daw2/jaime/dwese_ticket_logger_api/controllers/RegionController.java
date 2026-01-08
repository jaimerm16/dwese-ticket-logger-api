package org.iesalixar.daw2.JaimeRamirezMuela.dwese_ticket_logger_webapp.controllers;
import jakarta.validation.Valid;
import org.iesalixar.daw2.JaimeRamirezMuela.dwese_ticket_logger_webapp.repositories.RegionRepository;
import org.iesalixar.daw2.JaimeRamirezMuela.dwese_ticket_logger_webapp.repositories.ProvinciaRepository;
import org.iesalixar.daw2.JaimeRamirezMuela.dwese_ticket_logger_webapp.entities.Provincia;
import org.iesalixar.daw2.JaimeRamirezMuela.dwese_ticket_logger_webapp.entities.Region;
import org.iesalixar.daw2.JaimeRamirezMuela.dwese_ticket_logger_webapp.services.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Controlador que maneja las operaciones CRUD para la entidad Region.
 * Utiliza RegionDAO para interactuar con la base de datos.
 */
@Controller
@RequestMapping("/regions")
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
     * Lista todas las regiones y las pasa como atributo al modelo para que sean
     * accesibles en la vista region.html.
     *
     * @param model Objeto del modelo para pasar datos a la vista.
     * @return El nombre de la plantilla Thymeleaf para renderizar la lista de
    regiones.
     */
    @GetMapping()
    public String listRegions(@RequestParam(defaultValue = "1") int page, @RequestParam(required = false) String search, @RequestParam(required = false) String sort, Model model) {
        logger.info("Solicitando la lista de todas las regiones..." + search);
        Pageable pageable = PageRequest.of(page - 1, 5, getSort(sort));
        Page<Region> regions;
        int totalPages = 0;
        if (search != null && !search.isBlank()) {
            regions = regionRepository.findByNameContainingIgnoreCase(search, pageable);
            totalPages = (int) Math.ceil((double) regionRepository.countByNameContainingIgnoreCase(search) / 5);
        } else {
            regions = regionRepository.findAll(pageable);
            totalPages = (int) Math.ceil((double) regionRepository.count() / 5);
        }
        logger.info("Se han cargado {} regiones.", regions.toList().size());
        model.addAttribute("listRegions", regions.toList()); // Pasar la lista de regiones al modelo
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", page);
        model.addAttribute("search", search);
        model.addAttribute("sort", sort);
        return "region"; // Nombre de la plantilla Thymeleaf a renderizar
    }

    /**
     * Muestra el formulario para crear una nueva región.
     *
     * @param model Modelo para pasar datos a la vista.
     * @return El nombre de la plantilla Thymeleaf para el formulario.
     */
    @GetMapping("/new")
    public String showNewForm(Model model, RedirectAttributes redirectAttributes) {
        model.addAttribute("region", new Region());
        List<Provincia> listProvincias = provinciaRepository.findAll();
        model.addAttribute("provincias", listProvincias);
        return "region-form";
    }

    /**
     * Muestra el formulario para editar una región existente.
     *
     * @param id ID de la región a editar.
     * @param model Modelo para pasar datos a la vista.
     * @return El nombre de la plantilla Thymeleaf para el formulario.
     */
    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id, Model model) throws SQLException {
        Optional<Region> regionOpt = regionRepository.findById(id);
        model.addAttribute("region", regionOpt);
        model.addAttribute("provincias", provinciaRepository.findAll());
        return "region-form";
    }
    /**
     * Inserta una nueva región en la base de datos.
     *
     * @param region Objeto que contiene los datos del formulario.
     * @param redirectAttributes Atributos para mensajes flash de redirección.
     * @return Redirección a la lista de regiones.
     */
    @PostMapping("/insert")
    public String insertRegion(@Valid @ModelAttribute("region") Region region, @RequestParam("imageFile") MultipartFile
            imageFile, BindingResult result, RedirectAttributes redirectAttributes) {
        logger.info("Insertando nueva región con código {}", region.getCode());
        if (result.hasErrors()) {
            return "region-form"; // Devuelve el formulario para mostrar loserrores de validación
        }
        if (regionRepository.existsRegionByCode(region.getCode())) {
            logger.warn("El código de la región {} ya existe.",
                    region.getCode());
            redirectAttributes.addFlashAttribute("errorMessage", "El código de la región ya existe.");
            return "redirect:/regions/new";
        }
        // Guardar la imagen subida
        if (!imageFile.isEmpty()) {
            String fileName = fileStorageService.saveFile(imageFile);
            if (fileName != null) {
                region.setImage(fileName); // Guardar el nombre del archivo en la entidad
            }
        }
        regionRepository.save(region);
        logger.info("Región {} insertada con éxito.", region.getCode());
        return "redirect:/regions"; // Redirigir a la lista de regiones
    }

    /* Actualiza una región existente en la base de datos.
     *
     * @param region Objeto que contiene los datos del formulario.
     * @param redirectAttributes Atributos para mensajes flash de redirección.
     * @return Redirección a la lista de regiones.
     */
    @PostMapping("/update")
    public String updateRegion(@Valid @ModelAttribute("region") Region region, @RequestParam("imageFile") MultipartFile
            imageFile, BindingResult result, RedirectAttributes redirectAttributes) {
        logger.info("Actualizando región con ID {}", region.getId());
        if (result.hasErrors()) {
            return "region-form"; // Devuelve el formulario para mostrar loserrores de validación
        }
        if (regionRepository.existsRegionByCodeAndNotId(region.getCode(), region.getId())) {
            logger.warn("El código de la región {} ya existe para otra región.",
                    region.getCode());
            redirectAttributes.addFlashAttribute("errorMessage", "El código de la región ya existe para otra región.");
            return "redirect:/regions/edit?id=" + region.getId();
        }
        // Guardar la imagen subida
        if (!imageFile.isEmpty()) {
            String fileName = fileStorageService.saveFile(imageFile);
            if (fileName != null) {
                region.setImage(fileName); // Guardar el nombre del archivo en la entidad
            }
        }
        regionRepository.save(region);
        logger.info("Región con ID {} actualizada con éxito.", region.getId());
        return "redirect:/regions"; // Redirigir a la lista de regiones
    }
    /**
     * Elimina una región de la base de datos.
     *
     * @param id ID de la región a eliminar.
     * @param redirectAttributes Atributos para mensajes flash de redirección.
     * @return Redirección a la lista de regiones.
     */
    @PreAuthorize("hasRole('ADMIN')")

    @PostMapping("/delete")
    public String deleteRegion(@RequestParam("id") Long id, RedirectAttributes
            redirectAttributes) {
        logger.info("Eliminando región con ID {}", id);
        regionRepository.deleteById(id);
        logger.info("Región con ID {} eliminada con éxito.", id);
        return "redirect:/regions"; // Redirigir a la lista de regiones
    }
    public void example(){
        String cadena = "Daniel";
        cadena.isBlank();
    }

    private Sort getSort(String sort) {
        if (sort == null) {
            return Sort.by("id").ascending();
        }
        return switch (sort) {
            case "nameAsc" -> Sort.by("name").ascending();
            case "nameDesc" -> Sort.by("name").descending();
            case "codeAsc" -> Sort.by("code").ascending();
            case "codeDesc" -> Sort.by("code").descending();
            case "idDesc" -> Sort.by("id").descending();
            default -> Sort.by("id").ascending();
        };
    }
    /*@PostMapping("/{id}/delete-image")
    public String deleteRegionImage(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        logger.info("Eliminando imagen de la región con ID {}", id);

        Optional<Region> regionOpt = regionRepository.findById(id);
        if (regionOpt != null && regionOpt.findImageById(id) != null) {
            fileStorageService.deleteFile(regionOpt.findImageById(id)); // elimina el archivo físico
            regionOpt.updateImageById(null);
            regionRepository.save(); // actualiza la BD con el campo de imagen vacío
            logger.info("Imagen de la región {} eliminada correctamente.", id);
            redirectAttributes.addFlashAttribute("successMessage", "Imagen eliminada correctamente.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "No se encontró la imagen para eliminar.");
        }

        return "redirect:/regions/edit?id=" + id;
    }*/

}