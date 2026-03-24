package com.hexaware.cms.service;

import java.util.List;
import com.hexaware.cms.dto.IncidentDTO;

public interface IncidentService {

    IncidentDTO createIncident(IncidentDTO incidentDTO);

    List<IncidentDTO> getAllIncidents();

    IncidentDTO getIncidentById(Long id);

    IncidentDTO updateIncident(Long id, IncidentDTO incidentDTO);

    void deleteIncident(Long id);
    
    IncidentDTO assignOfficer(Long incidentId, Long officerId);
}