package com.cognizant.medi.patient.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cognizant.medi.patient.model.Patient;

public interface PatientRepository extends JpaRepository<Patient, Integer>{

	Patient findByAadharId(String aadharId);
}
