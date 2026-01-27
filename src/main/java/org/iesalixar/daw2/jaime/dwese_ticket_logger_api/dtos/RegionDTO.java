package org.iesalixar.daw2.jaime.dwese_ticket_logger_api.dtos;

import lombok.Getter;
import lombok.Setter;

/**
 * Clase DTO (Data Transfer Object) que representa una región.
 *
 * Esta clase se utiliza para transferir datos de una región
 * entre las capas de la aplicación, especialmente para exponerlos
 * a través de la API sin incluir información innecesaria o sensible.
 */
@Getter
@Setter
public class RegionDTO {

    /**
     * Identificador único de la región.
     * Es el mismo ID que se encuentra en la entidad 'Region' de la base de datos.
     */
    private Long id;

    /**
     * Código de la región.
     * Normalmente es una cadena corta (máximo 2 caracteres) que identifica la región.
     * Ejemplo: "01" para Andalucía.
     */
    private String code;

    /**
     * Nombre completo de la región.
     * Ejemplo: "Andalucía", "Cataluña".
     */
    private String name;


    private String image;
}
