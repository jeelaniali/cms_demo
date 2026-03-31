package com.hexaware.cms.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.hexaware.cms.dto.IncidentDTO;
import com.hexaware.cms.exception.IncidentNotFoundException;
import com.hexaware.cms.exception.UserNotFoundException;
import com.hexaware.cms.model.Incident;
import com.hexaware.cms.model.IncidentStatus;
import com.hexaware.cms.model.Role;
import com.hexaware.cms.model.User;
import com.hexaware.cms.repository.IncidentRepository;
import com.hexaware.cms.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IncidentServiceImpl implements IncidentService {

    @Autowired
    private IncidentRepository incidentRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepository userRepository;

    // Keep mapping simple and explicit for nested user fields used in UI.
    private IncidentDTO toDTO(Incident incident) {
        IncidentDTO dto = modelMapper.map(incident, IncidentDTO.class);

        if (incident.getReportedBy() != null) {
            dto.setReportedById(incident.getReportedBy().getId());
        }

        if (incident.getOfficer() != null) {
            dto.setOfficerId(incident.getOfficer().getId());
            dto.setOfficerName(incident.getOfficer().getName());
        }

        return dto;
    }

    @Override
    public IncidentDTO createIncident(IncidentDTO dto) {
        Incident incident = modelMapper.map(dto, Incident.class);

        // Step 1 of workflow: always INITIATED when user reports.
        incident.setStatus(IncidentStatus.INITIATED);

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User reporter = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        incident.setReportedBy(reporter);

        Incident savedIncident = incidentRepository.save(incident);
        return toDTO(savedIncident);
    }

    @Override
    public List<IncidentDTO> getAllIncidents() {
        return incidentRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public IncidentDTO getIncidentById(Long id) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new IncidentNotFoundException("Incident not found with id: " + id));

        return toDTO(incident);
    }

    @Override
    public IncidentDTO updateIncident(Long id, IncidentDTO dto) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new IncidentNotFoundException("Incident not found with id: " + id));

        incident.setIncidentType(dto.getIncidentType());
        incident.setDescription(dto.getDescription());
        incident.setLocation(dto.getLocation());
        incident.setDateReported(dto.getDateReported());

        Incident updatedIncident = incidentRepository.save(incident);
        return toDTO(updatedIncident);
    }

    @Override
    public void deleteIncident(Long id) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new IncidentNotFoundException("Incident not found with id: " + id));

        incidentRepository.delete(incident);
    }

    @Override
    public IncidentDTO assignOfficer(Long incidentId, Long officerId) {
        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new IncidentNotFoundException("Incident not found with id: " + incidentId));

        User officer = userRepository.findById(officerId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + officerId));

        if (officer.getRole() != Role.OFFICER) {
            throw new RuntimeException("Selected user is not an officer");
        }

        // Prevent duplicate assignment and status skipping.
        if (incident.getOfficer() != null) {
            throw new RuntimeException("Officer already assigned for this incident");
        }

        if (incident.getStatus() != IncidentStatus.INITIATED) {
            throw new RuntimeException("Only INITIATED incidents can be assigned");
        }

        incident.setOfficer(officer);
        incident.setStatus(IncidentStatus.ACTIVE);

        Incident updated = incidentRepository.save(incident);
        return toDTO(updated);
    }

    @Override
    public List<IncidentDTO> getMyIncidents() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User loggedInUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return incidentRepository.findByReportedById(loggedInUser.getId())
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<IncidentDTO> getIncidentsByUser(String email) {
        return incidentRepository.findByReportedByEmail(email)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<IncidentDTO> getIncidentsByOfficer(Long officerId) {
        return incidentRepository.findByOfficerId(officerId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public IncidentDTO closeIncident(Long id) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new IncidentNotFoundException("Incident not found with id: " + id));

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User loggedInUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (loggedInUser.getRole() != Role.OFFICER) {
            throw new RuntimeException("Only officer can close incidents");
        }

        if (incident.getOfficer() == null) {
            throw new RuntimeException("Cannot close incident before assigning officer");
        }

        if (!incident.getOfficer().getId().equals(loggedInUser.getId())) {
            throw new RuntimeException("You are not assigned to this incident");
        }

        if (incident.getStatus() != IncidentStatus.ACTIVE) {
            throw new RuntimeException("Only ACTIVE incidents can be closed");
        }

        incident.setStatus(IncidentStatus.CLOSED);
        Incident updated = incidentRepository.save(incident);
        return toDTO(updated);
    }

    @Override
    public IncidentDTO verifyIncident(Long id) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new IncidentNotFoundException("Incident not found with id: " + id));

        if (incident.getOfficer() == null) {
            throw new RuntimeException("Cannot verify incident before assigning officer");
        }

        if (incident.getStatus() != IncidentStatus.CLOSED) {
            throw new RuntimeException("Only CLOSED incidents can be verified");
        }

        incident.setStatus(IncidentStatus.VERIFIED);
        Incident updated = incidentRepository.save(incident);
        return toDTO(updated);
    }

    @Override
    public byte[] generateIncidentPdf(Long id) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new IncidentNotFoundException("Incident not found with id: " + id));

        User loggedInUser = getLoggedInUser();
        validatePdfAccess(loggedInUser, incident);

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);

            document.add(new Paragraph("Crime Management System Report").setBold().setFontSize(18));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Incident ID: " + incident.getId()));
            document.add(new Paragraph("Type: " + getValueOrDefault(incident.getIncidentType())));
            document.add(new Paragraph("Status: " + getValueOrDefault(incident.getStatus())));
            document.add(new Paragraph("Location: " + getValueOrDefault(incident.getLocation())));
            document.add(new Paragraph("Assigned Officer: " + getOfficerName(incident)));
            document.add(new Paragraph("Generated Date: " + formatDate(LocalDateTime.now())));

            document.close();
            return outputStream.toByteArray();
        } catch (Exception ex) {
            throw new RuntimeException("Failed to generate PDF report");
        }
    }

    private User getLoggedInUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    private void validatePdfAccess(User loggedInUser, Incident incident) {
        if (loggedInUser.getRole() == Role.STATION_HEAD) {
            return;
        }

        if (loggedInUser.getRole() == Role.USER) {
            if (incident.getReportedBy() == null || !incident.getReportedBy().getId().equals(loggedInUser.getId())) {
                throw new AccessDeniedException("You can download only your own incidents");
            }
            return;
        }

        if (loggedInUser.getRole() == Role.OFFICER) {
            if (incident.getOfficer() == null || !incident.getOfficer().getId().equals(loggedInUser.getId())) {
                throw new AccessDeniedException("You can download only incidents assigned to you");
            }
            return;
        }

        throw new AccessDeniedException("Access denied");
    }

    private String getOfficerName(Incident incident) {
        if (incident.getOfficer() == null) {
            return "Not assigned";
        }

        return getValueOrDefault(incident.getOfficer().getName());
    }

    private String getValueOrDefault(Object value) {
        return value == null ? "-" : value.toString();
    }

    private String formatDate(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
    }
}
