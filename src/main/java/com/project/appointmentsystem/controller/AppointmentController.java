package com.project.appointmentsystem.controller;
import com.project.appointmentsystem.Security.JwtUtil;
import com.project.appointmentsystem.entity.AppointmentEntity;
import com.project.appointmentsystem.entity.appointmentStatus;
import com.project.appointmentsystem.entity.userEntity;
import com.project.appointmentsystem.repository.AppointmentRepository;
import com.project.appointmentsystem.repository.UserRepository;
import com.project.appointmentsystem.service.AppointmentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final UserRepository userRepository;
private final JwtUtil jwtUtil;
    private final AppointmentRepository appointmentRepository;

    public AppointmentController(AppointmentService appointmentService, UserRepository userRepository,JwtUtil jwtUtil,AppointmentRepository appointmentRepository) {
        this.appointmentService = appointmentService;
      this.userRepository=userRepository;
        this.jwtUtil=jwtUtil;
        this.appointmentRepository=appointmentRepository;
    }
    @PostMapping("/book")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> bookAppointment(
            @RequestBody AppointmentEntity appointment,
            HttpServletRequest request
    ) {
        String token = request.getHeader("Authorization").substring(7);
        String email = jwtUtil.extractUsername(token);

        userEntity customer = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        appointment.setCustomer(customer);

        AppointmentEntity saved = appointmentService.createAppointment(appointment);

        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<AppointmentEntity> updateAppointmentTime(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            HttpServletRequest request) {

        String token = request.getHeader("Authorization").substring(7);
        String email = jwtUtil.extractUsername(token);

        userEntity customer = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));


        AppointmentEntity appointment = appointmentService.getAppointmentById(id);
        if (!appointment.getCustomer().getEmail().equals(email)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can't edit this appointment");
        }

        LocalDateTime newStartTime = LocalDateTime.parse(body.get("startTime"));
        AppointmentEntity updated = appointmentService.updateAppointmentTime(id, newStartTime);

        return ResponseEntity.ok(updated);
    }


    @GetMapping("/my")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<List<AppointmentEntity>> getMyAppointmentsAsStaff(
            HttpServletRequest request
    ) {
        String token = request.getHeader("Authorization").substring(7);
        String email = jwtUtil.extractUsername(token);

        return ResponseEntity.ok(
                appointmentService.getMyAppointmentsAsStaff(email)
        );
    }

    @GetMapping("/customer/my")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<AppointmentEntity>> getMyAppointmentsAsCustomer(
            HttpServletRequest request
    ) {
        String token = request.getHeader("Authorization").substring(7);
        String email = jwtUtil.extractUsername(token);

        return ResponseEntity.ok(
                appointmentService.getMyAppointments(email)
        );
    }
    @PutMapping("/cancel/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<AppointmentEntity> cancelAppointment(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        String token = request.getHeader("Authorization").substring(7);
        String email = jwtUtil.extractUsername(token);

        return ResponseEntity.ok(
                appointmentService.cancelAppointment(id, email, "ROLE_CUSTOMER")
        );
    }
    @GetMapping("/admin/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AppointmentEntity>> getPendingAppointments() {
        return ResponseEntity.ok(
                appointmentRepository.findByStatus(appointmentStatus.PENDING)
        );
    }


    @PutMapping("/admin/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AppointmentEntity> updateAppointmentStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            HttpServletRequest request
    ) {
        String token = request.getHeader("Authorization").substring(7);
        String email = jwtUtil.extractUsername(token);

        userEntity admin = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        String status = body.get("status"); // APPROVED أو REJECTED

        return ResponseEntity.ok(
                appointmentService.updateAppointmentStatus(id, status, admin)
        );
    }
    /*
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AppointmentEntity>> getAllAppointments() {
        return ResponseEntity.ok(appointmentRepository.findAll());
    }*/


}



