package org.iesalixar.daw2.jaime.dwese_ticket_logger_api.controllers;

import org.iesalixar.daw2.jaime.dwese_ticket_logger_api.dtos.UserDTO;
import org.iesalixar.daw2.jaime.dwese_ticket_logger_api.services.UserService;
import org.iesalixar.daw2.jaime.dwese_ticket_logger_api.utils.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtutil;

    @GetMapping()
    public ResponseEntity<UserDTO> getUsername(@RequestHeader("Authorization") String tokenheader) {
        logger.info("Solicitando la información del usuario logueado");

        String token = tokenheader.replace("Bearer ", "");
        Long id = jwtutil.extractClaim(token, claims -> claims.get("id", Long.class));

        try{
            UserDTO userDTO = userService.getUserById(id);
            logger.info("Se ha encontrado el usuario con identificador {}.", id);
            return ResponseEntity.ok(userDTO);
        } catch (Exception e) {
            logger.info("Error al buscar el usuario con ID {}:  {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}