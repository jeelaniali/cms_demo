package com.hexaware.cms.service;

import com.hexaware.cms.dto.ReportDTO;
import com.hexaware.cms.exception.ReportNotFoundException;
import com.hexaware.cms.exception.IncidentNotFoundException;
import com.hexaware.cms.model.Report;
import com.hexaware.cms.model.Incident;
import com.hexaware.cms.repository.ReportRepository;
import com.hexaware.cms.repository.IncidentRepository;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private IncidentRepository incidentRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ReportDTO createReport(ReportDTO dto) {

        Incident incident = incidentRepository.findById(dto.getIncidentId())
                .orElseThrow(() ->
                        new IncidentNotFoundException("Incident not found with id: " + dto.getIncidentId()));

        if (incident.getStatus() != com.hexaware.cms.model.IncidentStatus.CLOSED && 
            incident.getStatus() != com.hexaware.cms.model.IncidentStatus.VERIFIED) {
            throw new RuntimeException("Report can only be created for CLOSED or VERIFIED incidents.");
        }

        Report report = modelMapper.map(dto, Report.class);

        report.setGeneratedDate(LocalDateTime.now());
        report.setIncident(incident);

        Report saved = reportRepository.save(report);

        return modelMapper.map(saved, ReportDTO.class);
    }

    @Override
    public List<ReportDTO> getAllReports() {

        return reportRepository.findAll()
                .stream()
                .map(report -> modelMapper.map(report, ReportDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public ReportDTO getReportById(Long id) {

        Report report = reportRepository.findById(id)
                .orElseThrow(() ->
                        new ReportNotFoundException("Report not found with id: " + id));

        return modelMapper.map(report, ReportDTO.class);
    }

    @Override
    public ReportDTO updateReport(Long id, ReportDTO dto) {

        Report report = reportRepository.findById(id)
                .orElseThrow(() ->
                        new ReportNotFoundException("Report not found with id: " + id));

        modelMapper.map(dto, report);

        Report updated = reportRepository.save(report);

        return modelMapper.map(updated, ReportDTO.class);
    }

    @Override
    public void deleteReport(Long id) {

        Report report = reportRepository.findById(id)
                .orElseThrow(() ->
                        new ReportNotFoundException("Report not found with id: " + id));

        reportRepository.delete(report);
    }

    @Override
    public List<ReportDTO> getReportByIncident(Long incidentId) {
        return reportRepository.findByIncidentId(incidentId)
                .stream()
                .map(report -> modelMapper.map(report, ReportDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public ReportDTO generateReport(Long incidentId) {
        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new IncidentNotFoundException("Incident not found with id: " + incidentId));

        if (incident.getStatus() != com.hexaware.cms.model.IncidentStatus.CLOSED && 
            incident.getStatus() != com.hexaware.cms.model.IncidentStatus.VERIFIED) {
            throw new RuntimeException("Report can only be generated for CLOSED or VERIFIED incidents.");
        }

        Report report = new Report();
        report.setGeneratedDate(LocalDateTime.now());
        report.setIncident(incident);
        report.setReportDetails("System Generated Report for Incident ID: " + incidentId);

        Report saved = reportRepository.save(report);
        return modelMapper.map(saved, ReportDTO.class);
    }
}