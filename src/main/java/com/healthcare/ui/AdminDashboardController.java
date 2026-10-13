package com.healthcare.ui;

import com.healthcare.models.*;
import com.healthcare.service.AppointmentService;
import com.healthcare.service.UserService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class AdminDashboardController {

    @FXML private Label welcomeLabel, pageTitle;
    @FXML private Label totalUsersLabel, totalDoctorsLabel, totalPatientsLabel, totalAppointmentsLabel;

    @FXML private VBox overviewView, usersView, appointmentsView;
    @FXML private Button btnOverview, btnUsers, btnAppointments;

    @FXML private TextField userSearchField;
    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, String> colUserId, colUsername, colFullName, colEmail, colPhone, colRole, colActions;

    @FXML private ComboBox<String> apptFilterCombo;
    @FXML private TableView<Appointment> appointmentsTable;
    @FXML private TableColumn<Appointment, String> colApptId, colApptPatient, colApptDoctor,
            colApptDate, colApptReason, colApptStatus, colApptActions;

    private final UserService userService = new UserService();
    private final AppointmentService appointmentService = new AppointmentService();
    private User currentUser;
    private ObservableList<User> userList = FXCollections.observableArrayList();
    private ObservableList<Appointment> apptList = FXCollections.observableArrayList();

    public void setUser(User user) {
        this.currentUser = user;
        if (welcomeLabel != null) welcomeLabel.setText("Welcome, " + user.getFullName());
        loadStats();
        setupTables();
        showOverview();
    }

    private void loadStats() {
        List<User> allUsers = userService.getAllUsers();
        long doctors = allUsers.stream().filter(u -> u.getRole() == Role.DOCTOR).count();
        long patients = allUsers.stream().filter(u -> u.getRole() == Role.PATIENT).count();
        long appts = appointmentService.getAllAppointments().size();

        totalUsersLabel.setText(String.valueOf(allUsers.size()));
        totalDoctorsLabel.setText(String.valueOf(doctors));
        totalPatientsLabel.setText(String.valueOf(patients));
        totalAppointmentsLabel.setText(String.valueOf(appts));
    }

    private void setupTables() {
        colUserId.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getUserId())));
        colUsername.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUsername()));
        colFullName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFullName()));
        colEmail.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmail()));
        colPhone.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPhone()));
        colRole.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRole().name()));

        colActions.setCellFactory(col -> new TableCell<>() {
            final Button delBtn = new Button("Delete");
            { delBtn.getStyleClass().add("btn-danger");
                delBtn.setOnAction(e -> deleteUser(getTableView().getItems().get(getIndex()))); }
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : delBtn);
            }
        });

        colApptId.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getAppointmentId())));
        colApptPatient.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPatientName()));
        colApptDoctor.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDoctorName()));
        colApptDate.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getAppointmentDate() != null ? c.getValue().getAppointmentDate().toString() : ""));
        colApptReason.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getReason()));
        colApptStatus.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatus().name()));

        apptFilterCombo.setItems(FXCollections.observableArrayList("ALL", "SCHEDULED", "COMPLETED", "CANCELLED"));
        apptFilterCombo.setValue("ALL");
    }

    @FXML private void showOverview() {
        switchView(overviewView, "Dashboard Overview", btnOverview);
        loadStats();
    }

    @FXML private void showUsers() {
        switchView(usersView, "User Management", btnUsers);
        loadUsers();
    }

    @FXML private void showAppointments() {
        switchView(appointmentsView, "Appointment Management", btnAppointments);
        loadAppointments();
    }

    private void switchView(VBox view, String title, Button activeBtn) {
        overviewView.setVisible(false); overviewView.setManaged(false);
        usersView.setVisible(false); usersView.setManaged(false);
        appointmentsView.setVisible(false); appointmentsView.setManaged(false);

        view.setVisible(true); view.setManaged(true);
        pageTitle.setText(title);

        btnOverview.getStyleClass().remove("sidebar-btn-active");
        btnUsers.getStyleClass().remove("sidebar-btn-active");
        btnAppointments.getStyleClass().remove("sidebar-btn-active");
        if (!activeBtn.getStyleClass().contains("sidebar-btn-active"))
            activeBtn.getStyleClass().add("sidebar-btn-active");
    }

    private void loadUsers() {
        userList.setAll(userService.getAllUsers());
        usersTable.setItems(userList);
    }

    @FXML private void searchUsers() {
        String q = userSearchField.getText().trim().toLowerCase();
        if (q.isEmpty()) { loadUsers(); return; }
        List<User> filtered = userService.getAllUsers().stream()
                .filter(u -> u.getFullName().toLowerCase().contains(q)
                        || u.getUsername().toLowerCase().contains(q))
                .toList();
        userList.setAll(filtered);
    }

    private void deleteUser(User user) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete user: " + user.getFullName() + "?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                if (userService.deleteUser(user.getUserId())) {
                    loadUsers(); loadStats();
                    new Alert(Alert.AlertType.INFORMATION, "✅ User deleted!").showAndWait();
                } else {
                    new Alert(Alert.AlertType.ERROR, "❌ Delete failed").showAndWait();
                }
            }
        });
    }

    @FXML private void addUserDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add New User");

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField u = new TextField(), n = new TextField(),
                e = new TextField(), p = new TextField(), pw = new PasswordField();
        ComboBox<String> role = new ComboBox<>();
        role.getItems().addAll("ADMIN", "DOCTOR", "PATIENT");
        role.setValue("PATIENT");

        grid.addRow(0, new Label("Username:"), u);
        grid.addRow(1, new Label("Full Name:"), n);
        grid.addRow(2, new Label("Email:"), e);
        grid.addRow(3, new Label("Phone:"), p);
        grid.addRow(4, new Label("Password:"), pw);
        grid.addRow(5, new Label("Role:"), role);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.OK) {
                if (u.getText().isEmpty() || n.getText().isEmpty() || pw.getText().isEmpty()) {
                    new Alert(Alert.AlertType.WARNING, "Fill required fields").showAndWait();
                    return;
                }
                User newUser;
                switch (role.getValue()) {
                    case "ADMIN" -> newUser = new Admin(0, u.getText(), pw.getText(), n.getText(), e.getText(), p.getText());
                    case "DOCTOR" -> newUser = new Doctor(0, u.getText(), pw.getText(), n.getText(), e.getText(), p.getText());
                    default -> newUser = new Patient(0, u.getText(), pw.getText(), n.getText(), e.getText(), p.getText());
                }
                if (userService.registerUser(newUser)) {
                    loadUsers(); loadStats();
                    new Alert(Alert.AlertType.INFORMATION, "✅ User added!").showAndWait();
                } else {
                    new Alert(Alert.AlertType.ERROR, "❌ Failed (username may exist)").showAndWait();
                }
            }
        });
    }

    @FXML private void loadAppointments() {
        List<Appointment> all = appointmentService.getAllAppointments();
        String filter = apptFilterCombo.getValue();
        if (filter != null && !filter.equals("ALL")) {
            all = all.stream().filter(a -> a.getStatus().name().equals(filter)).toList();
        }
        apptList.setAll(all);
        appointmentsTable.setItems(apptList);
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