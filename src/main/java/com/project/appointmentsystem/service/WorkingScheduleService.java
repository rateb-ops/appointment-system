package com.project.appointmentsystem.service;
import com.project.appointmentsystem.entity.Role;
import com.project.appointmentsystem.entity.userEntity;
import com.project.appointmentsystem.entity.workingscheduleEnitiy;
import com.project.appointmentsystem.repository.UserRepository;
import com.project.appointmentsystem.repository.WorkingScheduleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class WorkingScheduleService {

    private final WorkingScheduleRepository workingScheduleRepository;
    private final UserRepository userRepository;

    public WorkingScheduleService(WorkingScheduleRepository workingScheduleRepository, UserRepository userRepository) {
        this.workingScheduleRepository = workingScheduleRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public workingscheduleEnitiy save(workingscheduleEnitiy w, String staffEmail) {

        userEntity staff = userRepository.findByEmail(staffEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (staff.getRole() != Role.ROLE_STAFF) {
            throw new RuntimeException("User is not STAFF");
        }

        w.setStaff(staff);

        if (workingScheduleRepository
                .existsByStaffAndDayOfWeek(staff, w.getDayOfWeek())) {
            throw new IllegalArgumentException("This day already exists");
        }

        if (!w.isHoliday()) {
            if (w.getStart_time() == null || w.getEnd_Time() == null) {
                throw new IllegalArgumentException("Please enter work time");
            }
        } else {
            w.setStart_time(null);
            w.setEnd_Time(null);
        }

        return workingScheduleRepository.save(w);
    }


    public List<workingscheduleEnitiy> getMySchedules(String staffEmail) {
        userEntity staff = userRepository.findByEmail(staffEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (staff.getRole() != Role.ROLE_STAFF) {
            throw new RuntimeException("You are not STAFF");
        }

        return workingScheduleRepository.findByStaff(staff);
    }
    public List<workingscheduleEnitiy> getAllSchedules(String role) {
        if (!"ROLE_ADMIN".equals(role)) {
            throw new RuntimeException(" only ADMIN can view all schedules");
        }
        return workingScheduleRepository.findAll();
    }


    @Transactional
    public workingscheduleEnitiy updateSchedule(Long id, workingscheduleEnitiy req, String staffEmail) {

        workingscheduleEnitiy existing = workingScheduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found"));

        if (!existing.getStaff().getEmail().equals(staffEmail)) {
            throw new RuntimeException("You are not allowed to update this schedule");
        }

        if (req.getDayOfWeek() != null &&
                !req.getDayOfWeek().equals(existing.getDayOfWeek())) {
            throw new IllegalArgumentException("You can't update the day");
        }
        if (req.isHoliday()) {
            existing.setHoliday(true);
            existing.setStart_time(null);
            existing.setEnd_Time(null);
        } else {
            if (req.getStart_time() == null || req.getEnd_Time() == null) {
                throw new IllegalArgumentException("Please enter your work time");
            }
            existing.setHoliday(false);
            existing.setStart_time(req.getStart_time());
            existing.setEnd_Time(req.getEnd_Time());
        }
        return workingScheduleRepository.save(existing);
    }

    @Transactional
    public void deleteSchedule(Long id, String staffEmail) {

        workingscheduleEnitiy schedule = workingScheduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found"));

        if (!schedule.getStaff().getEmail().equals(staffEmail)) {
            throw new RuntimeException("You are not allowed to delete this schedule");
        }

        workingScheduleRepository.delete(schedule);
    }
}
