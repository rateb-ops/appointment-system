package com.project.appointmentsystem.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
public class AppointmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime startTime;
    @Column(name="end_time")
    private  LocalDateTime endTime;


    @Enumerated(EnumType.STRING)
    private appointmentStatus status;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private userEntity customer;

    @ManyToOne
    @JoinColumn(name = "service_id")
    private serviceEntity service;


    public AppointmentEntity() {}
    public Long getId() {

        return id;
    }
    public void setId(Long id) {

        this.id = id;
    }
    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime start_Time) {

        this.startTime = start_Time;
    }

    public LocalDateTime getEndtime() {
        return endTime;
    }

    public void setEndtime(LocalDateTime endtime) {

        this.endTime = endtime;
    }

    public appointmentStatus getStatus() {

        return status;
    }
    public void setStatus(appointmentStatus status) {

        this.status = status;
    }

    public userEntity getCustomer() {

        return customer;
    }
    public void setCustomer(userEntity customer) {

        this.customer = customer;
    }
    public serviceEntity getService() {

        return service;
    }
    public void setService(serviceEntity service) {

        this.service = service;
    }
}
