package org.iesalixar.daw2.jaime.dwese_ticket_logger_api.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponseDTO {

    private String token;
    private String message;
}
