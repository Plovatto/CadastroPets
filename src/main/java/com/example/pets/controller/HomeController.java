package com.example.pets.controller;

import com.example.pets.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import com.example.pets.controller.AddGatoController;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HomeController {

    @FXML
    private ListView<String> gatosListView;

    @FXML
    private ListView<String> cachorrosListView;

    @FXML
    private TextField novoGatoField;

    @FXML
    private TextField novoCachorroField;

    @FXML
    private AnchorPane homeAnchorPane;
    private String DB_URL = "jdbc:mysql://127.0.0.1:3306/cadastro_pets";
    private String USER = "root";
    private String PASSWORD = "password";
    private FXMLLoader addGatoLoader = new FXMLLoader(getClass().getResource("/com/example/pets/AddGato.fxml"));
    private FXMLLoader addCachorroLoader = new FXMLLoader(getClass().getResource("/com/example/pets/AddCachorro.fxml"));

    @FXML
    public void initialize() {
        List<String> gatos = getNomesPets("gatos");
        gatosListView.getItems().addAll(gatos);
        novoCachorroField = new TextField();
        novoGatoField = new TextField();
        List<String> cachorros = getNomesPets("cachorros");
        cachorrosListView.getItems().addAll(cachorros);

        ObservableList<String> gatosObservable = FXCollections.observableArrayList(gatos);
        gatosListView.setItems(gatosObservable);
        gatosListView.setCellFactory(param -> new EditButtonListCell(gatosListView));

        ObservableList<String> cachorrosObservable = FXCollections.observableArrayList(cachorros);
        cachorrosListView.setItems(cachorrosObservable);
        cachorrosListView.setCellFactory(param -> new EditButtonListCell(cachorrosListView));

        gatosListView.setOnMouseClicked(event -> handlePetListViewClickGato(gatosListView, "gatos"));
        cachorrosListView.setOnMouseClicked(event -> handlePetListViewClickCachorro(cachorrosListView, "cachorros"));
    }
    private void atualizarListView(ListView<String> listView, String tipoPet) {
        listView.getItems().clear();
        List<String> nomesPets = getNomesPets(tipoPet);
        listView.getItems().addAll(nomesPets);
        listView.refresh();
    }
    public void atualizarListaGatos(ListView<String> listView, String tipoPet) {
        List<String> gatos = getNomesPets("gatos");
        gatosListView.getItems().clear();
        gatosListView.getItems().addAll(gatos);
        gatosListView.refresh();
        listView.getItems().clear();
        List<String> nomesPets = getNomesPets(tipoPet);
        listView.getItems().addAll(nomesPets);
        listView.refresh();
    }

    public void atualizarListaCachorros() {
        List<String> cachorros = getNomesPets("cachorros");
        cachorrosListView.getItems().clear();
        cachorrosListView.getItems().addAll(cachorros);
        cachorrosListView.refresh();
    }
    private void handlePetListViewClickGato(ListView<String> listView, String tipoPet) {
        if (listView.getSelectionModel().getSelectedItem() != null) {
            String nomePet = listView.getSelectionModel().getSelectedItem();
            int idPet = GatoModel.getInstance().obterIdDoGatoPeloNome(nomePet);

            if (idPet != -1) {
                exibirDetalhesGato(idPet);
            } else {
                System.out.println("ID do Pet não encontrado para o nome: " + nomePet);
            }
        }
    }
    private void handlePetListViewClickCachorro(ListView<String> listView, String tipoPet) {
        if (listView.getSelectionModel().getSelectedItem() != null) {
            String nomePet = listView.getSelectionModel().getSelectedItem();
            int idPet = CachorroModel.getInstance().obterIdDoCachorroPeloNome(nomePet);

            if (idPet != -1) {
                exibirDetalhesCachorro(idPet);
            } else {
                System.out.println("ID do Pet não encontrado para o nome: " + nomePet);
            }
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
            exibirAlerta("Aviso", "Gato não encontrado com o ID: " + idCachorro);
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


    private int obterIdDoGatoPeloNome(String tipoPet, String nomeGato) {
        int idGato = -1;

        String query = "SELECT id_gato FROM gatos WHERE nome = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, nomeGato);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                System.out.println("gdsbebucesso.");

                if (resultSet.next()) {
                    idGato = resultSet.getInt(1);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            exibirAlerta("Erro", "Erro ao obter o ID do gato do banco de dados.");
        }

        return idGato;
    }
    private int obterIdDoCachorroPeloNome(String tipoPet, String nomeCachorro) {
        int idCachorro = -1;

        String query = "SELECT id_cachorro FROM cachorros WHERE nome = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, nomeCachorro);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                System.out.println("sucessofssvafbf");

                if (resultSet.next()) {
                    idCachorro = resultSet.getInt(1);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            exibirAlerta("Erro", "Erro ao obter o ID do gato do banco de dados.");
        }

        return idCachorro;
    }
    private List<String> getNomesPets(String tipoPet) {
        List<String> nomesPets = new ArrayList<>();
        String query = "SELECT nome FROM " + tipoPet;

        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                nomesPets.add(resultSet.getString("nome"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            exibirAlerta("Erro", "Erro ao obter os nomes dos " + tipoPet + " do banco de dados.");
        }

        return nomesPets;
    }

    private void exibirAlerta(String titulo, String conteudo) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titulo);
        alert.setContentText(conteudo);
        alert.showAndWait();
    }

    @FXML
    public void addCat(ActionEvent actionEvent) {
        String novoGato = novoGatoField.getText();

        ObservableList<String> gatos = gatosListView.getItems();
        gatos.add(novoGato);
        novoGatoField.clear();

        AddGatoController addGatoController = addGatoLoader.getController();

        FXMLLoader newLoader = new FXMLLoader(getClass().getResource("/com/example/pets/AddGato.fxml"));

        try {
            Parent root = newLoader.load();

            Stage stage = new Stage();
            stage.setTitle("Adicionar Gato");
            stage.setScene(new Scene(root));

            stage.showAndWait();

            atualizarListView(gatosListView, "gatos");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void addDog(ActionEvent actionEvent) {
        String novoCachorro = novoCachorroField.getText();

        ObservableList<String> cachorro = cachorrosListView.getItems();
        cachorro.add(novoCachorro);
        novoCachorroField.clear();

        AddCachorroController addGatoController = addCachorroLoader.getController();

        FXMLLoader newLoader = new FXMLLoader(getClass().getResource("/com/example/pets/AddCachorro.fxml"));

        try {
            Parent root = newLoader.load();

            Stage stage = new Stage();
            stage.setTitle("Adicionar Cachorro");
            stage.setScene(new Scene(root));

            stage.showAndWait();

            atualizarListView(cachorrosListView, "cachorros");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void editarGato(int idPet, String tipoPet) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/pets/EditarGato.fxml"));
            Parent root = loader.load();

            Object editarPetController = loader.getController();

            if (editarPetController instanceof EditarGatoController) {
                Gato gato = GatoModel.getInstance().obterDetalhesGato(idPet);
                ((EditarGatoController) editarPetController).setGato(gato);

                Stage stage = new Stage();
                stage.setTitle("Editar Gato");
                stage.setScene(new Scene(root));

                stage.showAndWait();

                atualizarListView(gatosListView, "gatos");
            }

        } catch (IOException e) {
            e.printStackTrace();
            exibirAlerta("Erro", "Erro ao carregar a tela de edição do pet.");
        }
    }
    @FXML
    private void editarCachorro(int idPet, String tipoPet) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/pets/EditarCachorro.fxml"));
            Parent root = loader.load();

            Object editarPetController = loader.getController();

            if (editarPetController instanceof EditarCachorroController) {
                Cachorro cachorro = CachorroModel.getInstance().obterDetalhesCachorro(idPet);
                ((EditarCachorroController) editarPetController).setCachorro(cachorro);

                Stage stage = new Stage();
                stage.setTitle("Editar Cachorro");
                stage.setScene(new Scene(root));

                stage.showAndWait();

                atualizarListView(cachorrosListView, "cachorros");
            }

        } catch (IOException e) {
            e.printStackTrace();
            exibirAlerta("Erro", "Erro ao carregar a tela de edição do pet.");
        }
    }

    @FXML
    private void excluirPet(String nomePet) {
        String tipoPet;
        if (gatosListView.getItems().contains(nomePet)) {
            tipoPet = "gatos";
        } else if (cachorrosListView.getItems().contains(nomePet)) {
            tipoPet = "cachorros";
        } else {
            exibirAlerta("Erro", "Pet não encontrado.");
            return;
        }

        ImageView imageView = new ImageView();


        if ("gatos".equals(tipoPet)) {
            imageView.setFitWidth(100);
            imageView.setFitHeight(100);
            Image gatoImage = new Image(getClass().getClassLoader().getResourceAsStream("confirmcat.png"));
            imageView.setImage(gatoImage);
        } else if ("cachorros".equals(tipoPet)) {
            imageView.setFitWidth(120);
            imageView.setFitHeight(80);
            Image cachorroImage = new Image(getClass().getClassLoader().getResourceAsStream("confimdog.png"));
            imageView.setImage(cachorroImage);
        }


        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(nomePet);
        alert.setHeaderText("Tem certeza que deseja me excluir? ");
        alert.setGraphic(imageView);

        ButtonType confirmButtonType = new ButtonType("Sim", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(confirmButtonType, cancelButtonType);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == confirmButtonType) {
            String query;
            if ("gatos".equals(tipoPet)) {
                query = "DELETE FROM gatos WHERE nome = ?";
            } else if ("cachorros".equals(tipoPet)) {
                query = "DELETE FROM cachorros WHERE nome = ?";
            } else {
                exibirAlerta("Erro", "Tipo de pet desconhecido.");
                return;
            }

            try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                preparedStatement.setString(1, nomePet);

                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    if ("gatos".equals(tipoPet)) {
                        gatosListView.getItems().remove(nomePet);
                    } else if ("cachorros".equals(tipoPet)) {
                        cachorrosListView.getItems().remove(nomePet);
                    }
                } else {
                    exibirAlerta("Erro", "Pet não encontrado no banco de dados.");
                }

            } catch (SQLException e) {
                e.printStackTrace();
                exibirAlerta("Erro", "Erro ao excluir pet do banco de dados.");
            }
        }
    }

    public class EditButtonListCell extends ListCell<String> {
        private HBox hbox = new HBox();
        private Button editButton = new Button("✎");
        private Button deleteButton = new Button("\uD83D\uDDD1");
        private ListView<String> listView;

        public EditButtonListCell(ListView<String> listView) {
            this.listView = listView;

            editButton.setOnAction(event -> {
                String nomePet = getItem();
                String tipoPet;
                if (gatosListView.getItems().contains(nomePet)) {
                    tipoPet = "gatos";
                } else if (cachorrosListView.getItems().contains(nomePet)) {
                    tipoPet = "cachorros";
                } else {
                    exibirAlerta("Erro", "Pet não encontrado.");
                    return;
                }
                if ("gatos".equals(tipoPet)) {

                    int idPet = obterIdDoGatoPeloNome(listView.getId(), nomePet);
                    if (idPet != -1) {
                        editarGato(idPet, listView.getId());
                    } else {
                        System.out.println("ID do Pet não encontrado para o nome: " + nomePet);
                    }
                } else if ("cachorros".equals(tipoPet)) {
                    int idPet = obterIdDoCachorroPeloNome(listView.getId(), nomePet);
                    if (idPet != -1) {
                        editarCachorro(idPet, listView.getId());
                    } else {
                        System.out.println("ID do Pet não encontrado para o nome: " + nomePet);
                    }
                }

            });

            deleteButton.setOnAction(event -> {
                String petName = getItem();
                excluirPet(petName);
            });


            hbox.setStyle("-fx-text-fill: #8c3b23;-fx-font-size: 15;");
            editButton.setStyle("-fx-background-color:   #FFC092; -fx-text-fill: #FFFFFF;-fx-background-radius: 20;-fx-font-size: 14;");
            deleteButton.setStyle("-fx-background-color: #EB663B; -fx-text-fill: white; -fx-background-radius: 20;-fx-font-size: 14;");
            deleteButton.setMinHeight(32.0);
            deleteButton.setMinWidth(32.0);
            editButton.setMinHeight(32.0);
            editButton.setMinWidth(32.0);
            this.setStyle("-fx-background-color:  #FFFFFF; -fx-text-fill: #702c08;");



            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            hbox.getChildren().addAll(new Label(), spacer, editButton, deleteButton);
            hbox.setSpacing(10);
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            } else {
                ((Label)hbox.getChildren().get(0)).setText(item);
                setGraphic(hbox);
            }
        }

    }
    @FXML
    private void openProfileScreen(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/pets/perfil.fxml"));
            Parent root = loader.load();

            Scene profileScene = new Scene(root);

            Stage profileStage = new Stage();
            profileStage.setTitle("Perfil do Usuário");

            profileStage.initModality(Modality.APPLICATION_MODAL);

            profileStage.setScene(profileScene);

            Stage homeStage = (Stage) homeAnchorPane.getScene().getWindow();

            profileStage.initOwner(homeStage);

            profileStage.show();

            ProfileController profileController = loader.getController();

            int authenticatedUserId = Controller.getAuthenticatedUserId();
            profileController.initialize(authenticatedUserId);

        } catch (IOException e) {
            e.printStackTrace();
        }}
    }
