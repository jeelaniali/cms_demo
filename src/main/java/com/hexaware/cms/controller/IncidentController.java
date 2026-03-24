package com.hexaware.cms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.hexaware.cms.dto.IncidentDTO;
import com.hexaware.cms.service.IncidentService;

import jakarta.validation.Valid;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/incidents")
public class IncidentController {

    @Autowired
    private IncidentService incidentService;

    // USER can CREATE INCIDENT
    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public IncidentDTO createIncident(@Valid @RequestBody IncidentDTO incidentDTO) {
        return incidentService.createIncident(incidentDTO);
    }

    // STATION_HEAD can view ALL INCIDENTS
    @PreAuthorize("hasRole('STATION_HEAD')")
    @GetMapping
    public List<IncidentDTO> getAllIncidents() {
        return incidentService.getAllIncidents();
    }

    // USER / OFFICER / STATION_HEAD can view INCIDENT BY ID
    @PreAuthorize("hasAnyRole('USER','OFFICER','STATION_HEAD')")
    @GetMapping("/{id}")
    public IncidentDTO getIncidentById(@PathVariable Long id) {
        return incidentService.getIncidentById(id);
    }

    // OFFICER or STATION_HEAD can UPDATE INCIDENT
    @PreAuthorize("hasAnyRole('OFFICER','STATION_HEAD')")
    @PutMapping("/{id}")
    public IncidentDTO updateIncident(@PathVariable Long id,
                                      @Valid @RequestBody IncidentDTO incidentDTO) {
        return incidentService.updateIncident(id, incidentDTO);
    }

    // Only STATION_HEAD can DELETE INCIDENT
    @PreAuthorize("hasRole('STATION_HEAD')")
    @DeleteMapping("/{id}")
    public String deleteIncident(@PathVariable Long id) {
        incidentService.deleteIncident(id);
        return "Incident deleted successfully";
    }

    // Only STATION_HEAD can ASSIGN OFFICER
    @PreAuthorize("hasRole('STATION_HEAD')")
    @PutMapping("/{incidentId}/assign-officer/{officerId}")
    public IncidentDTO assignOfficer(@PathVariable Long incidentId,
                                     @PathVariable Long officerId) {

        return incidentService.assignOfficer(incidentId, officerId);
    }
}