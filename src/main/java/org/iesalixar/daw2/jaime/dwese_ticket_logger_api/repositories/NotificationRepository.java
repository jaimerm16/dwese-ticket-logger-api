package org.iesalixar.daw2.jaime.dwese_ticket_logger_api.repositories;

import org.iesalixar.daw2.jaime.dwese_ticket_logger_api.entities.Notification;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface NotificationRepository extends ReactiveMongoRepository<Notification, String> {
}
