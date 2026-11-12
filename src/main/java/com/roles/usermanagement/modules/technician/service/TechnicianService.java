package com.roles.usermanagement.modules.technician.service;

import com.roles.usermanagement.core.exception.ResourceNotFoundException;
import com.roles.usermanagement.modules.technician.dto.TechnicianProfileDto;
import com.roles.usermanagement.modules.technician.dto.TechnicianWorkloadResponse;
import com.roles.usermanagement.modules.technician.entity.TechnicianProfile;
import com.roles.usermanagement.modules.technician.enums.TechnicianAvailabilityStatus;
import com.roles.usermanagement.modules.technician.repository.TechnicianProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TechnicianService {

    private final TechnicianProfileRepository technicianRepository;

    public TechnicianService(TechnicianProfileRepository technicianRepository) {
        this.technicianRepository = technicianRepository;
    }

    @Transactional(readOnly = true)
    public List<TechnicianWorkloadResponse> getTechniciansWorkload() {
        return technicianRepository.findAll().stream().map(tech -> {
            int maxCap = tech.getMaxConcurrentTickets() > 0 ? tech.getMaxConcurrentTickets() : 5;
            double percentage = ((double) tech.getActiveTicketsCount() / maxCap) * 100.0;
            boolean isAvail = tech.getAvailabilityStatus() == TechnicianAvailabilityStatus.DISPONIBLE
                    && tech.getActiveTicketsCount() < maxCap;

            return TechnicianWorkloadResponse.builder()
                    .username(tech.getUsername())
                    .specialty(tech.getSpecialty())
                    .activeTickets(tech.getActiveTicketsCount())
                    .maxCapacity(maxCap)
                    .workloadPercentage(Math.round(percentage * 10.0) / 10.0)
                    .available(isAvail)
                    .build();
        }).toList();
    }

    @Transactional
    public TechnicianProfileDto updateStatus(String username, TechnicianAvailabilityStatus newStatus) {
        TechnicianProfile profile = technicianRepository.findByUsername(username)
                .orElseGet(() -> {
                    TechnicianProfile newProfile = TechnicianProfile.builder()
                            .username(username)
                            .availabilityStatus(newStatus)
                            .activeTicketsCount(0)
                            .maxConcurrentTickets(5)
                            .build();
                    return technicianRepository.save(newProfile);
                });

        profile.setAvailabilityStatus(newStatus);
        TechnicianProfile updated = technicianRepository.save(profile);

        return TechnicianProfileDto.builder()
                .username(updated.getUsername())
                .specialty(updated.getSpecialty())
                .availabilityStatus(updated.getAvailabilityStatus())
                .activeTicketsCount(updated.getActiveTicketsCount())
                .maxConcurrentTickets(updated.getMaxConcurrentTickets())
                .build();
    }
}
