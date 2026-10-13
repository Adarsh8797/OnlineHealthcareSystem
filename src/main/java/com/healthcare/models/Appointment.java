package com.healthcare.models;

import java.time.LocalDateTime;

/**
 * Appointment Model Class
 */
public class Appointment {

    private int appointmentId;
    private int patientId;
    private int doctorId;
    private LocalDateTime appointmentDate;
    private AppointmentStatus status;
    private String reason;
    private String notes;

    // Extra fields for display (JOIN queries ke liye)
    private String patientName;
    private String doctorName;

    public Appointment() {
    }

    public Appointment(int patientId, int doctorId, LocalDateTime appointmentDate,
                       String reason) {
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.appointmentDate = appointmentDate;
        this.reason = reason;
        this.status = AppointmentStatus.SCHEDULED;
    }

    // ===== GETTERS & SETTERS =====
    public int getAppointmentId() { return appointmentId; }
    public void setAppointmentId(int appointmentId) { this.appointmentId = appointmentId; }

    public int getPatientId() { return patientId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }

    public int getDoctorId() { return doctorId; }
    public void setDoctorId(int doctorId) { this.doctorId = doctorId; }

    public LocalDateTime getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(LocalDateTime appointmentDate) { this.appointmentDate = appointmentDate; }

    public AppointmentStatus getStatus() { return status; }
    public void setStatus(AppointmentStatus status) { this.status = status; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }

    @Override
    public String toString() {
        return "Appointment{" +
                "id=" + appointmentId +
                ", patient='" + patientName + '\'' +
                ", doctor='" + doctorName + '\'' +
                ", date=" + appointmentDate +
                ", status=" + status +
                '}';
    }
}