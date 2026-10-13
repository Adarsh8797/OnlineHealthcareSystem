package com.healthcare.models;

/**
 * Doctor Class - Inherits from User
 */
public class Doctor extends User {

    private int doctorId;
    private String specialization;
    private String qualification;
    private int experienceYears;
    private String availableDays;
    private double consultationFee;

    public Doctor() {
        super();
        setRole(Role.DOCTOR);
    }

    public Doctor(int userId, String username, String password,
                  String fullName, String email, String phone) {
        super(userId, username, password, fullName, email, phone, Role.DOCTOR);
    }

    // ===== POLYMORPHISM =====
    @Override
    public String getDashboardFXML() {
        return "/com/healthcare/ui/doctor_dashboard.fxml";
    }

    // ===== GETTERS & SETTERS =====
    public int getDoctorId() { return doctorId; }
    public void setDoctorId(int doctorId) { this.doctorId = doctorId; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getQualification() { return qualification; }
    public void setQualification(String qualification) { this.qualification = qualification; }

    public int getExperienceYears() { return experienceYears; }
    public void setExperienceYears(int experienceYears) { this.experienceYears = experienceYears; }

    public String getAvailableDays() { return availableDays; }
    public void setAvailableDays(String availableDays) { this.availableDays = availableDays; }

    public double getConsultationFee() { return consultationFee; }
    public void setConsultationFee(double consultationFee) { this.consultationFee = consultationFee; }
}