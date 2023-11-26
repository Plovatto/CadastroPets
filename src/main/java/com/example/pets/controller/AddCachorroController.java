package com.example.pets.controller;

import com.example.pets.model.Cachorro;
import com.example.pets.model.CachorroModel;
import com.example.pets.model.GatoModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.example.pets.model.Cachorro;
public class AddCachorroController {
    private CachorroModel cachorroModel;
    private GatoModel gatoModel;
    @FXML
    private TextField nomeField;

    @FXML
    private TextField idadeField;

    @FXML
    private TextField racaField;

    @FXML
    private TextField corField;

    @FXML
    private ComboBox<String> donoComboBox;
    public  String DB_URL = "jdbc:mysql://127.0.0.1:3306/cadastro_pets";
    public  String USER = "root";
    public String PASSWORD = "password";
    private byte[] imagem;
    @FXML
    private ImageView imagemCachorroView;

    @FXML
    public void initialize() {

        ObservableList<String> donoList = FXCollections.observableArrayList(getNomesUsuarios());
        donoComboBox.setItems(donoList);
    }

    @FXML
    private void adicionarCachorro(ActionEvent event) {
        try {

            String nome = nomeField.getText();
            int idade = Integer.parseInt(idadeField.getText());
            String raca = racaField.getText();
            String cor = corField.getText();
            String nomeDonoSelecionado = donoComboBox.getValue();


            if (nome.isEmpty() || cor.isEmpty() || raca.isEmpty() || idadeField.getText().isEmpty() || nomeDonoSelecionado == null) {
                exibirAlerta("Erro", "Preencha todos os campos obrigatórios.");
                return;
            }

            int idDono = getIdDonoSelecionado(nomeDonoSelecionado);
            int proximoIdCachorro = obterProximoIdCachorro();


            Cachorro novoCachorro = new Cachorro(nome, idade, raca, cor, idDono, imagem, proximoIdCachorro);


            adicionarCachorroNoBanco(novoCachorro);


            closeWindow(event);

        } catch (NumberFormatException e) {
            exibirAlerta("Erro", "Digite uma idade válida.");
        }
    }



    @FXML
    private void selecionarImagem(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecione uma Imagem");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imagens", "*.png", "*.jpg", "*.gif")
        );


        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        File arquivoImagem = fileChooser.showOpenDialog(stage);

        if (arquivoImagem != null) {
            try {

                imagem = Files.readAllBytes(arquivoImagem.toPath());
   Image imagem = new Image(arquivoImagem.toURI().toString());
                imagemCachorroView.setImage(imagem);
            } catch (IOException e) {
                e.printStackTrace();
                exibirAlerta("Erro", "Erro ao ler a imagem.");
            }
        }
    }

    private void closeWindow(ActionEvent event) {

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();


        stage.close();
    }

    private void exibirAlerta(String titulo, String conteudo) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setContentText(conteudo);
        alert.showAndWait();
    }
    public List<String> getNomesUsuarios() {
        List<String> nomesUsuarios = new ArrayList<>();
        String query = "SELECT nome FROM usuarios";

        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                nomesUsuarios.add(resultSet.getString("nome"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            exibirAlerta("Erro", "Erro ao obter os nomes dos usuários do banco de dados.");
        }

        return nomesUsuarios;
    }



    public int getIdDonoSelecionado(String nomeDono) {
        int idDono = -1;
        String query = "SELECT id_usuario FROM usuarios WHERE nome = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, nomeDono);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    idDono = resultSet.getInt("id_usuario");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            exibirAlerta("Erro", "Erro ao obter o ID do dono do banco de dados.");
        }

        return idDono;
    }
    public static int obterProximoIdCachorro() {
        int proximoId = -1;
        String DB_URL = "jdbc:mysql://127.0.0.1:3306/cadastro_pets";
        String USER = "root";
        String PASSWORD = "password";
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {


            ResultSet resultSet = statement.executeQuery("SELECT MAX(id_cachorro) + 1 AS proximo_id FROM cachorros");

            if (resultSet.next()) {
                proximoId = resultSet.getInt("proximo_id");


                if (resultSet.wasNull()) {
                    proximoId = 1;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return proximoId;
    }

    public void adicionarCachorroNoBanco(Cachorro cachorro) {

        String query;
        if (cachorro.getImagem() != null && cachorro.getImagem().length > 0) {

            query = "INSERT INTO cachorros (nome, idade, cor, raca, id_dono, imagem) VALUES (?, ?, ?, ?, ?, ?)";
        } else {

            query = "INSERT INTO cachorros (nome, idade, cor, raca, id_dono) VALUES (?, ?, ?, ?, ?)";
        }

        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, cachorro.getNome());
            preparedStatement.setInt(2, cachorro.getIdade());
            preparedStatement.setString(3, cachorro.getRaca());
            preparedStatement.setString(4, cachorro.getCor());
            preparedStatement.setInt(5, cachorro.getIdDono());


            if (cachorro.getImagem() != null && cachorro.getImagem().length > 0) {
                preparedStatement.setBytes(6, cachorro.getImagem());
            }

            preparedStatement.executeUpdate();

            exibirAlerta("Sucesso", "Cachorro adicionado com sucesso!");

        } catch (SQLException e) {
            e.printStackTrace();
            exibirAlerta("Erro", "Erro ao adicionar o cachorro ao banco de dados.");
        }
    }
}
