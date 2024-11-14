package com.uniquindio.health.services;

import com.uniquindio.health.domain.Microservice;
import com.uniquindio.health.repositories.MicroserviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class MonitoringService {
    @Autowired
    private MicroserviceRepository repository;

    private final NotificationClient notificationClient;

    private RestTemplate restTemplate = new RestTemplate();

    public MonitoringService(NotificationClient notificationClient) {
        this.notificationClient = notificationClient;
    }

    public Map<String, Object> checkHealthDetailed(String endpoint) {
        Map<String, Object> healthDetail = new HashMap<>();
        healthDetail.put("status", "DOWN"); // Asumimos que está abajo inicialmente

        try {
            // Realiza una solicitud HTTP al endpoint de salud del microservicio
            ResponseEntity<Map> response = restTemplate.getForEntity(endpoint + "/health", Map.class);

            // Verificamos si la respuesta es exitosa
            if (response.getStatusCode() == HttpStatus.OK) {
                // Aquí puedes realizar verificaciones adicionales según la respuesta
                healthDetail.put("status", "UP");
                healthDetail.put("checks", response.getBody().get("checks")); // Incluye detalles de liveness/readiness si están disponibles
            } else {
                healthDetail.put("error", "Microservicio no está disponible");
            }
        } catch (RestClientException e) {
            // Manejo de excepciones si la solicitud falla
            healthDetail.put("error", "Microservicio no está disponible");
        }

        return healthDetail;
    }



    public void registerMicroservice(Microservice service) {
        repository.save(service);
    }

    public Map<String, Map<String, Object>> checkAllServicesHealth() {
        List<Microservice> services = repository.findAll();
        Map<String, Map<String, Object>> healthStatus = new HashMap<>();

        for (Microservice service : services) {
            Map<String, Object> status = checkHealthDetailed(service.getEndpoint());
            healthStatus.put(service.getName(), status);

            // Si el servicio está DOWN, envía una notificación
            if ("DOWN".equals(status.get("status"))) {
                List<String> destinatario = repository.findAllEmails();
                List<String> canales = Arrays.asList("email"); // Canales de notificación
                String mensaje = "El microservicio " + service.getName() + " ha caído o está en estado de alarma.";
                String modo = "async";

                for (String email : destinatario) {
                    notifyServiceDown(email, canales, mensaje, modo);
                }            }
        }

        return healthStatus;
    }



    public Map<String, Object> checkSpecificServiceHealth(String serviceName) {
        Microservice service = repository.findByName(serviceName);

        if (service != null) {
            return checkHealthDetailed(service.getEndpoint());
        }

        return Map.of("error", "Microservice not found");
    }

    public void notifyServiceDown(String destinatario, List<String> canales, String mensaje, String modo) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("destinatario", destinatario);
        notification.put("canales", canales);
        notification.put("mensaje", mensaje);
        notification.put("modo", modo);
        notificationClient.sendNotification(notification);
    }

    @Scheduled(fixedRate = 60000) // Verifica cada minuto
    public void periodicHealthCheck() {
        checkAllServicesHealth();
    }



}
