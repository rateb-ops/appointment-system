package com.project.appointmentsystem.entity;
import jakarta.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Table(name = "working_schedules")

public class workingscheduleEnitiy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable =true)
    private LocalTime startTime;
    @Column(nullable =true)
    private LocalTime endTime;
    private boolean holiday;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;
    @ManyToOne
    @JoinColumn(name = "staff_id")
    private userEntity staff;

    public workingscheduleEnitiy() {}

    public boolean isHoliday() {

        return holiday;
    }

    public void setHoliday(boolean holiday) {

      this.holiday = holiday;
    }
    public Long getId() {

        return id;
    }

    public void setId(Long id) {

        this.id = id;
    }

    public LocalTime getEnd_Time() {
        return endTime;
    }

    public void setEnd_Time(LocalTime endTime) {
        this.endTime = endTime;
    }
    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }
    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }
    public userEntity getStaff() {
        return staff;
    }
    public void setStaff(userEntity staff) {
        this.staff = staff;
    }

    public LocalTime getStart_time() {

        return startTime;
    }
    public void setStart_time(LocalTime startTime) {

        this.startTime = startTime;
    }

}
