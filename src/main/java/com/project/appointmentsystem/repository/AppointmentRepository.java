package com.project.appointmentsystem.repository;

import com.project.appointmentsystem.entity.AppointmentEntity;
import com.project.appointmentsystem.entity.serviceEntity;
import com.project.appointmentsystem.entity.userEntity;
import com.project.appointmentsystem.entity.appointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Long> {


    List<AppointmentEntity> findByCustomer(userEntity customer);
    List<AppointmentEntity> findByService_Staff(userEntity staff);
    List<AppointmentEntity> findByStatus(appointmentStatus status);

    boolean existsByServiceAndIdNotAndStartTimeLessThanAndEndTimeGreaterThan(
            serviceEntity service,
            Long id,
            LocalDateTime endTime,
            LocalDateTime startTime
    );

    boolean existsByServiceAndStartTimeLessThanAndEndTimeGreaterThan(
            serviceEntity service,
            LocalDateTime endTime,
            LocalDateTime startTime
    );
}
