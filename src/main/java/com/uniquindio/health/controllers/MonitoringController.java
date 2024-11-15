package com.uniquindio.health.controllers;

import com.uniquindio.health.domain.Microservice;
import com.uniquindio.health.services.MonitoringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/monitoring")
public class MonitoringController {

    @Autowired
    private MonitoringService monitoringService;

    // Ruta para registrar un nuevo microservicio
    @PostMapping("/register")
    public String registerMicroservice(@RequestBody Microservice service) {
        monitoringService.registerMicroservice(service);
        return "Microservice registered successfully!";
    }

    // Ruta para obtener la salud de todos los microservicios
    @GetMapping("/health")
    public ResponseEntity<?> getAllHealthStatus() {
        Map<String, Map<String, Object>> healthStatus = monitoringService.checkAllServicesHealth();
        for (Map.Entry<String, Map<String, Object>> entry : healthStatus.entrySet()) {
            Object value = entry.getValue();

            if (value instanceof Map) {
                Map<?, ?> nestedMap = (Map<?, ?>) value;

                if (nestedMap.containsKey("error")) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(healthStatus);
                }
            }
        }
        return ResponseEntity.ok(healthStatus);
    }

    @GetMapping("/health/{name}")
    public ResponseEntity<?> getSpecificHealthStatus(@PathVariable String name) {
        Map<String, Object> healthStatus = monitoringService.checkSpecificServiceHealth(name);

        // Verificamos si la respuesta incluye un

        if (healthStatus.containsKey("error")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(healthStatus);
        }

        return ResponseEntity.ok(healthStatus);
    }


}
