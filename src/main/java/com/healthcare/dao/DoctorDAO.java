package com.healthcare.dao;

import com.healthcare.db.DatabaseConnector;
import com.healthcare.models.Doctor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DoctorDAO - Doctor specific DB operations
 */
public class DoctorDAO {

    /**
     * Get all doctors with their user info
     */
    public List<Doctor> getAllDoctors() {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT u.*, d.doctor_id, d.specialization, d.qualification, " +
                "d.experience_years, d.available_days, d.consultation_fee " +
                "FROM users u " +
                "JOIN doctors d ON u.user_id = d.user_id " +
                "WHERE u.role = 'DOCTOR'";

        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                doctors.add(mapToDoctor(rs));
            }
        } catch (SQLException e) {
            System.err.println("Get doctors error: " + e.getMessage());
        }
        return doctors;
    }

    /**
     * Get doctor by user_id
     */
    public Doctor getDoctorByUserId(int userId) {
        String sql = "SELECT u.*, d.doctor_id, d.specialization, d.qualification, " +
                "d.experience_years, d.available_days, d.consultation_fee " +
                "FROM users u " +
                "JOIN doctors d ON u.user_id = d.user_id " +
                "WHERE u.user_id = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapToDoctor(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Get doctor error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Register new doctor (creates user + doctor record)
     */
    public boolean addDoctor(Doctor doctor) {
        String userSql = "INSERT INTO users (username, password, full_name, email, phone, role) " +
                "VALUES (?, ?, ?, ?, ?, 'DOCTOR')";
        String docSql = "INSERT INTO doctors (user_id, specialization, qualification, " +
                "experience_years, available_days, consultation_fee) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnector.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement userPs = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
                userPs.setString(1, doctor.getUsername());
                userPs.setString(2, doctor.getPassword());
                userPs.setString(3, doctor.getFullName());
                userPs.setString(4, doctor.getEmail());
                userPs.setString(5, doctor.getPhone());
                userPs.executeUpdate();

                int userId;
                try (ResultSet keys = userPs.getGeneratedKeys()) {
                    keys.next();
                    userId = keys.getInt(1);
                }

                try (PreparedStatement docPs = conn.prepareStatement(docSql)) {
                    docPs.setInt(1, userId);
                    docPs.setString(2, doctor.getSpecialization());
                    docPs.setString(3, doctor.getQualification());
                    docPs.setInt(4, doctor.getExperienceYears());
                    docPs.setString(5, doctor.getAvailableDays());
                    docPs.setDouble(6, doctor.getConsultationFee());
                    docPs.executeUpdate();
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.err.println("Add doctor error: " + e.getMessage());
            return false;
        }
    }

    private Doctor mapToDoctor(ResultSet rs) throws SQLException {
        Doctor d = new Doctor(
                rs.getInt("user_id"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("full_name"),
                rs.getString("email"),
                rs.getString("phone")
        );
        d.setDoctorId(rs.getInt("doctor_id"));
        d.setSpecialization(rs.getString("specialization"));
        d.setQualification(rs.getString("qualification"));
        d.setExperienceYears(rs.getInt("experience_years"));
        d.setAvailableDays(rs.getString("available_days"));
        d.setConsultationFee(rs.getDouble("consultation_fee"));
        return d;
    }
}