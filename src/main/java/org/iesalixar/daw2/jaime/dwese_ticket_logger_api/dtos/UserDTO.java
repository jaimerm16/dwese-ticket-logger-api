package org.iesalixar.daw2.jaime.dwese_ticket_logger_api.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String image;
    private String username;

    // Constructor, Getters y Setters
    public UserDTO(Long id, String firstName, String lastName, String username, String image) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.image = image;
    }

    public UserDTO() {

    }
}