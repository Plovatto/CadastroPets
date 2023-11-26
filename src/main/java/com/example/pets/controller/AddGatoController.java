package com.example.pets.controller;

import com.example.pets.model.Gato;
import com.example.pets.model.GatoModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class AddGatoController implements Initializable {
    private static AddGatoController instance;
    public static AddGatoController getInstance() {
        if (instance == null) {
            instance = new AddGatoController();
        }
        return instance;
    }

    private GatoModel gatoModel;

    @FXML
    private TextField nomeField;

    @FXML
    private TextField idadeField;

    @FXML
    private TextField corField;

    @FXML
    private TextField pelagemField;

    @FXML
    private ComboBox<String> donoComboBox;

    private byte[] imagem;

    @FXML
    private ImageView imagemGatoView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gatoModel = GatoModel.getInstance();
        ObservableList<String> donoList = FXCollections.observableArrayList(gatoModel.getNomesUsuarios());
        donoComboBox.setItems(donoList);
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
                imagemGatoView.setImage(imagem);
            } catch (IOException e) {
                e.printStackTrace();
                exibirAlerta("Erro", "Erro ao ler a imagem.");
            }
        }
    }

    @FXML
    public void adicionarGato(ActionEvent event) {
        try {
            String nome = nomeField.getText();
            String cor = corField.getText();
            String pelagem = pelagemField.getText();
            String nomeDonoSelecionado = donoComboBox.getValue();
            String idadeTexto = idadeField.getText();


            if (nome.isEmpty() || cor.isEmpty() || pelagem.isEmpty() || nomeDonoSelecionado == null || idadeTexto.isEmpty()) {
                exibirAlerta("Erro", "Preencha todos os campos obrigatórios.");
                return;
            }

            int idade = Integer.parseInt(idadeTexto);
            int idDono = gatoModel.getIdDonoSelecionado(nomeDonoSelecionado);
            int proximoIdGato = gatoModel.obterProximoIdGato();

            Gato novoGato = new Gato(nome, idade, cor, pelagem, idDono, imagem, proximoIdGato);
            gatoModel.adicionarGatoNoBanco(novoGato);
            exibirAlerta("Sucesso", "Gato adicionado com sucesso!");
            closeWindow(event);
        } catch (NumberFormatException e) {
            exibirAlerta("Erro", "Digite uma idade válida.");
        }
    }


    public void exibirAlerta(String titulo, String conteudo) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setContentText(conteudo);
        alert.showAndWait();
    }

    private void closeWindow(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
