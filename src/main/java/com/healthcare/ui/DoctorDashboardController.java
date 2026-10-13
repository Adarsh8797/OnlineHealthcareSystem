package com.healthcare.ui;

import com.healthcare.dao.DoctorDAO;
import com.healthcare.dao.PatientDAO;
import com.healthcare.models.*;
import com.healthcare.service.AppointmentService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class DoctorDashboardController {

    @FXML private Label welcomeLabel, pageTitle;
    @FXML private Button btnAppointments, btnRecords;
    @FXML private VBox appointmentsView, recordsView;

    @FXML private ComboBox<String> filterCombo;
    @FXML private TableView<Appointment> appointmentsTable;
    @FXML private TableColumn<Appointment, String> colId, colPatient, colDate, colReason, colStatus, colActions;

    @FXML private TableView<Patient> patientsTable;
    @FXML private TableColumn<Patient, String> colPId, colPName, colPEmail, colPPhone, colPBlood, colPHistory;

    private final AppointmentService appointmentService = new AppointmentService();
    private final PatientDAO patientDAO = new PatientDAO();
    private final DoctorDAO doctorDAO = new DoctorDAO();

    private User currentUser;
    private Doctor currentDoctor;
    private ObservableList<Appointment> apptList = FXCollections.observableArrayList();
    private ObservableList<Patient> patientList = FXCollections.observableArrayList();

    public void setUser(User user) {
        this.currentUser = user;
        if (welcomeLabel != null) welcomeLabel.setText("Welcome, " + user.getFullName());
        this.currentDoctor = doctorDAO.getDoctorByUserId(user.getUserId());

        setupTables();
        showAppointments();
    }

    private void setupTables() {
        colId.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getAppointmentId())));
        colPatient.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPatientName()));
        colDate.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getAppointmentDate())));
        colReason.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getReason()));
        colStatus.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatus().name()));

        colActions.setCellFactory(col -> new TableCell<>() {
            final Button completeBtn = new Button("Complete");
            final Button cancelBtn = new Button("Cancel");
            final HBox box = new HBox(5, completeBtn, cancelBtn);
            {
                completeBtn.getStyleClass().add("btn-success");
                cancelBtn.getStyleClass().add("btn-danger");
                completeBtn.setOnAction(e -> updateStatus(getTableView().getItems().get(getIndex()), AppointmentStatus.COMPLETED));
                cancelBtn.setOnAction(e -> updateStatus(getTableView().getItems().get(getIndex()), AppointmentStatus.CANCELLED));
            }
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        colPId.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getPatientId())));
        colPName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFullName()));
        colPEmail.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmail()));
        colPPhone.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPhone()));
        colPBlood.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBloodGroup()));
        colPHistory.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMedicalHistory()));

        filterCombo.setItems(FXCollections.observableArrayList("ALL", "SCHEDULED", "COMPLETED", "CANCELLED"));
        filterCombo.setValue("ALL");
    }

    @FXML private void showAppointments() {
        appointmentsView.setVisible(true); appointmentsView.setManaged(true);
        recordsView.setVisible(false); recordsView.setManaged(false);
        pageTitle.setText("My Appointments");
        btnAppointments.getStyleClass().add("sidebar-btn-active");
        btnRecords.getStyleClass().remove("sidebar-btn-active");
        loadAppointments();
    }

    @FXML private void showRecords() {
        appointmentsView.setVisible(false); appointmentsView.setManaged(false);
        recordsView.setVisible(true); recordsView.setManaged(true);
        pageTitle.setText("Patient Records");
        btnRecords.getStyleClass().add("sidebar-btn-active");
        btnAppointments.getStyleClass().remove("sidebar-btn-active");
        patientList.setAll(patientDAO.getAllPatients());
        patientsTable.setItems(patientList);
    }

    @FXML private void loadAppointments() {
        if (currentDoctor == null) return;
        List<Appointment> all = appointmentService.getAppointmentsByDoctor(currentDoctor.getDoctorId());
        String filter = filterCombo.getValue();
        if (filter != null && !filter.equals("ALL")) {
            all = all.stream().filter(a -> a.getStatus().name().equals(filter)).toList();
        }
        apptList.setAll(all);
        appointmentsTable.setItems(apptList);
    }

    private void updateStatus(Appointment appt, AppointmentStatus status) {
        if (appointmentService.updateStatus(appt.getAppointmentId(), status, "Updated by doctor")) {
            loadAppointments();
            new Alert(Alert.AlertType.INFORMATION, "✅ Status updated to " + status).showAndWait();
        } else {
            new Alert(Alert.AlertType.ERROR, "❌ Update failed").showAndWait();
        }
    }

    @FXML private void handleLogout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/healthcare/ui/login.fxml"));
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            Scene scene = new Scene(root, 800, 600);
            scene.getStylesheets().add(getClass().getResource("/com/healthcare/css/styles.css").toExternalForm());
            stage.setTitle("Healthcare System - Login");
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (Exception e) { e.printStackTrace(); }
    }
}