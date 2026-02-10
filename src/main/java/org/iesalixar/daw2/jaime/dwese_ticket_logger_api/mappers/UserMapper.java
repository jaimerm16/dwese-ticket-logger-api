package org.iesalixar.daw2.jaime.dwese_ticket_logger_api.mappers;

import org.iesalixar.daw2.jaime.dwese_ticket_logger_api.dtos.UserDTO;
import org.iesalixar.daw2.jaime.dwese_ticket_logger_api.entities.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserDTO toDTO(User usuario) {
        UserDTO dto = new UserDTO();
        dto.setId(usuario.getId());
        dto.setFirstName(usuario.getFirstName());
        dto.setLastName(usuario.getLastName());
        dto.setUsername(usuario.getUsername());
        dto.setImage(usuario.getImage());
        return dto;
    }

    public User toEntity(UserDTO dto) {
        User usuario = new User();
        usuario.setId(dto.getId());
        usuario.setFirstName(dto.getFirstName());
        usuario.setLastName(dto.getLastName());
        usuario.setUsername(dto.getUsername());
        usuario.setImage(dto.getImage());
        return usuario;
    }
}
