package com.example.pets.controller;

import com.example.pets.model.Gato;
import com.example.pets.model.GatoModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class EditarGatoController {

    @FXML
    private ComboBox<String> donoComboBox;

    @FXML
    private Button selecionarImagemButton;
    @FXML
    private TextField nomeField;

    @FXML
    private TextField idadeField;

    @FXML
    private TextField corField;

    @FXML
    private TextField pelagemField;
    @FXML
    private ImageView imagemGatoView;
    private byte[] imagem;

    private Gato gato;

    private GatoModel gatoModel = new GatoModel();

    @FXML
    private void initialize() {
        ObservableList<String> donoList = FXCollections.observableArrayList(getNomesUsuarios());
        donoComboBox.setItems(donoList);
    }

    public void setGato(Gato gato) {
        this.gato = gato;

        if (gato != null) {
            nomeField.setText(gato.getNome());
            idadeField.setText(String.valueOf(gato.getIdade()));
            corField.setText(gato.getCor());
            pelagemField.setText(gato.getPelagem());

            if (gato.getImagem() != null) {
                Image imagem = new Image(new ByteArrayInputStream(gato.getImagem()));
                imagemGatoView.setImage(imagem);
            }

            String nomeDono = getNomeDono(gato.getIdDono());
            donoComboBox.setValue(nomeDono);
        } else {
            nomeField.clear();
            idadeField.clear();
            corField.clear();
            pelagemField.clear();
            donoComboBox.setValue(null);

            imagemGatoView.setImage(null);
        }
    }



    private String getNomeDono(int id_dono) {
        return gatoModel.getNomeDono(id_dono);
    }


    @FXML
    private void selecionarImagem() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecionar Imagem");

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Imagens", "*.png", "*.jpg", "*.gif");
        fileChooser.getExtensionFilters().add(extFilter);

        File selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile != null) {
            try {
                byte[] imagemBytes = Files.readAllBytes(selectedFile.toPath());

                Image img = new Image(new ByteArrayInputStream(imagemBytes));
                imagemGatoView.setImage(img);

                imagem = imagemBytes;
            } catch (IOException e) {
                e.printStackTrace();
                exibirAlerta("Erro", "Erro ao ler a imagem.");
            }
        }
    }




    private void exibirAlerta(String titulo, String conteudo) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setContentText(conteudo);
        alert.showAndWait();
    }

    private List<String> getNomesUsuarios() {
        return gatoModel.getNomesUsuarios();
    }

    @FXML
    private void salvarEdicao(ActionEvent event) {
        try {
            String novoNome = nomeField.getText();
            int novaIdade = Integer.parseInt(idadeField.getText());
            String novaCor = corField.getText();
            String novaPelagem = pelagemField.getText();
            int novoIdDono = getIdDonoSelecionado();

            Gato novoGato = new Gato(novoNome, novaIdade, novaCor, novaPelagem, novoIdDono, imagem, gato.getIdGato());
            gatoModel.atualizarGato(novoGato);

            exibirAlerta("Sucesso", "Gato editado com sucesso!");

            Stage stage = (Stage) nomeField.getScene().getWindow();
            stage.close();
            System.out.println("Novo Nome: " + novoNome);
        } catch (NumberFormatException e) {
            exibirAlerta("Erro", "Digite uma idade válida.");
        }
    }

    private int getIdDonoSelecionado() {
        String nomeDonoSelecionado = donoComboBox.getValue();
        return gatoModel.getIdDonoSelecionado(nomeDonoSelecionado);
    }
}
