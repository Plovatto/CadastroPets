package com.example.pets.controller;

import com.example.pets.model.Cachorro;
import com.example.pets.model.CachorroModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.ByteArrayInputStream;

public class DetalhesCachorroController {

    @FXML
    private Label nomeLabel;

    @FXML
    private Label idadeLabel;

    @FXML
    private Label racaLabel;

    @FXML
    private Label corLabel;

    @FXML
    private Label donoLabel;

    @FXML
    private ImageView imagemImageCachorro;

    private Cachorro cachorro;

    public void setCachorro(Cachorro cachorro) {
        this.cachorro = cachorro;
        preencherDetalhes();
    }

    private void preencherDetalhes() {
        if (cachorro != null) {
            nomeLabel.setText(cachorro.getNome());
            idadeLabel.setText( cachorro.getIdade() + " ano");
            racaLabel.setText(cachorro.getRaca());
            corLabel.setText(cachorro.getCor());
            donoLabel.setText(obterNomeDono(cachorro.getIdDono()));

            byte[] imagemBytes = cachorro.getImagem();
            if (imagemBytes != null && imagemBytes.length > 0) {
                try {
                    Image imagem = new Image(new ByteArrayInputStream(imagemBytes));
                    imagemImageCachorro.setImage(imagem);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("Erro ao carregar a imagem.");
                }
            }
        }
    }

    private String obterNomeDono(int idDono) {
        return CachorroModel.getInstance().getNomeDono(idDono);
    }
}
