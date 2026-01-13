package com.project.appointmentsystem.service;
import com.project.appointmentsystem.entity.*;
import com.project.appointmentsystem.repository.AppointmentRepository;
import com.project.appointmentsystem.repository.ServiceRepository;
import com.project.appointmentsystem.repository.UserRepository;
import com.project.appointmentsystem.repository.WorkingScheduleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final WorkingScheduleRepository workingScheduleRepository;
    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public AppointmentService(AppointmentRepository appointmentRepository, WorkingScheduleRepository workingScheduleRepository,ServiceRepository serviceRepository,UserRepository userRepository, NotificationService notificationService) {
        this.appointmentRepository = appointmentRepository;
        this.workingScheduleRepository = workingScheduleRepository;
        this.serviceRepository = serviceRepository;
        this.userRepository=userRepository;
        this.notificationService = notificationService;

    }
    @Transactional
    public AppointmentEntity createAppointment(AppointmentEntity appointment) {

        serviceEntity service = serviceRepository.findById(appointment.getService().getId())
                .orElseThrow(() -> new RuntimeException("Service not found"));

        appointment.setService(service);

        LocalDateTime appointmentStart = appointment.getStartTime();
        LocalDateTime appointmentEnd = appointmentStart.plusMinutes(service.getDurition());
        appointment.setEndtime(appointmentEnd);

        userEntity staff = service.getStaff();
        List<workingscheduleEnitiy> schedules =
                workingScheduleRepository.findByStaff(staff);

        boolean validTime = schedules.stream().anyMatch(ws -> {

            if (ws.isHoliday()) return false;

            if (!ws.getDayOfWeek().equals(appointmentStart.getDayOfWeek()))
                return false;

            LocalDate appointmentDate = appointmentStart.toLocalDate();

            LocalDateTime scheduleStart =
                    LocalDateTime.of(appointmentDate, ws.getStart_time());

            LocalDateTime scheduleEnd =
                    LocalDateTime.of(appointmentDate, ws.getEnd_Time());

            return !appointmentStart.isBefore(scheduleStart)
                    && !appointmentEnd.isAfter(scheduleEnd);
        });

        if (!validTime) {
            throw new RuntimeException("The staff is not here now");
        }

        boolean overlap =
                appointmentRepository.existsByServiceAndStartTimeLessThanAndEndTimeGreaterThan(
                        service,
                        appointmentEnd,
                        appointmentStart
                );

        if (overlap) {
            throw new RuntimeException("You can't book now, time overlaps");
        }

        appointment.setStatus(appointmentStatus.PENDING);

        return appointmentRepository.save(appointment);
    }

    public AppointmentEntity getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
    }
    @Transactional
    public AppointmentEntity updateAppointmentTime(Long appointmentId, LocalDateTime newStartTime) {

        AppointmentEntity appointment = getAppointmentById(appointmentId);

        serviceEntity service = appointment.getService();

        LocalDateTime newEndTime = newStartTime.plusMinutes(service.getDurition());

        userEntity staff = service.getStaff();
        List<workingscheduleEnitiy> schedules =
                workingScheduleRepository.findByStaff(staff);

        boolean validTime = schedules.stream().anyMatch(ws -> {
            LocalDate date = newStartTime.toLocalDate();
            LocalDateTime scheduleStart =
                    LocalDateTime.of(date, ws.getStart_time());
            LocalDateTime scheduleEnd =
                    LocalDateTime.of(date, ws.getEnd_Time());

            return !ws.isHoliday()
                    && !newStartTime.isBefore(scheduleStart)
                    && !newEndTime.isAfter(scheduleEnd);
        });

        if (!validTime) {
            throw new RuntimeException("Staff not available at this time");
        }

        boolean overlap = appointmentRepository.existsByServiceAndIdNotAndStartTimeLessThanAndEndTimeGreaterThan(
                                service,
                                appointmentId,
                                newEndTime,
                                newStartTime
                        );
        if (overlap) {
            throw new RuntimeException("Time overlaps with another appointment");
        }
        appointment.setStartTime(newStartTime);
        appointment.setEndtime(newEndTime);
        appointment.setStatus(appointmentStatus.PENDING);

        return appointmentRepository.save(appointment);
    }
    @Transactional
    public AppointmentEntity cancelAppointment(Long appointmentId, String email, String role) {

        AppointmentEntity appointment = getAppointmentById(appointmentId);

        boolean isOwner = appointment.getCustomer().getEmail().equals(email);
        boolean isAdmin = "ROLE_ADMIN".equals(role);

        if (!isOwner && !isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to cancel this appointment");
        }

        appointment.setStatus(appointmentStatus.CANCELED);

        return appointmentRepository.save(appointment);
    }


    public List<AppointmentEntity> getMyAppointments(String customerEmail) {
        userEntity customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        return appointmentRepository.findByCustomer(customer);
    }

    @Transactional
    public AppointmentEntity updateAppointmentStatus(Long appointmentId, String newStatus, userEntity admin) {

        if (admin.getRole() != Role.ROLE_ADMIN) {
            throw new RuntimeException("Only admin can update appointment status");
        }

        AppointmentEntity appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (appointment.getStatus() != appointmentStatus.PENDING) {
            throw new RuntimeException("Only pending appointments can be updated");
        }

        if (newStatus.equalsIgnoreCase("APPROVED")) {
            appointment.setStatus(appointmentStatus.APPROVED);
        } else if (newStatus.equalsIgnoreCase("REJECTED")) {
            appointment.setStatus(appointmentStatus.REJECTED);
        } else {
            throw new RuntimeException("Invalid status: must be APPROVED or REJECTED");
        }
        AppointmentEntity savedAppointment = appointmentRepository.save(appointment);
        notificationService.sendStatusNotification(
                savedAppointment.getId(),
                savedAppointment.getStatus().toString(),
                savedAppointment.getCustomer().getId()
        );
        return savedAppointment;

    }

    public List<AppointmentEntity> getMyAppointmentsAsStaff(String staffEmail) {

        userEntity staff = userRepository.findByEmail(staffEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (staff.getRole() != Role.ROLE_STAFF) {
            throw new RuntimeException("User is not STAFF");
        }

        return appointmentRepository.findByService_Staff(staff);
    }

}
