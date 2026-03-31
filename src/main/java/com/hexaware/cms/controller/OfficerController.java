package com.hexaware.cms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.hexaware.cms.dto.IncidentDTO;
import com.hexaware.cms.service.IncidentService;
import com.hexaware.cms.model.User;
import com.hexaware.cms.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/officer")
public class OfficerController {

    @Autowired
    private IncidentService incidentService;

    @Autowired
    private UserRepository userRepository;

    @PreAuthorize("hasRole('OFFICER')")
    @GetMapping("/incidents")
    public List<IncidentDTO> getAssignedIncidents() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User loggedInUser = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        return incidentService.getIncidentsByOfficer(loggedInUser.getId());
    }

    @PreAuthorize("hasRole('OFFICER')")
    @PutMapping("/incidents/{id}/close")
    public IncidentDTO closeIncident(@PathVariable Long id) {
        return incidentService.closeIncident(id);
    }
}
