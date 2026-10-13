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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class PatientDashboardController {

    @FXML private Label welcomeLabel, pageTitle, statusLabel, profileStatus;
    @FXML private Button btnBook, btnHistory, btnProfile;
    @FXML private VBox bookView, historyView, profileView;

    @FXML private ComboBox<Doctor> doctorCombo;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> timeCombo;
    @FXML private TextArea reasonArea;

    @FXML private TableView<Appointment> historyTable;
    @FXML private TableColumn<Appointment, String> colHId, colHDoctor, colHDate, colHReason, colHStatus, colHActions;

    @FXML private TextField profileName, profileEmail, profilePhone, profileBlood;
    @FXML private TextArea profileAddress;

    private final AppointmentService appointmentService = new AppointmentService();
    private final DoctorDAO doctorDAO = new DoctorDAO();
    private final PatientDAO patientDAO = new PatientDAO();

    private User currentUser;
    private Patient currentPatient;
    private ObservableList<Appointment> historyList = FXCollections.observableArrayList();

    public void setUser(User user) {
        this.currentUser = user;
        if (welcomeLabel != null) welcomeLabel.setText("Welcome, " + user.getFullName());
        this.currentPatient = patientDAO.getPatientByUserId(user.getUserId());

        setupTables();
        setupForm();
        showBook();
    }

    private void setupTables() {
        colHId.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getAppointmentId())));
        colHDoctor.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDoctorName()));
        colHDate.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getAppointmentDate())));
        colHReason.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getReason()));
        colHStatus.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatus().name()));

        colHActions.setCellFactory(col -> new TableCell<>() {
            final Button cancelBtn = new Button("Cancel");
            {
                cancelBtn.getStyleClass().add("btn-danger");
                cancelBtn.setOnAction(e -> cancelAppt(getTableView().getItems().get(getIndex())));
            }
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                Appointment a = empty ? null : getTableView().getItems().get(getIndex());
                if (empty || a == null || a.getStatus() != AppointmentStatus.SCHEDULED) {
                    setGraphic(null);
                } else {
                    setGraphic(cancelBtn);
                }
            }
        });
    }

    private void setupForm() {
        doctorCombo.setItems(FXCollections.observableArrayList(doctorDAO.getAllDoctors()));
        doctorCombo.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(Doctor d) {
                return d == null ? "" : d.getFullName() + " - " + d.getSpecialization();
            }
            @Override public Doctor fromString(String s) { return null; }
        });

        timeCombo.setItems(FXCollections.observableArrayList(
                "09:00", "10:00", "11:00", "12:00", "14:00", "15:00", "16:00", "17:00"));
        datePicker.setValue(java.time.LocalDate.now().plusDays(1));
    }

    @FXML private void showBook() {
        switchView(bookView, "Book Appointment", btnBook);
        statusLabel.setText("");
    }

    @FXML private void showHistory() {
        switchView(historyView, "My Appointments", btnHistory);
        loadHistory();
    }

    @FXML private void showProfile() {
        switchView(profileView, "My Profile", btnProfile);
        loadProfile();
    }

    private void switchView(VBox view, String title, Button active) {
        bookView.setVisible(false); bookView.setManaged(false);
        historyView.setVisible(false); historyView.setManaged(false);
        profileView.setVisible(false); profileView.setManaged(false);

        view.setVisible(true); view.setManaged(true);
        pageTitle.setText(title);

        btnBook.getStyleClass().remove("sidebar-btn-active");
        btnHistory.getStyleClass().remove("sidebar-btn-active");
        btnProfile.getStyleClass().remove("sidebar-btn-active");
        if (!active.getStyleClass().contains("sidebar-btn-active"))
            active.getStyleClass().add("sidebar-btn-active");
    }

    @FXML private void bookAppointment() {
        Doctor doc = doctorCombo.getValue();
        if (doc == null || datePicker.getValue() == null || timeCombo.getValue() == null) {
            statusLabel.setText("⚠ Please fill all fields");
            return;
        }
        if (currentPatient == null) {
            statusLabel.setText("⚠ Patient record not found");
            return;
        }

        try {
            LocalTime time = LocalTime.parse(timeCombo.getValue());
            LocalDateTime dateTime = LocalDateTime.of(datePicker.getValue(), time);

            Appointment appt = new Appointment();
            appt.setPatientId(currentPatient.getPatientId());
            appt.setDoctorId(doc.getDoctorId());
            appt.setAppointmentDate(dateTime);
            appt.setReason(reasonArea.getText().isEmpty() ? "General checkup" : reasonArea.getText());

            if (appointmentService.bookAppointment(appt)) {
                statusLabel.setText("✅ Appointment booked!");
                statusLabel.setStyle("-fx-text-fill: #2e7d32; -fx-font-weight: bold;");
                reasonArea.clear();
            } else {
                statusLabel.setText("❌ Slot already booked. Try another time.");
                statusLabel.setStyle("-fx-text-fill: #d32f2f; -fx-font-weight: bold;");
            }
        } catch (Exception e) {
            statusLabel.setText("❌ Error: " + e.getMessage());
            statusLabel.setStyle("-fx-text-fill: #d32f2f;");
        }
    }

    @FXML private void loadHistory() {
        if (currentPatient == null) return;
        List<Appointment> list = appointmentService.getAppointmentsByPatient(currentPatient.getPatientId());
        historyList.setAll(list);
        historyTable.setItems(historyList);
    }

    private void cancelAppt(Appointment appt) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Cancel this appointment?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                appointmentService.cancelAppointment(appt.getAppointmentId());
                loadHistory();
            }
        });
    }

    private void loadProfile() {
        if (currentPatient == null) return;
        profileName.setText(currentPatient.getFullName());
        profileEmail.setText(currentPatient.getEmail());
        profilePhone.setText(currentPatient.getPhone());
        profileBlood.setText(currentPatient.getBloodGroup() == null ? "" : currentPatient.getBloodGroup());
        profileAddress.setText(currentPatient.getAddress() == null ? "" : currentPatient.getAddress());
    }

    @FXML private void saveProfile() {
        if (currentPatient == null) return;
        currentPatient.setFullName(profileName.getText());
        currentPatient.setEmail(profileEmail.getText());
        currentPatient.setPhone(profilePhone.getText());
        currentPatient.setBloodGroup(profileBlood.getText());
        currentPatient.setAddress(profileAddress.getText());

        // Update users table
        com.healthcare.dao.UserDAO userDAO = new com.healthcare.dao.UserDAO();
        if (userDAO.updateUser(currentPatient)) {
            profileStatus.setText("✅ Profile updated!");
            profileStatus.setStyle("-fx-text-fill: #2e7d32; -fx-font-weight: bold;");
        } else {
            profileStatus.setText("❌ Update failed");
            profileStatus.setStyle("-fx-text-fill: #d32f2f;");
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