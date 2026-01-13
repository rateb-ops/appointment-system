package com.project.appointmentsystem.repository;

import com.project.appointmentsystem.entity.serviceEntity;
import com.project.appointmentsystem.entity.userEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<serviceEntity,Long> {
    List<serviceEntity> findAllByStaff(userEntity staff);


    boolean existsByNameAndStaff(String name, userEntity staff);


}
