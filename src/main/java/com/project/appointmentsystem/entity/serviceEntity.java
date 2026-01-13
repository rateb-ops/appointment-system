package com.project.appointmentsystem.entity;

import jakarta.persistence.*;

@Entity
@Table(name="services")
public class serviceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(nullable = false)
    private int durition;
    @Column(nullable = false)
    private double price;
    @ManyToOne
    @JoinColumn(name = "staff_id",nullable = false)
    private userEntity staff;

    public serviceEntity(){}

    public Long getId() {

        return id;
    }
    public void setId(Long id) {

        this.id = id;
    }
    public userEntity getStaff() {

        return staff;
    }
    public void setStaff_id(userEntity staff) {

        this.staff = staff;
    }

    public String getName() {

        return name;
    }
    public void setName(String name) {

        this.name = name;
    }
    public int getDurition() {

        return durition;
    }
    public void setDurition(int durition) {

        this.durition = durition;
    }
    public double getPrice() {

        return price;
    }
    public void setPrice(double price) {

        this.price = price;
    }
}
