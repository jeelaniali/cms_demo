package com.hexaware.cms.service;
import com.hexaware.cms.model.Role;
import com.hexaware.cms.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;
import com.hexaware.cms.model.IncidentStatus;
import com.hexaware.cms.dto.*;
import com.hexaware.cms.model.Incident;
import com.hexaware.cms.repository.*;
import com.hexaware.cms.repository.IncidentRepository;
import com.hexaware.cms.exception.IncidentNotFoundException;
import com.hexaware.cms.exception.UserNotFoundException;
import com.hexaware.cms.exception.OfficerNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class IncidentServiceImpl implements IncidentService {

    @Autowired
    private IncidentRepository incidentRepository;

    @Autowired
    private ModelMapper modelMapper;
    
    @Autowired
    private UserRepository userRepository;

    // CREATE INCIDENT
    @Override
    public IncidentDTO createIncident(IncidentDTO dto) {

        Incident incident = modelMapper.map(dto, Incident.class);
        
        incident.setStatus(IncidentStatus.INITIATED);

        Incident savedIncident = incidentRepository.save(incident);

        return modelMapper.map(savedIncident, IncidentDTO.class);
    }

    // GET ALL INCIDENTS
    @Override
    public List<IncidentDTO> getAllIncidents() {

        return incidentRepository.findAll()
                .stream()
                .map(incident -> modelMapper.map(incident, IncidentDTO.class))
                .collect(Collectors.toList());
    }

    // GET INCIDENT BY ID
    @Override
    public IncidentDTO getIncidentById(Long id) {

        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new IncidentNotFoundException("Incident not found with id: " + id));

        return modelMapper.map(incident, IncidentDTO.class);
    }

    // UPDATE INCIDENT
    @Override
    public IncidentDTO updateIncident(Long id, IncidentDTO dto) {

        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new IncidentNotFoundException("Incident not found with id: " + id));

        incident.setIncidentType(dto.getIncidentType());
        incident.setDescription(dto.getDescription());
        incident.setLocation(dto.getLocation());
        incident.setDateReported(dto.getDateReported());

        Incident updatedIncident = incidentRepository.save(incident);

        return modelMapper.map(updatedIncident, IncidentDTO.class);
    }

    // DELETE INCIDENT
    @Override
    public void deleteIncident(Long id) {

        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new IncidentNotFoundException("Incident not found with id: " + id));

        incidentRepository.delete(incident);
    }
    
    @Override
    public IncidentDTO assignOfficer(Long incidentId, Long officerId) {

        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() ->
                        new IncidentNotFoundException("Incident not found with id: " + incidentId));

        User officer = userRepository.findById(officerId)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found with id: " + officerId));

        if (officer.getRole() != Role.OFFICER) {
            throw new RuntimeException("Selected user is not an officer");
        }

        incident.setOfficer(officer);

        incident.setStatus(IncidentStatus.ACTIVE);

        Incident updated = incidentRepository.save(incident);

        return modelMapper.map(updated, IncidentDTO.class);
    }
}