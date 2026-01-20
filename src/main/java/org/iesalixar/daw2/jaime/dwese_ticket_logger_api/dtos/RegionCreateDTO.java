package org.iesalixar.daw2.jaime.dwese_ticket_logger_api.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class RegionCreateDTO {

    /**
     * Código único de la región.
     *
     * - No puede estar vacío (@NotEmpty).
     * - Longitud máxima de 2 caracteres (@Size(max = 2)).
     *
     * Ejemplo: "01" para Andalucía, "13" para Madrid.
     */
    @NotEmpty(message = "{msg.region.code.notEmpty}")
    @Size(max = 2, message = "{msg.region.code.size}")
    private String code;

    /**
     * Nombre completo de la región.
     *
     * - No puede estar vacío (@NotEmpty).
     * - Longitud máxima de 100 caracteres (@Size(max = 100)).
     *
     * Ejemplo: "Andalucía", "Cataluña", "Galicia".
     */
    @NotEmpty(message = "{msg.region.name.notEmpty}")
    @Size(max = 100, message = "{msg.region.name.size}")
    private String name;

    /**
     * Archivo de imagen subido desde el formulario.
     * No lleva validación de @NotEmpty porque puede ser opcional (especialmente al actualizar).
     */
    private MultipartFile imageFile;
}