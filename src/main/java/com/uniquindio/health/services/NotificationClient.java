package com.uniquindio.health.services;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class NotificationClient {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.routingkey}")
    private String routingKey;

    public NotificationClient(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendNotification(Map<String, Object> notificationData) {
        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, notificationData);
            System.out.println("Log enviado correctamente: " + notificationData);
        } catch (Exception e) {
            System.err.println("Error while sending log to RabbitMQ: " + e.getMessage());
        }
    }
}
