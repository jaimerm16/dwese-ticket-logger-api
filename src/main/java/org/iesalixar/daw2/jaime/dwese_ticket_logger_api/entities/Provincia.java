package org.iesalixar.daw2.jaime.dwese_ticket_logger_api.entities;

import jakarta.persistence.*; // Anotaciones de JPA
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * La clase `Provincia` representa una entidad que modela una provincia dentro de
 la base de datos.
 * Contiene cuatro campos: `id`, `code`, `name`, y `region`, donde `id` es el
 identificador único de la provincia,
 * `code` es un código asociado a la provincia, `name` es el nombre de la
 provincia, y `region` es la relación
 * con la entidad `Region`, representando la comunidad autónoma a la que
 pertenece la provincia.
 *
 * Las anotaciones de Lombok ayudan a reducir el código repetitivo al generar
 automáticamente
 * métodos comunes como getters, setters, constructores, y otros métodos estándar
 de los objetos.
 */
@Entity // Marca esta clase como una entidad JPA.
@Table(name = "provincias") // Define el nombre de la tabla asociada a esta entidad.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Provincia {
    // Campo que almacena el identificador único de la provincia. Es autogenerado y clave primaria.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // Campo que almacena el código de la provincia, normalmente una cadena corta que identifica la provincia.
    // Ejemplo: "23" para Jaén.
    @NotEmpty(message = "{msg.provincia.code.notEmpty}")
    @Size(max = 2, message = "{msg.provincia.code.size}")
    @Column(name = "code", nullable = false, length = 2) // Define la columna correspondiente en la tabla.
    private String code;

    // Campo que almacena el nombre completo de la provincia, como "Sevilla" o "Jaén".
    @NotEmpty(message = "{msg.provincia.name.notEmpty}")
    @Size(max = 100, message = "{msg.provincia.name.size}")
    @Column(name = "name", nullable = false, length = 100) // Define la columna correspondiente en la tabla.
    private String name;

    // Relación uno a muchos con la entidad `Location`. Una provincia puede tener muchas ubicaciones.
    @OneToMany(mappedBy = "provincia", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Location> locations;
    /**
     * Constructor que excluye el campo `id`. Se utiliza para crear instancias
     de `Provincia`
     * cuando el `id` aún no se ha generado (por ejemplo, antes de insertarla en
     la base de datos).
     * @param code Código de la provincia.
     * @param name Nombre de la provincia.
     */
    public Provincia(String code, String name) {
        this.code = code;
        this.name = name;
    }
}