package com.example.pets.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Controller {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;
    private static int authenticatedUserId;
    @FXML
    private Label messageLabel;

    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/cadastro_pets";
    private static final String USER = "root";
    private static final String PASSWORD = "password";

    @FXML
    private void loginButtonClicked(ActionEvent event) {
        String enteredUsername = usernameField.getText();
        String enteredPassword = passwordField.getText();

        if (authenticateUser(enteredUsername, enteredPassword)) {
            messageLabel.setText("Login bem-sucedido!");
            System.out.println("ID do usuário autenticado: " + getAuthenticatedUserId());
            loadHomeScreen(event);
        } else {
            messageLabel.setText("Credenciais inválidas. Tente novamente.");
        }
    }



    private boolean authenticateUser(String username, String password) {
        String query = "SELECT id_usuario FROM usuarios WHERE email = ? AND senha = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                authenticatedUserId = resultSet.getInt("id_usuario");
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }


    public static int getAuthenticatedUserId() {
        return authenticatedUserId;
    }


    @FXML
    private void openAddPetScreen(ActionEvent event) {
        openAddPetScreen("AddGato.fxml", ((Node) event.getSource()));
    }

    @FXML
    private void loadHomeScreen(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader();
            InputStream fxmlStream = getClass().getResourceAsStream("/com/example/pets/Home.fxml");
            Parent homeScreen = loader.load(fxmlStream);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();


            stage.setScene(new Scene(homeScreen));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openAddPetScreen(String fxmlPath, Node node) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/pets/" + fxmlPath));
            Parent root = loader.load();
            Object controller = loader.getController();


            try {
                Method initDataMethod = controller.getClass().getMethod("initData");
                initDataMethod.invoke(controller);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }


            Stage stage = (Stage) node.getScene().getWindow();


            stage.setScene(new Scene(root));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }   @FXML
    private void cadastroButtonClicked(ActionEvent event) {

        loadCadastroScreen(event);
    }

    private void loadCadastroScreen(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader();
            InputStream fxmlStream = getClass().getResourceAsStream("/com/example/pets/Cadastro.fxml");
            Parent cadastroScreen = loader.load(fxmlStream);


            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setScene(new Scene(cadastroScreen));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    protected void loadLoginScreen(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader();
            InputStream fxmlStream = getClass().getResourceAsStream("/com/example/pets/Login.fxml");
            Parent loginScreen = loader.load(fxmlStream);


            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();


            stage.setScene(new Scene(loginScreen));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void initialize() {
    }
}