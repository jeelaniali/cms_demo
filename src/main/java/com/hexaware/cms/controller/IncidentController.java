package com.hexaware.cms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hexaware.cms.dto.IncidentDTO;
import com.hexaware.cms.service.IncidentService;

import jakarta.validation.Valid;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/incidents")
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
    @GetMapping("/id/{id}")
    public IncidentDTO getIncidentById(@PathVariable Long id) {
        return incidentService.getIncidentById(id);
    }

    // OFFICER or STATION_HEAD can UPDATE INCIDENT
    @PreAuthorize("hasAnyRole('OFFICER','STATION_HEAD')")
    @PutMapping("/{id:\\d+}")
    public IncidentDTO updateIncident(@PathVariable Long id,
                                      @Valid @RequestBody IncidentDTO incidentDTO) {
        return incidentService.updateIncident(id, incidentDTO);
    }

    // Only STATION_HEAD can DELETE INCIDENT
    @PreAuthorize("hasRole('STATION_HEAD')")
    @DeleteMapping("/{id:\\d+}")
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

    // Logged-in user can fetch only their own incidents
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/my")
    public List<IncidentDTO> getMyIncidents() {
        return incidentService.getMyIncidents();
    }

    @PreAuthorize("hasRole('STATION_HEAD')")
    @GetMapping("/user/{email}")
    public List<IncidentDTO> getIncidentsByUser(@PathVariable String email) {
        return incidentService.getIncidentsByUser(email);
    }

    @PreAuthorize("hasAnyRole('OFFICER','STATION_HEAD')")
    @GetMapping("/officer/{officerId}")
    public List<IncidentDTO> getIncidentsByOfficer(@PathVariable Long officerId) {
        return incidentService.getIncidentsByOfficer(officerId);
    }

    @PreAuthorize("hasRole('OFFICER')")
    @PutMapping("/{id:\\d+}/close")
    public IncidentDTO closeIncident(@PathVariable Long id) {
        return incidentService.closeIncident(id);
    }

    @PreAuthorize("hasRole('STATION_HEAD')")
    @PutMapping("/{id:\\d+}/verify")
    public IncidentDTO verifyIncident(@PathVariable Long id) {
        return incidentService.verifyIncident(id);
    }

    // USER / OFFICER / STATION_HEAD can download PDF, but service checks exact access
    @PreAuthorize("hasAnyRole('USER','OFFICER','STATION_HEAD')")
    @GetMapping("/{id:\\d+}/pdf")
    public ResponseEntity<byte[]> downloadIncidentPdf(@PathVariable Long id) {
        byte[] pdfBytes = incidentService.generateIncidentPdf(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=incident_" + id + ".pdf")
                .body(pdfBytes);
    }
}
