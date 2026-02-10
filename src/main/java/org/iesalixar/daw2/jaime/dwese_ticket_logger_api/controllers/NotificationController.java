package org.iesalixar.daw2.jaime.dwese_ticket_logger_api.controllers;

import org.iesalixar.daw2.jaime.dwese_ticket_logger_api.dtos.NotificationCreateDTO;
import org.iesalixar.daw2.jaime.dwese_ticket_logger_api.dtos.NotificationDTO;
import org.iesalixar.daw2.jaime.dwese_ticket_logger_api.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Controlador REST para gestionar las notificaciones en la aplicación.
 */
@RestController
@RequestMapping("/ws/notifications") //
public class NotificationController {

    @Autowired
    private NotificationService notificationService; //

    @GetMapping
    public Flux<NotificationDTO> getAllNotifications() {
        return notificationService.getAllNotifications(); //
    }

    @PostMapping
    public Mono<NotificationDTO> createNotification(@RequestBody NotificationCreateDTO notificationCreateDTO) {
        return notificationService.saveNotification(notificationCreateDTO); //
    }
}