package com.example.pets.controller;

import com.example.pets.model.Gato;
import com.example.pets.model.GatoModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.ByteArrayInputStream;
import java.util.Base64;

public class DetalhesGatoController {

    @FXML
    private Label nomeLabel;

    @FXML
    private Label idadeLabel;

    @FXML
    private Label pelagemLabel;

    @FXML
    private Label corLabel;

    @FXML
    private Label donoLabel;

    @FXML
    private ImageView imagemImageView;

    private Gato gato;

    public void setGato(Gato gato) {
        this.gato = gato;
        preencherDetalhes();
    }

    private void preencherDetalhes() {
        if (gato != null) {
            nomeLabel.setText(gato.getNome());
            idadeLabel.setText(gato.getIdade() + " ano");
            pelagemLabel.setText(gato.getPelagem());
            corLabel.setText(gato.getCor());
            donoLabel.setText(obterNomeDono(gato.getIdDono()));

            byte[] imagemBytes = gato.getImagem();
            if (imagemBytes != null && imagemBytes.length > 0) {
                try {
                    Image imagem = new Image(new ByteArrayInputStream(imagemBytes));
                    imagemImageView.setImage(imagem);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("Erro ao carregar a imagem.");
                }
            }
        }
    }

    private String obterNomeDono(int idDono) {
        return GatoModel.getInstance().getNomeDono(idDono);
    }
}
