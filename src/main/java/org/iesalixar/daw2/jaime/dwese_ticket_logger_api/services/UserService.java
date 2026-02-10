package org.iesalixar.daw2.jaime.dwese_ticket_logger_api.services;

import org.iesalixar.daw2.jaime.dwese_ticket_logger_api.dtos.UserDTO;
import org.iesalixar.daw2.jaime.dwese_ticket_logger_api.entities.User;
import org.iesalixar.daw2.jaime.dwese_ticket_logger_api.mappers.UserMapper;
import org.iesalixar.daw2.jaime.dwese_ticket_logger_api.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(RegionService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    public Long getIdByUsername(String username){
        return userRepository.getIdByUsername(username);
    };

    public UserDTO getUserById(Long id) {
        Optional<User> userDTO = userRepository.findById(id);

        if (userDTO.isPresent()){
            return userMapper.toDTO(userDTO.get());
        }
        throw new RuntimeException("El usuario con identificador " + id + " no existe.");
    }
}