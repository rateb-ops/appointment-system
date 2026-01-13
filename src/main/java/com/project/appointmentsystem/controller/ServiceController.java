package com.project.appointmentsystem.controller;
import com.project.appointmentsystem.Security.JwtUtil;
import com.project.appointmentsystem.entity.serviceEntity;
import com.project.appointmentsystem.service.servicesservice;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
public class ServiceController {

    private final servicesservice serviceService;
    private final JwtUtil jwtUtil;

    public ServiceController(servicesservice serviceService,JwtUtil jwtUtil) {

        this.serviceService = serviceService;
        this.jwtUtil= jwtUtil;
    }



    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<serviceEntity> getServiceById(@PathVariable Long id) {
        return ResponseEntity.ok(serviceService.getServiceById(id));
    }
    @PreAuthorize("hasRole('STAFF')")
    @PostMapping("/create")
    public ResponseEntity<serviceEntity> addService(@RequestBody serviceEntity service, HttpServletRequest request) {
        String email = jwtUtil.extractUsername(request.getHeader("Authorization").substring(7));
        return ResponseEntity.ok(serviceService.createService(service,email));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<serviceEntity> updateService(@PathVariable Long id, @RequestBody serviceEntity service, HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        String email = jwtUtil.extractUsername(token);
        return ResponseEntity.ok(serviceService.updateService(id, service, email));
    }

    @GetMapping("/my-services")
    public ResponseEntity<List<serviceEntity>> getMyServices(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        String email = jwtUtil.extractUsername(token);
        List<serviceEntity> services = serviceService.getServicesByStaff(email);
        return ResponseEntity.ok(services);
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id, HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        String email = jwtUtil.extractUsername(token);
        String role = jwtUtil.extractRole(token);
        serviceService.deleteService(id, email, role);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    public ResponseEntity<List<serviceEntity>> getAllServices() {
        List<serviceEntity> services = serviceService.getAllServices();
        return ResponseEntity.ok(services);
    }
}
