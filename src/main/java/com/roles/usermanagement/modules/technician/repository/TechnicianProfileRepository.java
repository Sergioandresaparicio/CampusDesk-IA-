package com.roles.usermanagement.modules.technician.repository;

import com.roles.usermanagement.modules.technician.entity.TechnicianProfile;
import com.roles.usermanagement.modules.technician.enums.TechnicianAvailabilityStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TechnicianProfileRepository extends JpaRepository<TechnicianProfile, Long> {

    Optional<TechnicianProfile> findByUsername(String username);

    List<TechnicianProfile> findByAvailabilityStatus(TechnicianAvailabilityStatus status);

    @Query("SELECT t FROM TechnicianProfile t WHERE t.availabilityStatus = 'DISPONIBLE' AND t.activeTicketsCount < t.maxConcurrentTickets ORDER BY t.activeTicketsCount ASC")
    List<TechnicianProfile> findAvailableTechniciansOrderedByWorkload();
}
