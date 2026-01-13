package com.project.appointmentsystem.controller;

import com.project.appointmentsystem.Security.JwtUtil;
import com.project.appointmentsystem.entity.userEntity;
import com.project.appointmentsystem.entity.workingscheduleEnitiy;
import com.project.appointmentsystem.service.WorkingScheduleService;
import com.project.appointmentsystem.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/schedules")
public class WorkingScheduleController {

    private final WorkingScheduleService workingScheduleService;
    private final JwtUtil jwtUtil;

    private final UserRepository userRepository;

    @Autowired
    public WorkingScheduleController(WorkingScheduleService workingScheduleService,JwtUtil jwtUtil ,UserRepository userRepository) {
        this.workingScheduleService = workingScheduleService;
        this.jwtUtil=jwtUtil;
        this.userRepository = userRepository;
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<workingscheduleEnitiy> createSchedule(
            @RequestBody workingscheduleEnitiy w,
            HttpServletRequest request
    ) {
        String token = request.getHeader("Authorization").substring(7);
        String email = jwtUtil.extractUsername(token);

        return ResponseEntity.ok(
                workingScheduleService.save(w, email)
        );
    }


    @GetMapping("/my")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<List<workingscheduleEnitiy>> getMySchedules(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        String email = jwtUtil.extractUsername(token);

        return ResponseEntity.ok(
                workingScheduleService.getMySchedules(email)
        );
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<workingscheduleEnitiy> updateSchedule(
            @PathVariable Long id,
            @RequestBody workingscheduleEnitiy req,
            HttpServletRequest request
    ) {
        String token = request.getHeader("Authorization").substring(7);
        String email = jwtUtil.extractUsername(token);

        return ResponseEntity.ok(
                workingScheduleService.updateSchedule(id, req, email)
        );
    }


    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<Void> deleteSchedule(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        String token = request.getHeader("Authorization").substring(7);
        String email = jwtUtil.extractUsername(token);

        workingScheduleService.deleteSchedule(id, email);

        return ResponseEntity.noContent().build();

    }
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<workingscheduleEnitiy>> getAllSchedules(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        String role = jwtUtil.extractRole(token);

        return ResponseEntity.ok(workingScheduleService.getAllSchedules(role));
    }
}
