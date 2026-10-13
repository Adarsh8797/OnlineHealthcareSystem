package com.healthcare.dao;

import com.healthcare.db.DatabaseConnector;
import com.healthcare.models.Patient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * PatientDAO - Patient specific DB operations
 */
public class PatientDAO {

    /**
     * Register new patient
     */
    public boolean addPatient(Patient patient) {
        String userSql = "INSERT INTO users (username, password, full_name, email, phone, role) " +
                "VALUES (?, ?, ?, ?, ?, 'PATIENT')";
        String patSql = "INSERT INTO patients (user_id, date_of_birth, gender, address, blood_group, medical_history) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnector.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement userPs = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
                userPs.setString(1, patient.getUsername());
                userPs.setString(2, patient.getPassword());
                userPs.setString(3, patient.getFullName());
                userPs.setString(4, patient.getEmail());
                userPs.setString(5, patient.getPhone());
                userPs.executeUpdate();

                int userId;
                try (ResultSet keys = userPs.getGeneratedKeys()) {
                    keys.next();
                    userId = keys.getInt(1);
                }

                try (PreparedStatement patPs = conn.prepareStatement(patSql)) {
                    patPs.setInt(1, userId);
                    patPs.setDate(2, patient.getDateOfBirth() != null ? Date.valueOf(patient.getDateOfBirth()) : null);
                    patPs.setString(3, patient.getGender() != null ? patient.getGender().name() : null);
                    patPs.setString(4, patient.getAddress());
                    patPs.setString(5, patient.getBloodGroup());
                    patPs.setString(6, patient.getMedicalHistory());
                    patPs.executeUpdate();
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.err.println("Add patient error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get patient by user_id
     */
    public Patient getPatientByUserId(int userId) {
        String sql = "SELECT u.*, p.patient_id, p.date_of_birth, p.gender, p.address, " +
                "p.blood_group, p.medical_history " +
                "FROM users u " +
                "JOIN patients p ON u.user_id = p.user_id " +
                "WHERE u.user_id = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapToPatient(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Get patient error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Get all patients
     */
    public List<Patient> getAllPatients() {
        List<Patient> list = new ArrayList<>();
        String sql = "SELECT u.*, p.patient_id, p.date_of_birth, p.gender, p.address, " +
                "p.blood_group, p.medical_history " +
                "FROM users u " +
                "JOIN patients p ON u.user_id = p.user_id " +
                "WHERE u.role = 'PATIENT'";

        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapToPatient(rs));
            }
        } catch (SQLException e) {
            System.err.println("Get patients error: " + e.getMessage());
        }
        return list;
    }

    /**
     * Update medical history
     */
    public boolean updateMedicalHistory(int patientId, String history) {
        String sql = "UPDATE patients SET medical_history = ? WHERE patient_id = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, history);
            ps.setInt(2, patientId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Update history error: " + e.getMessage());
            return false;
        }
    }

    private Patient mapToPatient(ResultSet rs) throws SQLException {
        Patient p = new Patient(
                rs.getInt("user_id"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("full_name"),
                rs.getString("email"),
                rs.getString("phone")
        );
        p.setPatientId(rs.getInt("patient_id"));

        Date dob = rs.getDate("date_of_birth");
        if (dob != null) p.setDateOfBirth(dob.toLocalDate());

        String gender = rs.getString("gender");
        if (gender != null) p.setGender(com.healthcare.models.Gender.valueOf(gender));

        p.setAddress(rs.getString("address"));
        p.setBloodGroup(rs.getString("blood_group"));
        p.setMedicalHistory(rs.getString("medical_history"));
        return p;
    }
}