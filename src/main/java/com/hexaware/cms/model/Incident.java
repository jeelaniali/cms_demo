package com.hexaware.cms.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "incidents")
public class Incident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String incidentType;

    private String description;

    private String location;

    private LocalDateTime dateReported;
    
    @Enumerated(EnumType.STRING)
    private IncidentStatus status;
    
    @ManyToOne
    @JoinColumn(name = "reported_by_id")
    private User reportedBy;

    public User getReportedBy() {
        return reportedBy;
    }

    public void setReportedBy(User reportedBy) {
        this.reportedBy = reportedBy;
    }
    
    public IncidentStatus getStatus() {
		return status;
	}

	public void setStatus(IncidentStatus status) {
		this.status = status;
	}

	@ManyToOne
    @JoinColumn(name = "officer_id")
    private User officer;

    
	public User getOfficer() {
		return officer;
	}

	public void setOfficer(User officer) {
		this.officer = officer;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getIncidentType() {
		return incidentType;
	}

	public void setIncidentType(String incidentType) {
		this.incidentType = incidentType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public LocalDateTime getDateReported() {
		return dateReported;
	}

	public void setDateReported(LocalDateTime dateReported) {
		this.dateReported = dateReported;
	}

	
    
    

}