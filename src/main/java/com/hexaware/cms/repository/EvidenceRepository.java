package com.hexaware.cms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.hexaware.cms.model.Evidence;
import java.util.List;

public interface EvidenceRepository extends JpaRepository<Evidence, Long> {
    List<Evidence> findByIncidentId(Long incidentId);
}