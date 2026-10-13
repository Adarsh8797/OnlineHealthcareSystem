package com.healthcare.dao;

import com.healthcare.db.DatabaseConnector;
import com.healthcare.models.Appointment;
import com.healthcare.models.AppointmentStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * AppointmentDAO - JDBC operations for appointments
 */
public class AppointmentDAO {

    /**
     * Book a new appointment
     */
    public boolean bookAppointment(Appointment appt) {
        String sql = "INSERT INTO appointments (patient_id, doctor_id, appointment_date, status, reason) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, appt.getPatientId());
            ps.setInt(2, appt.getDoctorId());
            ps.setTimestamp(3, Timestamp.valueOf(appt.getAppointmentDate()));
            ps.setString(4, appt.getStatus().name());
            ps.setString(5, appt.getReason());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        appt.setAppointmentId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Booking error: " + e.getMessage());
        }
        return false;
    }

    /**
     * Check if a slot is already booked (used in synchronized booking)
     */
    public boolean isSlotBooked(int doctorId, Timestamp dateTime) {
        String sql = "SELECT COUNT(*) FROM appointments " +
                "WHERE doctor_id = ? AND appointment_date = ? AND status = 'SCHEDULED'";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, doctorId);
            ps.setTimestamp(2, dateTime);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Check slot error: " + e.getMessage());
        }
        return false;
    }

    /**
     * Get all appointments for a specific doctor (JOIN query)
     */
    public List<Appointment> getAppointmentsByDoctor(int doctorId) {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT a.*, p_user.full_name AS patient_name, d_user.full_name AS doctor_name " +
                "FROM appointments a " +
                "JOIN patients p ON a.patient_id = p.patient_id " +
                "JOIN users p_user ON p.user_id = p_user.user_id " +
                "JOIN doctors d ON a.doctor_id = d.doctor_id " +
                "JOIN users d_user ON d.user_id = d_user.user_id " +
                "WHERE a.doctor_id = ? " +
                "ORDER BY a.appointment_date DESC";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, doctorId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToAppointment(rs, true));
                }
            }
        } catch (SQLException e) {
            System.err.println("Get doctor appointments error: " + e.getMessage());
        }
        return list;
    }

    /**
     * Get all appointments for a specific patient
     */
    public List<Appointment> getAppointmentsByPatient(int patientId) {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT a.*, p_user.full_name AS patient_name, d_user.full_name AS doctor_name " +
                "FROM appointments a " +
                "JOIN patients p ON a.patient_id = p.patient_id " +
                "JOIN users p_user ON p.user_id = p_user.user_id " +
                "JOIN doctors d ON a.doctor_id = d.doctor_id " +
                "JOIN users d_user ON d.user_id = d_user.user_id " +
                "WHERE a.patient_id = ? " +
                "ORDER BY a.appointment_date DESC";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, patientId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToAppointment(rs, true));
                }
            }
        } catch (SQLException e) {
            System.err.println("Get patient appointments error: " + e.getMessage());
        }
        return list;
    }

    /**
     * Get ALL appointments (Admin use)
     */
    public List<Appointment> getAllAppointments() {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT a.*, p_user.full_name AS patient_name, d_user.full_name AS doctor_name " +
                "FROM appointments a " +
                "JOIN patients p ON a.patient_id = p.patient_id " +
                "JOIN users p_user ON p.user_id = p_user.user_id " +
                "JOIN doctors d ON a.doctor_id = d.doctor_id " +
                "JOIN users d_user ON d.user_id = d_user.user_id " +
                "ORDER BY a.appointment_date DESC";

        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapResultSetToAppointment(rs, true));
            }
        } catch (SQLException e) {
            System.err.println("Get all appointments error: " + e.getMessage());
        }
        return list;
    }

    /**
     * Update appointment status
     */
    public boolean updateStatus(int appointmentId, AppointmentStatus status, String notes) {
        String sql = "UPDATE appointments SET status = ?, notes = ? WHERE appointment_id = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status.name());
            ps.setString(2, notes);
            ps.setInt(3, appointmentId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Update status error: " + e.getMessage());
            return false;
        }
    }

    private Appointment mapResultSetToAppointment(ResultSet rs, boolean withJoins) throws SQLException {
        Appointment a = new Appointment();
        a.setAppointmentId(rs.getInt("appointment_id"));
        a.setPatientId(rs.getInt("patient_id"));
        a.setDoctorId(rs.getInt("doctor_id"));
        a.setAppointmentDate(rs.getTimestamp("appointment_date").toLocalDateTime());
        a.setStatus(AppointmentStatus.valueOf(rs.getString("status")));
        a.setReason(rs.getString("reason"));
        a.setNotes(rs.getString("notes"));

        if (withJoins) {
            a.setPatientName(rs.getString("patient_name"));
            a.setDoctorName(rs.getString("doctor_name"));
        }
        return a;
    }
}