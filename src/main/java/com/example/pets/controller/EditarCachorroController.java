package com.example.pets.controller;

import com.example.pets.model.Cachorro;
import com.example.pets.model.CachorroModel;
import com.example.pets.model.Gato;
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

public class EditarCachorroController {

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
    private TextField racaField;
    @FXML
    private ImageView imagemCachorroView;
    private byte[] imagem;

    private Cachorro cachorro;

    private CachorroModel cachorroModel = new CachorroModel();

    @FXML
    private void initialize() {
        ObservableList<String> donoList = FXCollections.observableArrayList(getNomesUsuarios());
        donoComboBox.setItems(donoList);
    }

    public void setCachorro(Cachorro cachorro) {
        this.cachorro = cachorro;

        if (cachorro != null) {
            nomeField.setText(cachorro.getNome());
            idadeField.setText(String.valueOf(cachorro.getIdade()));
            corField.setText(cachorro.getCor());
            racaField.setText(cachorro.getRaca());

            if (cachorro.getImagem() != null) {
                Image imagem = new Image(new ByteArrayInputStream(cachorro.getImagem()));
                imagemCachorroView.setImage(imagem);
            }

            String nomeDono = getNomeDono(cachorro.getIdDono());
            donoComboBox.setValue(nomeDono);
        } else {
            nomeField.clear();
            idadeField.clear();
            corField.clear();
            racaField.clear();
            donoComboBox.setValue(null);

            imagemCachorroView.setImage(null);
        }
    }

    private String getNomeDono(int id_dono) {
        return cachorroModel.getNomeDono(id_dono);
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
                imagemCachorroView.setImage(img);

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
        return cachorroModel.getNomesUsuarios();
    }

    @FXML
    private void salvarEdicao(ActionEvent event) {
        try {
            String novoNome = nomeField.getText();
            int novaIdade = Integer.parseInt(idadeField.getText());
            String novaCor = corField.getText();
            String novaRaca = racaField.getText();
            int novoIdDono = getIdDonoSelecionado();

            Cachorro novoCachorro = new Cachorro(novoNome, novaIdade, novaCor, novaRaca, novoIdDono, imagem, cachorro.getIdCachorro());
            cachorroModel.atualizarCachorro(novoCachorro);

            exibirAlerta("Sucesso", "Cachorro editado com sucesso!");

            Stage stage = (Stage) nomeField.getScene().getWindow();
            stage.close();
            System.out.println("Novo Nome: " + novoNome);
        } catch (NumberFormatException e) {
            exibirAlerta("Erro", "Digite uma idade válida.");
        }
    }

    private int getIdDonoSelecionado() {
        String nomeDonoSelecionado = donoComboBox.getValue();
        return cachorroModel.getIdDonoSelecionado(nomeDonoSelecionado);
    }
}
