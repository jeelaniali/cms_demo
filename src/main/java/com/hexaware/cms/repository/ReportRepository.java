package com.hexaware.cms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.hexaware.cms.model.Report;

public interface ReportRepository extends JpaRepository<Report, Long> {
}
