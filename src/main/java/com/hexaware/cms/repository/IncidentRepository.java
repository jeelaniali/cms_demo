package com.hexaware.cms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.hexaware.cms.model.Incident;

public interface IncidentRepository extends JpaRepository<Incident, Long> {

}