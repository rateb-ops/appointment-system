package com.project.appointmentsystem.repository;
import com.project.appointmentsystem.entity.userEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<userEntity,Long> {

    Optional<userEntity> findByEmail(String email);


}
