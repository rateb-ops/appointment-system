package com.project.appointmentsystem.repository;
import com.project.appointmentsystem.entity.userEntity;
import com.project.appointmentsystem.entity.workingscheduleEnitiy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.DayOfWeek;
import java.util.List;

@Repository
public interface WorkingScheduleRepository extends JpaRepository<workingscheduleEnitiy,Long> {

    List<workingscheduleEnitiy> findByStaff(userEntity staff);
                boolean existsByStaffAndDayOfWeek(userEntity staff, DayOfWeek dayOfWeek);

        }
