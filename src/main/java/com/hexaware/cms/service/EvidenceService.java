package com.hexaware.cms.service;

import java.util.List;
import com.hexaware.cms.dto.EvidenceDTO;

public interface EvidenceService {

    EvidenceDTO createEvidence(EvidenceDTO evidenceDTO);

    List<EvidenceDTO> getAllEvidence();

    EvidenceDTO getEvidenceById(Long id);

    EvidenceDTO updateEvidence(Long id, EvidenceDTO evidenceDTO);

    void deleteEvidence(Long id);
}