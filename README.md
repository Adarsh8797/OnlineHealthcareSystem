# 🏥 Online Healthcare Management System

A comprehensive Java-based healthcare management system built with **JavaFX**, **JDBC**, and **MySQL**. Features role-based dashboards for Admins, Doctors, and Patients.

---

## 🎯 Features

### 👨‍💼 Admin Dashboard
- User Management (Add, Delete, Search)
- View all appointments with status filter
- System statistics (total users, doctors, patients, appointments)

### 🩺 Doctor Dashboard
- View my appointments
- Update appointment status (Complete / Cancel)
- Access patient medical records

### 👤 Patient Dashboard
- Book new appointments with doctor, date, time
- View appointment history and cancel
- Update personal profile

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| **Language** | Java 21 |
| **UI** | JavaFX 21 + FXML + CSS |
| **Database** | MySQL 8 |
| **Connectivity** | JDBC |
| **Build Tool** | Maven |
| **IDE** | IntelliJ IDEA |

---

## 🧠 OOP Concepts Implemented

- **Inheritance** → `User` (abstract) → `Admin`, `Doctor`, `Patient`
- **Polymorphism** → `getDashboardFXML()` overridden in each subclass
- **Abstraction** → Abstract `User` class
- **Encapsulation** → Private fields + getters/setters
- **Interfaces & Generics** → `List<User>`, `List<Appointment>`
- **Exception Handling** → Try-catch in DAO and Services

## ⚡ Advanced Concepts

- **Multithreading & Synchronization** → `synchronized bookAppointment()` method prevents double-booking race conditions
- **Collections Framework** → `ArrayList`, `ObservableList` for JavaFX
- **JDBC** → `PreparedStatement`, `ResultSet`, transactions with `commit/rollback`

---

## 📁 Project Structure
