package org.iesalixar.daw2.jaime.dwese_ticket_logger_api.controllers;

import org.iesalixar.daw2.jaime.dwese_ticket_logger_api.entities.Provincia;
import org.iesalixar.daw2.jaime.dwese_ticket_logger_api.repositories.ProvinciaRepository;
import org.iesalixar.daw2.jaime.dwese_ticket_logger_api.entities.Provincia;
import org.iesalixar.daw2.jaime.dwese_ticket_logger_api.repositories.RegionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/provincias")
public class ProvinciaController {

    private static final Logger logger = LoggerFactory.getLogger(ProvinciaController.class);

    @Autowired
    private RegionRepository regionRepository;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private ProvinciaRepository provinciaRepository;

    /** Lista todas las provincias */
    @GetMapping()
    public String listProvincias(@RequestParam(defaultValue = "1") int page, @RequestParam(required = false) String search, @RequestParam(required = false) String sort, Model model) {
        logger.info("Solicitando la lista de todas las provincias..." + search);
        Pageable pageable = PageRequest.of(page - 1, 5, getSort(sort));
        Page<Provincia> provincias;
        int totalPages = 0;
        if (search != null && !search.isBlank()) {
            provincias = provinciaRepository.findByNameContainingIgnoreCase(search, pageable);
            totalPages = (int) Math.ceil((double) provinciaRepository.countByNameContainingIgnoreCase(search) / 5);
        } else {
            provincias = provinciaRepository.findAll(pageable);
            totalPages = (int) Math.ceil((double) provinciaRepository.count() / 5);
        }
        logger.info("Se han cargado {} provincias.", provincias.toList().size());
        model.addAttribute("listProvincias", provincias.toList()); // Pasar la lista de provincias al modelo
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", page);
        model.addAttribute("search", search);
        model.addAttribute("sort", sort);
        return "provincia"; // Nombre de la plantilla Thymeleaf a renderizar
    }


    /** Formulario para crear una nueva provincia */
    @GetMapping("/new")
    public String showNewForm(Model model, RedirectAttributes redirectAttributes) {
        model.addAttribute("provincia", new Provincia());
        try {
            List<Provincia> listProvincias = provinciaRepository.findAll();
            model.addAttribute("provincias", listProvincias);
        } catch (Exception e) {
            e.printStackTrace(); // imprime la causa exacta del error 500
            redirectAttributes.addFlashAttribute("errorMessage", "Error al cargar provincias.");
            return "redirect:/provincias";
        }
        return "provincia-form";
    }

    /** Formulario para editar una provincia existente */
    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            if (provinciaRepository == null) {
                throw new IllegalStateException("provinciaDAO no inyectado");
            }
            Optional<Provincia> provincia = provinciaRepository.findById(id);
            if (provincia == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Provincia no encontrada.");
                return "redirect:/provincias";
            }
            model.addAttribute("provincia", provincia);
        } catch (Exception e) {
            logger.error("Error inesperado: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error interno del servidor.");
            return "redirect:/provincias";
        }
        return "provincia-form";
    }

    /** Inserta una nueva provincia */
    @PostMapping("/insert")
    public String insertProvincia(@ModelAttribute("provincia") Provincia provincia,
                                  RedirectAttributes redirectAttributes) {
        try {
            if (provinciaRepository == null) {
                throw new IllegalStateException("provinciaDAO no inyectado");
            }
            if (provinciaRepository.existsProvinciaByCode(provincia.getCode())) {
                redirectAttributes.addFlashAttribute("errorMessage", "El código de la provincia ya existe.");
                return "redirect:/provincias/new";
            }
            provinciaRepository.save(provincia);
            logger.info("Provincia {} insertada con éxito.", provincia.getCode());
        } catch (Exception e) {
            logger.error("Error inesperado al insertar la provincia: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error interno del servidor.");
            return "redirect:/provincias/new";
        }
        return "redirect:/provincias";
    }

    /** Actualiza una provincia existente */
    @PostMapping("/update")
    public String updateProvincia(@ModelAttribute("provincia") Provincia provincia,
                                  RedirectAttributes redirectAttributes) {
        try {
            if (provinciaRepository == null) {
                throw new IllegalStateException("provinciaDAO no inyectado");
            }
            if (provinciaRepository.existsProvinciaByCodeAndNotId(provincia.getCode(), provincia.getId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "El código de la provincia ya existe para otra provincia.");
                return "redirect:/provincias/edit?id=" + provincia.getId();
            }
            provinciaRepository.save(provincia);
            logger.info("Provincia con ID {} actualizada con éxito.", provincia.getId());
        } catch (Exception e) {
            logger.error("Error inesperado al actualizar la provincia: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error interno del servidor.");
            return "redirect:/provincias/edit?id=" + provincia.getId();
        }
        return "redirect:/provincias";
    }

    /** Elimina una provincia */
    @PostMapping("/delete")
    public String deleteProvincia(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            if (provinciaRepository == null) {
                throw new IllegalStateException("provinciaDAO no inyectado");
            }
            Optional<Provincia> provincia = provinciaRepository.findById(id);
            if (provincia == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Provincia no encontrada.");
                return "redirect:/provincias";
            }
            provinciaRepository.deleteById(id);
            logger.info("Provincia con ID {} eliminada con éxito.", id);
        } catch (Exception e) {
            logger.error("Error inesperado al eliminar la provincia: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error interno del servidor.");
        }
        return "redirect:/provincias";
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

}