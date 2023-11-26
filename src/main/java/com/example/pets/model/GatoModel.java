package com.example.pets.model;

import com.example.pets.controller.AddGatoController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GatoModel { @FXML
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
    private AddGatoController addGatoController;

    private static GatoModel instance;
    public static GatoModel getInstance() {
        if (instance == null) {
            instance = new GatoModel();
        }
        return instance;
    }
    private String DB_URL = "jdbc:mysql://127.0.0.1:3306/cadastro_pets";
    private String USER = "root";
    private String PASSWORD = "password";

    public List<String> getNomesGatos() {
        List<String> nomesGatos = new ArrayList<>();
        String query = "SELECT nome FROM gatos";

        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                nomesGatos.add(resultSet.getString("nome"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            addGatoController.getInstance().exibirAlerta("Erro", "Erro ao obter os nomes dos gatos do banco de dados.");
        }

        return nomesGatos;
    }
    public int obterIdDoGatoPeloNome(String nomeGato) {
        int idGato = -1;
        String query = "SELECT id_gato FROM gatos WHERE nome = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, nomeGato);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    idGato = resultSet.getInt("id_gato");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            addGatoController.getInstance().exibirAlerta("Erro", "Erro ao obter o ID do gato do banco de dados.");
        }

        return idGato;
    }

    public Gato obterDetalhesGato(int idGato) {
        Gato gato = null;
        String query = "SELECT * FROM gatos WHERE id_gato = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, idGato);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String nome = resultSet.getString("nome");
                    int idade = resultSet.getInt("idade");
                    String cor = resultSet.getString("cor");
                    String pelagem = resultSet.getString("pelagem");
                    int idDono = resultSet.getInt("id_dono");
                    byte[] imagem = resultSet.getBytes("imagem");

                    gato = new Gato(nome, idade, cor, pelagem, idDono, imagem, idGato);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            addGatoController.getInstance().exibirAlerta("Erro", "Erro ao obter detalhes do gato do banco de dados.");
        }

        return gato;
    }

    public String getNomeDono(int idDono) {
        String nomeDono = null;
        String query = "SELECT nome FROM usuarios WHERE id_usuario = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, idDono);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    nomeDono = resultSet.getString("nome");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            addGatoController.getInstance().exibirAlerta("Erro", "Erro ao obter o nome do dono do banco de dados.");
        }

        return nomeDono;
    }

    public void atualizarGato(Gato gato) {
        String query;

        if (gato.getImagem() != null && gato.getImagem().length > 0) {
            query = "UPDATE gatos SET idade = ?, cor = ?, pelagem = ?, id_dono = ?, imagem = ?, nome = ? WHERE id_gato = ?";
        } else {
            query = "UPDATE gatos SET idade = ?, cor = ?, pelagem = ?, id_dono = ?, nome = ? WHERE id_gato = ?";
        }

        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, gato.getIdade());
            preparedStatement.setString(2, gato.getCor());
            preparedStatement.setString(3, gato.getPelagem());
            preparedStatement.setInt(4, gato.getIdDono());

            if (gato.getImagem() != null && gato.getImagem().length > 0) {
                preparedStatement.setBytes(5, gato.getImagem());
                preparedStatement.setString(6, gato.getNome());
                preparedStatement.setInt(7, gato.getIdGato());
            } else {
                preparedStatement.setString(5, gato.getNome());
                preparedStatement.setInt(6, gato.getIdGato());
            }

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            addGatoController.getInstance().exibirAlerta("Erro", "Erro ao atualizar gato no banco de dados.");
        }
    }



// ...


    public byte[] obterImagemDoBancoDeDados(String nomeGato) {
        String query = "SELECT imagem FROM gatos WHERE nome = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, nomeGato);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getBytes("imagem");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            addGatoController.getInstance().exibirAlerta("Erro", "Erro ao obter imagem do banco de dados.");
        }

        return new byte[0];
    }

    public void atualizarImagemNoBancoDeDados(byte[] imagem, String nomeGato) {
        String query = "UPDATE gatos SET imagem = ? WHERE nome = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setBytes(1, imagem);
            preparedStatement.setString(2, nomeGato);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            addGatoController.getInstance().exibirAlerta("Erro", "Erro ao atualizar imagem no banco de dados.");
        }
    }


    public int getIdDonoSelecionado(String nomeDonoSelecionado) {
        int idDono = -1;

        String query = "SELECT id_usuario FROM usuarios WHERE nome = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, nomeDonoSelecionado);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                idDono = resultSet.getInt("id_usuario");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            addGatoController.getInstance().exibirAlerta("Erro", "Erro ao obter o ID do dono do banco de dados.");
        }

        return idDono;
    }

   public List<String> getNomesUsuarios() {
        List<String> nomes = new ArrayList<>();

        String query = "SELECT nome FROM usuarios";


        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                nomes.add(resultSet.getString("nome"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            addGatoController.getInstance().exibirAlerta("Erro", "Erro ao obter os nomes dos usuários do banco de dados.");
        }

        return nomes;
    }



    public int getIdDonoSelecionado() {
        String nomeDonoSelecionado = donoComboBox.getValue();
        int idDono = -1;


        String query = "SELECT id_usuario FROM usuarios WHERE nome = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, nomeDonoSelecionado);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                idDono = resultSet.getInt("id_usuario");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            addGatoController.getInstance().exibirAlerta("Erro", "Erro ao obter o ID do dono do banco de dados.");
        }

        return idDono;
    }
    public void adicionarGatoNoBanco(Gato gato) {


        String query = "INSERT INTO gatos (nome, idade, cor, pelagem, id_dono, imagem) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, gato.getNome());
            preparedStatement.setInt(2, gato.getIdade());
            preparedStatement.setString(3, gato.getCor());
            preparedStatement.setString(4, gato.getPelagem());
            preparedStatement.setInt(5, gato.getIdDono());

            preparedStatement.setBytes(6, gato.getImagem());

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            addGatoController.getInstance().exibirAlerta("Erro", "Erro ao adicionar o gato ao banco de dados.");
        }
    }




    public int obterProximoIdGato() {
        int proximoId = -1;

        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {

            ResultSet resultSet = statement.executeQuery("SELECT MAX(id_gato) + 1 AS proximo_id FROM gatos");

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

}