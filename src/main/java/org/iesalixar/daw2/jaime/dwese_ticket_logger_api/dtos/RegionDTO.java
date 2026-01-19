package org.iesalixar.daw2.jaime.dwese_ticket_logger_api.dtos;

import lombok.Getter;
import lombok.Setter;

/**
 * Clase DTO (Data Transfer Object) que representa una región.
 * Esta close se utiliza para transferir datos de una región
 * entre las capas de la aplicación, especialmente para exponerlos
 * a través de la API sin incluir información innecesaria o sensible.
 */

@Getter
@Setter
public class RegionDTO {

    /**
     * Identificador único de la región.
     * Es el mismo 10 que se encuentra en la entidad Region de la base de datos.
     */
    private Long id;

    /**
     *Código de la región.
     * Normalmente es una cadena corta (máximo 2 caracteres) que identifica lo región.
     * Ejemplo: "61" para Andalucia.
     */
     private String code;

     /**
      * Nombre completo de la región.
      * Ejemplo: "Andalucia", "Cataluña".
     */
     private String name;
}

