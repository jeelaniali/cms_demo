package com.hexaware.cms.service;

import java.util.List;
import com.hexaware.cms.dto.ReportDTO;

public interface ReportService {

    ReportDTO createReport(ReportDTO reportDTO);

    List<ReportDTO> getAllReports();

    ReportDTO getReportById(Long id);

    ReportDTO updateReport(Long id, ReportDTO reportDTO);

    void deleteReport(Long id);

    List<ReportDTO> getReportByIncident(Long incidentId);
    
    ReportDTO generateReport(Long incidentId);
}
