package com.hexaware.cms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.hexaware.cms.dto.EvidenceDTO;
import com.hexaware.cms.service.EvidenceService;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/evidence")
public class EvidenceController {

    @Autowired
    private EvidenceService evidenceService;

    @PreAuthorize("hasRole('OFFICER')")
    @PostMapping
    public EvidenceDTO createEvidence(@Valid @RequestBody EvidenceDTO evidenceDTO) {
        return evidenceService.createEvidence(evidenceDTO);
    }

    @GetMapping
    public List<EvidenceDTO> getAllEvidence() {
        return evidenceService.getAllEvidence();
    }

    @GetMapping("/{id}")
    public EvidenceDTO getEvidenceById(@PathVariable Long id) {
        return evidenceService.getEvidenceById(id);
    }

    @PreAuthorize("hasRole('OFFICER')")
    @PutMapping("/{id}")
    public EvidenceDTO updateEvidence(@PathVariable Long id,
                                      @Valid @RequestBody EvidenceDTO evidenceDTO) {
        return evidenceService.updateEvidence(id, evidenceDTO);
    }

    @PreAuthorize("hasRole('STATION_HEAD')")
    @DeleteMapping("/{id}")
    public String deleteEvidence(@PathVariable Long id) {
        evidenceService.deleteEvidence(id);
        return "Evidence deleted successfully";
    }

    @PreAuthorize("hasAnyRole('USER','OFFICER','STATION_HEAD')")
    @GetMapping("/incident/{incidentId}")
    public List<EvidenceDTO> getEvidenceByIncident(@PathVariable Long incidentId) {
        return evidenceService.getEvidenceByIncident(incidentId);
    }
}