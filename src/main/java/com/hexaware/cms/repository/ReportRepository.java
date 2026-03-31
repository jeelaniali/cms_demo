package com.hexaware.cms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.hexaware.cms.model.Report;
import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByIncidentId(Long incidentId);
}
