package com.example.laboratorjavafx.controllers;

import com.example.laboratorjavafx.domain.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import com.example.laboratorjavafx.service.Service;

public class UserController {
    @FXML
    private TableView<User> usersTable;
    @FXML
    private TableColumn<User, Integer> idColumn;
    @FXML
    private TableColumn<User, String> firstNameColumn;
    @FXML
    private TableColumn<User, String> lastNameColumn;
    @FXML
    private TextField firstNameField, lastNameField;
    @FXML
    private Button addButton, updateButton, deleteButton;

    private Service service;
    private ObservableList<User> users = FXCollections.observableArrayList();

    public void setService(Service service) {
        this.service = service;
        initializeTable();
        loadUsers();
    }

    private void initializeTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        usersTable.setItems(users);
    }

    private void loadUsers() {
        users.clear();
        for (User user : service.getAllUsers()) {
            users.add(user);
        }
    }

    @FXML
    private void addUser() {
        try {
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            // Validate inputs
            if (firstName.isEmpty() || lastName.isEmpty()) {
                showAlert("Input Error", "First name and last name cannot be empty.");
                return;
            }
            User user = service.getUserByNumePrenume(firstName, lastName);
            service.addUser(user);
            loadUsers();
            clearForm();
            showConfirmation("User Added", "User successfully added.");
        } catch (Exception e) {
            showAlert("Error", "An error occurred: " + e.getMessage());
        }
    }

// Similar changes for updateUser and deleteUser

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearForm() {
        firstNameField.clear();
        lastNameField.clear();
    }

    @FXML
    private void updateUser() {
        try {
            User selectedUser = usersTable.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                String newFirstName = firstNameField.getText();
                String newLastName = lastNameField.getText();
                service.updateUser(selectedUser.getId(), newFirstName, newLastName);
                loadUsers();
            }
        } catch (Exception e) {
            e.printStackTrace(); // Replace with proper error handling
        }
    }

    @FXML
    private void deleteUser() {
        try {
            User selectedUser = usersTable.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                service.deleteUser(selectedUser.getEmail());
                loadUsers();
            }
        } catch (Exception e) {
            e.printStackTrace(); // Replace with proper error handling
        }
    }
}
