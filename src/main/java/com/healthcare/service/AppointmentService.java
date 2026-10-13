package com.healthcare.service;

import com.healthcare.dao.AppointmentDAO;
import com.healthcare.models.Appointment;
import com.healthcare.models.AppointmentStatus;

import java.sql.Timestamp;
import java.util.List;

/**
 * AppointmentService - Business Logic Layer
 *
 * ⭐ MULTITHREADING DEMONSTRATION:
 * The bookAppointment() method is SYNCHRONIZED to prevent race conditions
 * when multiple users try to book the same time slot simultaneously.
 */
public class AppointmentService {

    private final AppointmentDAO appointmentDAO;

    public AppointmentService() {
        this.appointmentDAO = new AppointmentDAO();
    }

    /**
     * ⭐ SYNCHRONIZED METHOD - Prevents double booking
     *
     * Scenario: Two patients (threads) try to book the same slot with same doctor.
     * Without synchronized: Both check availability → both see empty → both book → DOUBLE BOOKING ❌
     * With synchronized: First thread locks, checks, books. Second waits, then sees booked → fails ✅
     */
    public synchronized boolean bookAppointment(Appointment appointment) {
        System.out.println("🔒 [" + Thread.currentThread().getName() + "] Trying to book slot...");

        try {
            // Simulate DB call delay (demonstrates race condition importance)
            Thread.sleep(500);

            // Step 1: Check if slot is already booked
            Timestamp slot = Timestamp.valueOf(appointment.getAppointmentDate());
            if (appointmentDAO.isSlotBooked(appointment.getDoctorId(), slot)) {
                System.out.println("❌ [" + Thread.currentThread().getName() + "] Slot ALREADY BOOKED!");
                return false;
            }

            // Step 2: Book the appointment
            appointment.setStatus(AppointmentStatus.SCHEDULED);
            boolean success = appointmentDAO.bookAppointment(appointment);

            if (success) {
                System.out.println("✅ [" + Thread.currentThread().getName() + "] Slot BOOKED successfully!");
            }
            return success;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Thread interrupted: " + e.getMessage());
            return false;
        }
    }

    public List<Appointment> getAppointmentsByDoctor(int doctorId) {
        return appointmentDAO.getAppointmentsByDoctor(doctorId);
    }

    public List<Appointment> getAppointmentsByPatient(int patientId) {
        return appointmentDAO.getAppointmentsByPatient(patientId);
    }

    public List<Appointment> getAllAppointments() {
        return appointmentDAO.getAllAppointments();
    }

    public boolean updateStatus(int appointmentId, AppointmentStatus status, String notes) {
        return appointmentDAO.updateStatus(appointmentId, status, notes);
    }

    /**
     * Cancel appointment
     */
    public boolean cancelAppointment(int appointmentId) {
        return appointmentDAO.updateStatus(appointmentId, AppointmentStatus.CANCELLED, "Cancelled by user");
    }

    /**
     * Complete appointment (doctor marks done)
     */
    public boolean completeAppointment(int appointmentId, String notes) {
        return appointmentDAO.updateStatus(appointmentId, AppointmentStatus.COMPLETED, notes);
    }
}