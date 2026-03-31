package com.hexaware.cms.service;

import com.hexaware.cms.dto.EvidenceDTO;
import com.hexaware.cms.exception.EvidenceNotFoundException;
import com.hexaware.cms.exception.IncidentNotFoundException;
import com.hexaware.cms.model.Evidence;
import com.hexaware.cms.model.Incident;
import com.hexaware.cms.repository.EvidenceRepository;
import com.hexaware.cms.repository.IncidentRepository;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EvidenceServiceImpl implements EvidenceService {

    @Autowired
    private EvidenceRepository evidenceRepository;

    @Autowired
    private IncidentRepository incidentRepository;

    @Autowired
    private ModelMapper modelMapper;

    // CREATE EVIDENCE
    @Override
    public EvidenceDTO createEvidence(EvidenceDTO dto) {

        Evidence evidence = modelMapper.map(dto, Evidence.class);

        Incident incident = incidentRepository.findById(dto.getIncidentId())
                .orElseThrow(() ->
                        new IncidentNotFoundException("Incident not found with id: " + dto.getIncidentId()));

        evidence.setIncident(incident);

        Evidence savedEvidence = evidenceRepository.save(evidence);

        return modelMapper.map(savedEvidence, EvidenceDTO.class);
    }


    @Override
    public List<EvidenceDTO> getAllEvidence() {

        return evidenceRepository.findAll()
                .stream()
                .map(evidence -> modelMapper.map(evidence, EvidenceDTO.class))
                .collect(Collectors.toList());
    }

    // GET EVIDENCE BY ID
    @Override
    public EvidenceDTO getEvidenceById(Long id) {

        Evidence evidence = evidenceRepository.findById(id)
                .orElseThrow(() ->
                        new EvidenceNotFoundException("Evidence not found with id: " + id));

        return modelMapper.map(evidence, EvidenceDTO.class);
    }

    // UPDATE EVIDENCE
    @Override
    public EvidenceDTO updateEvidence(Long id, EvidenceDTO dto) {

        Evidence evidence = evidenceRepository.findById(id)
                .orElseThrow(() ->
                        new EvidenceNotFoundException("Evidence not found with id: " + id));

        Incident incident = incidentRepository.findById(dto.getIncidentId())
                .orElseThrow(() ->
                        new IncidentNotFoundException("Incident not found with id: " + dto.getIncidentId()));

        modelMapper.map(dto, evidence);

        evidence.setIncident(incident);

        Evidence updatedEvidence = evidenceRepository.save(evidence);

        return modelMapper.map(updatedEvidence, EvidenceDTO.class);
    }

    // DELETE EVIDENCE
    @Override
    public void deleteEvidence(Long id) {

        Evidence evidence = evidenceRepository.findById(id)
                .orElseThrow(() ->
                        new EvidenceNotFoundException("Evidence not found with id: " + id));

        evidenceRepository.delete(evidence);
    }

    @Override
    public List<EvidenceDTO> getEvidenceByIncident(Long incidentId) {
        return evidenceRepository.findByIncidentId(incidentId)
                .stream()
                .map(evidence -> modelMapper.map(evidence, EvidenceDTO.class))
                .collect(Collectors.toList());
    }
}