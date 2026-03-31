package com.hexaware.cms.controller;

import com.hexaware.cms.dto.ReportDTO;
import com.hexaware.cms.service.ReportService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @PostMapping
    public ReportDTO createReport(@Valid @RequestBody ReportDTO reportDTO) {
        return reportService.createReport(reportDTO);
    }

    @GetMapping
    public List<ReportDTO> getAllReports() {
        return reportService.getAllReports();
    }

    @GetMapping("/{id}")
    public ReportDTO getReportById(@PathVariable Long id) {
        return reportService.getReportById(id);
    }

    @PutMapping("/{id}")
    public ReportDTO updateReport(@PathVariable Long id,
                                  @Valid @RequestBody ReportDTO reportDTO) {
        return reportService.updateReport(id, reportDTO);
    }

    @PreAuthorize("hasRole('STATION_HEAD')")
    @DeleteMapping("/{id}")
    public String deleteReport(@PathVariable Long id) {
        reportService.deleteReport(id);
        return "Report deleted successfully";
    }

    @GetMapping("/incident/{incidentId}")
    public List<ReportDTO> getReportByIncident(@PathVariable Long incidentId) {
        return reportService.getReportByIncident(incidentId);
    }
    
    @PreAuthorize("hasRole('STATION_HEAD')")
    @PostMapping("/admin/report/{incidentId}")
    public ReportDTO generateReport(@PathVariable Long incidentId) {
        return reportService.generateReport(incidentId);
    }
}