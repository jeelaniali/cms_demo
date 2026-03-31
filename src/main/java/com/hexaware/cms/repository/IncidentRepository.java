package com.hexaware.cms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.hexaware.cms.model.Incident;
import java.util.List;

public interface IncidentRepository extends JpaRepository<Incident, Long> {
    List<Incident> findByReportedById(Long reportedById);
    List<Incident> findByReportedByEmail(String email);
    List<Incident> findByOfficerId(Long officerId);
}
