package com.healthcare.models;

import java.time.LocalDate;

/**
 * Patient Class - Inherits from User
 */
public class Patient extends User {

    private int patientId;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String address;
    private String bloodGroup;
    private String medicalHistory;

    public Patient() {
        super();
        setRole(Role.PATIENT);
    }

    public Patient(int userId, String username, String password,
                   String fullName, String email, String phone) {
        super(userId, username, password, fullName, email, phone, Role.PATIENT);
    }

    // ===== POLYMORPHISM =====
    @Override
    public String getDashboardFXML() {
        return "/com/healthcare/ui/patient_dashboard.fxml";
    }

    // ===== GETTERS & SETTERS =====
    public int getPatientId() { return patientId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public String getMedicalHistory() { return medicalHistory; }
    public void setMedicalHistory(String medicalHistory) { this.medicalHistory = medicalHistory; }
}