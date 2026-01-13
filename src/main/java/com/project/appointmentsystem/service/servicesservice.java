package com.project.appointmentsystem.service;
import com.project.appointmentsystem.entity.serviceEntity;
import com.project.appointmentsystem.entity.userEntity;
import com.project.appointmentsystem.repository.ServiceRepository;
import com.project.appointmentsystem.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
public class servicesservice {
    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;

    public servicesservice(ServiceRepository serviceRepository, UserRepository userRepository) {

        this.serviceRepository = serviceRepository;
        this.userRepository = userRepository;
    }


    public List<serviceEntity> getAllServices() {

        return serviceRepository.findAll();
    }
    public serviceEntity getServiceById(Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found"));
    }
    public List<serviceEntity> getServicesByStaff(String staffEmail) {
        userEntity staff = userRepository.findByEmail(staffEmail)
                .orElseThrow(() -> new RuntimeException("Staff not found"));
        return serviceRepository.findAllByStaff(staff);
    }


    @Transactional
    public serviceEntity createService(serviceEntity service, String staffEmail) {

        if (service.getName() == null || service.getName().isEmpty()) {
            throw new RuntimeException("Service name is required");
        }
        if (service.getDurition() <= 0) {
            throw new RuntimeException("Service duration must be positive");
        }
        if (service.getPrice() <= 0) {
            throw new RuntimeException("Service price must be positive");
        }

        userEntity staff = userRepository.findByEmail(staffEmail)
                .orElseThrow(() -> new RuntimeException("Staff not found"));


        boolean exists = serviceRepository.existsByNameAndStaff(service.getName(), staff);
        if (exists) {
            throw new RuntimeException("Service already exists for this staff");
        }
       service.setStaff_id(staff);

        service.setId(null);

        return serviceRepository.save(service);
    }

    @Transactional
    public serviceEntity updateService(Long serviceId, serviceEntity updatedService, String staffEmail) {
        serviceEntity service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("service is not found"));

        if (!service.getStaff().getEmail().equals(staffEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "you are not allowed to edit this service");
        }

        service.setName(updatedService.getName());
        service.setDurition(updatedService.getDurition());
        service.setPrice(updatedService.getPrice());
        return serviceRepository.save(service);
    }
    @Transactional
    public void deleteService(Long serviceId, String userEmail, String role) {

        serviceEntity service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service not found"));

        if ("ROLE_STAFF".equals(role)) {
            if (!service.getStaff().getEmail().equals(userEmail)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to delete this service");
            }
        } else if (!"ROLE_ADMIN".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to delete this service");
        }

        serviceRepository.delete(service);
    }

}
