package com.example.pets.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CadastroController {

    @FXML
    private TextField newNomeField;



    @FXML
    private TextField newEmailField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private Label cadastroMessageLabel;

    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/cadastro_pets";
    private static final String USER = "root";
    private static final String PASSWORD = "password";
    @FXML
    private void cadastrarUsuario(ActionEvent event) {
        String newNome = newNomeField.getText();
        String newEmail = newEmailField.getText();
        String newPassword = newPasswordField.getText();

        if (newNome.isEmpty() || newEmail.isEmpty() || newPassword.isEmpty()) {
            cadastroMessageLabel.setText("Preencha todos os campos.");
        } else if (cadastrar(newNome, newEmail, newPassword)) {

            exibirAlerta("Sucesso", "Usuário adicionado com sucesso!");
            loadLoginScreen(event);
        } else {
            cadastroMessageLabel.setText("Erro ao cadastrar. Tente novamente.");
        }
    }
    public void exibirAlerta(String titulo, String conteudo) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setContentText(conteudo);
        alert.showAndWait();
    }
    private boolean cadastrar(String nome, String email, String password) {
        String query = "INSERT INTO usuarios (nome, email, senha) VALUES (?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, nome);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, password);

            int rowsAffected = preparedStatement.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    private void loadLoginScreen(ActionEvent event) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/pets/Login.fxml"));
            Parent root = loader.load();


            Controller loginController = loader.getController();

            Stage stage = (Stage) loginHyperlink.getScene().getWindow();


            Scene scene = new Scene(root);
            stage.setScene(scene);


            loginController.initialize();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @FXML
    private Hyperlink loginHyperlink;


    @FXML
    private void goToLogin(ActionEvent event) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/pets/Login.fxml"));
            Parent root = loader.load();


            Controller loginController = loader.getController();


            Stage stage = (Stage) loginHyperlink.getScene().getWindow();


            Scene scene = new Scene(root);
            stage.setScene(scene);

            loginController.initialize();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}