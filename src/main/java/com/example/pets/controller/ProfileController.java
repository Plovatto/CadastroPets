package com.example.pets.controller;

import com.example.pets.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ProfileController {

    @FXML
    private Label usernameLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private ListView<String> gatosListView;

    @FXML
    private ListView<String> cachorrosListView;

    private String DB_URL = "jdbc:mysql://127.0.0.1:3306/cadastro_pets";
    private String USER = "root";
    private String PASSWORD = "password";

    public void initialize(int userId) {
        UserProfile userProfile = fetchUserProfile(userId);

        if (userProfile != null) {
            updateUI(userProfile);
        }

        setListViewTransparent(gatosListView);
        setListViewTransparent(cachorrosListView);
        gatosListView.setStyle("-fx-text-fill: #702c08;");
        cachorrosListView.setStyle("-fx-text-fill: #702c08;");
    }
    @FXML
    private void goBackToHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/pets/Home.fxml"));
            Parent root = loader.load();

            Scene homeScene = new Scene(root);

            Stage profileStage = (Stage) usernameLabel.getScene().getWindow();

            profileStage.setScene(homeScene);

            profileStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setListViewTransparent(ListView<String> listView) {
        listView.setCellFactory(param -> new ListCell<>() {
            {
                setBackground(null);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(item);
            }
        });
    }

    private void updateUI(UserProfile userProfile) {
        usernameLabel.setText(userProfile.getUsername());
        emailLabel.setText(userProfile.getEmail());

        List<String> gatos = getNomesPets(userProfile.getUserId(), "gatos");
        List<String> cachorros = getNomesPets(userProfile.getUserId(), "cachorros");

        ObservableList<String> gatosObservable = FXCollections.observableArrayList(gatos);
        gatosListView.setItems(gatosObservable);

        ObservableList<String> cachorrosObservable = FXCollections.observableArrayList(cachorros);
        cachorrosListView.setItems(cachorrosObservable);

        gatosListView.setOnMouseClicked(event -> handlePetListViewClick(gatosListView, userProfile.getUserId(), "gatos"));
        cachorrosListView.setOnMouseClicked(event -> handlePetListViewClick(cachorrosListView, userProfile.getUserId(), "cachorros"));
    }

    private void handlePetListViewClick(ListView<String> listView, int userId, String tipoPet) {
        if (listView.getSelectionModel().getSelectedItem() != null) {
            String nomePet = listView.getSelectionModel().getSelectedItem();
            int idPet = obterIdDoPetPeloNome(userId, tipoPet, nomePet);

            if (idPet != -1) {
                exibirDetalhesPet(idPet, tipoPet);
            } else {
                System.out.println("ID do Pet não encontrado para o nome: " + nomePet);
            }
        }
    }

    private void exibirDetalhesPet(int idPet, String tipoPet) {
        if ("gatos".equals(tipoPet)) {
            exibirDetalhesGato(idPet);
        } else if ("cachorros".equals(tipoPet)) {
            exibirDetalhesCachorro(idPet);
        }
    }

    private void exibirDetalhesGato(int idGato) {
        Gato gato = GatoModel.getInstance().obterDetalhesGato(idGato);

        if (gato != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/pets/DetalhesGato.fxml"));
                Parent root = loader.load();

                DetalhesGatoController detalhesGatoController = loader.getController();
                detalhesGatoController.setGato(gato);

                Stage stage = new Stage();
                stage.setTitle("Detalhes do Gato");
                stage.setScene(new Scene(root));
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
                exibirAlerta("Erro", "Erro ao carregar a tela de detalhes do Gato.");
            }
        } else {
            exibirAlerta("Aviso", "Gato não encontrado com o ID: " + idGato);
        }
    }

    private void exibirDetalhesCachorro(int idCachorro) {
        Cachorro cachorro = CachorroModel.getInstance().obterDetalhesCachorro(idCachorro);

        if (cachorro != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/pets/DetalhesCachorro.fxml"));
                Parent root = loader.load();

                DetalhesCachorroController detalhesCachorroController = loader.getController();
                detalhesCachorroController.setCachorro(cachorro);

                Stage stage = new Stage();
                stage.setTitle("Detalhes do Cachorro");
                stage.setScene(new Scene(root));
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
                exibirAlerta("Erro", "Erro ao carregar a tela de detalhes do Cachorro.");
            }
        } else {
            exibirAlerta("Aviso", "Cachorro não encontrado com o ID: " + idCachorro);
        }
    }

    private int obterIdDoPetPeloNome(int userId, String tipoPet, String nomePet) {
        int idPet = -1;
        String query;

        if ("gatos".equals(tipoPet)) {
            query = "SELECT id_gato FROM gatos WHERE id_dono = ? AND nome = ?";
        } else if ("cachorros".equals(tipoPet)) {
            query = "SELECT id_cachorro FROM cachorros WHERE id_dono = ? AND nome = ?";
        } else {
            return idPet;
        }

        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, nomePet);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    idPet = resultSet.getInt(1);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            exibirAlerta("Erro", "Erro ao obter o ID do pet do banco de dados.");
        }

        return idPet;
    }

    private List<String> getNomesPets(int userId, String tipoPet) {
        List<String> nomesPets = new ArrayList<>();
        String query;

        if ("gatos".equals(tipoPet)) {
            query = "SELECT nome FROM gatos WHERE id_dono = ?";
        } else if ("cachorros".equals(tipoPet)) {
            query = "SELECT nome FROM cachorros WHERE id_dono = ?";
        } else {
            return nomesPets;
        }

        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    nomesPets.add(resultSet.getString("nome"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            exibirAlerta("Erro", "Erro ao obter os nomes dos pets do banco de dados.");
        }

        return nomesPets;
    }


    private void exibirAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private UserProfile fetchUserProfile(int userId) {
        String query = "SELECT u.nome AS username, u.email, GROUP_CONCAT(DISTINCT g.nome) AS gatos, GROUP_CONCAT(DISTINCT c.nome) AS cachorros " +
                "FROM usuarios u " +
                "LEFT JOIN gatos g ON u.id_usuario = g.id_dono " +
                "LEFT JOIN cachorros c ON u.id_usuario = c.id_dono " +
                "WHERE u.id_usuario = ? " +
                "GROUP BY u.id_usuario";

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/cadastro_pets", "root", "password");
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, userId);

            ResultSet resultSet = preparedStatement.executeQuery();

            List<String> gatos = new ArrayList<>();
            List<String> cachorros = new ArrayList<>();


                if (resultSet.next()) {
                    String username = resultSet.getString("username");
                    String email = resultSet.getString("email");
                    String gatosString = resultSet.getString("gatos");
                    String cachorrosString = resultSet.getString("cachorros");

                    if (gatosString != null) {
                        gatos = Arrays.asList(gatosString.split(","));
                    } else {
                        System.out.println("A string de gatos é nula");
                    }

                    if (cachorrosString != null) {
                        cachorros = Arrays.asList(cachorrosString.split(","));
                    } else {
                        System.out.println("A string de cachorros é nula");
                    }

                    return new UserProfile(userId, username, email, cachorros, gatos);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return null;}
    @FXML
    private Button loginButton;
    @FXML
    private void goToLogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/pets/Login.fxml"));
            Parent root = loader.load();

            Controller loginController = loader.getController();

            Stage stage = (Stage) loginButton.getScene().getWindow();

            Scene scene = new Scene(root);
            stage.setScene(scene);

            loginController.initialize();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
