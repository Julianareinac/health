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
    public Map<String, Map<String, Object>> getAllHealthStatus() {
        return monitoringService.checkAllServicesHealth();
    }

    @GetMapping("/health/{name}")
    public ResponseEntity<?> getSpecificHealthStatus(@PathVariable String name) {
        Map<String, Object> healthStatus = monitoringService.checkSpecificServiceHealth(name);

        // Verificamos si la respuesta incluye un error
        if (healthStatus.containsKey("error")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(healthStatus);
        }

        return ResponseEntity.ok(healthStatus);
    }


}
