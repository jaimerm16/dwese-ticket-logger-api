package org.iesalixar.daw2.jaime.dwese_ticket_logger_api.services;

import org.iesalixar.daw2.jaime.dwese_ticket_logger_api.dtos.NotificationCreateDTO;
import org.iesalixar.daw2.jaime.dwese_ticket_logger_api.dtos.NotificationDTO;
import org.iesalixar.daw2.jaime.dwese_ticket_logger_api.entities.Notification;
import org.iesalixar.daw2.jaime.dwese_ticket_logger_api.mappers.NotificationMapper;
import org.iesalixar.daw2.jaime.dwese_ticket_logger_api.repositories.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    /**
     * Guarda una nueva notificación en la base de datos y la envía a través de WebSockets.
     * @param notificationCreateDTO DTO de la notificación a guardar.
     * @return Mono de NotificationDTO con la notificación guardada.
     */
    public Mono<NotificationDTO> saveNotification(NotificationCreateDTO notificationCreateDTO) {
        Notification notification = NotificationMapper.toEntity(notificationCreateDTO);

        return notificationRepository.save(notification)
                // Se ejecuta cuando la notificación ha sido guardada con éxito
                .doOnSuccess(savedNotification ->
                        // Crea un Mono para ejecutar la tarea de enviar la notificación por WebSocket
                        Mono.fromRunnable(() -> messagingTemplate.convertAndSend(
                                        "/topic/notifications", // Canal de WebSocket
                                        NotificationMapper.toDTO(savedNotification) // Convertimos a DTO
                                ))
                                .subscribeOn(Schedulers.boundedElastic()) // Evita bloqueos en el hilo principal
                                .subscribe() // Ejecuta la tarea asíncrona de envío
                )
                .map(NotificationMapper::toDTO); // Convierte a DTO antes de devolverla
    }

    public Flux<NotificationDTO> getAllNotifications(){
        return notificationRepository.findAll().map(NotificationMapper::toDTO);
    }
}