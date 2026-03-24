package com.hexaware.cms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.hexaware.cms.model.Evidence;

public interface EvidenceRepository extends JpaRepository<Evidence, Long> {
}