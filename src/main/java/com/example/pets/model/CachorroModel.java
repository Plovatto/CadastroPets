package com.example.pets.model;

import javafx.scene.control.Alert;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CachorroModel {
    private static CachorroModel instance;

    public static CachorroModel getInstance() {
        if (instance == null) {
            instance = new CachorroModel();
        }
        return instance;
    }

    public  String DB_URL = "jdbc:mysql://127.0.0.1:3306/cadastro_pets";
    public  String USER = "root";
    public String PASSWORD = "password";

    public List<String> getNomesCachorros() {
        List<String> nomesCachorros = new ArrayList<>();
        String query = "SELECT nome FROM cachorros";

        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                nomesCachorros.add(resultSet.getString("nome"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            exibirAlerta("Erro", "Erro ao obter os nomes dos cachorros do banco de dados.");
        }

        return nomesCachorros;
    }

    public int obterIdDoCachorroPeloNome(String nomeCachorro) {
        int idCachorro = -1;
        String query = "SELECT id_cachorro FROM cachorros WHERE nome = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, nomeCachorro);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    idCachorro = resultSet.getInt("id_cachorro");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            exibirAlerta("Erro", "Erro ao obter o ID do cachorro do banco de dados.");
        }

        return idCachorro;
    }

    public Cachorro obterDetalhesCachorro(int idCachorro) {
        Cachorro cachorro = null;
        String query = "SELECT * FROM cachorros WHERE id_cachorro = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, idCachorro);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String nome = resultSet.getString("nome");
                    int idade = resultSet.getInt("idade");
                    String cor = resultSet.getString("cor");
                    String raca = resultSet.getString("raca");
                    int idDono = resultSet.getInt("id_dono");
                    byte[] imagem = resultSet.getBytes("imagem");

                    cachorro = new Cachorro(nome, idade, cor, raca, idDono, imagem, idCachorro);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            exibirAlerta("Erro", "Erro ao obter detalhes do cachorro do banco de dados.");
        }

        return cachorro;
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
            exibirAlerta("Erro", "Erro ao obter o nome do dono do banco de dados.");
        }

        return nomeDono;
    }

    public void atualizarCachorro(Cachorro cachorro) {
        String query;

        if (cachorro.getImagem() != null && cachorro.getImagem().length > 0) {
            query = "UPDATE cachorros SET idade = ?, cor = ?, raca = ?, id_dono = ?, imagem = ?, nome = ? WHERE id_cachorro = ?";
        } else {
            query = "UPDATE cachorros SET idade = ?, cor = ?, raca = ?, id_dono = ?, nome = ? WHERE id_cachorro = ?";
        }

        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, cachorro.getIdade());
            preparedStatement.setString(2, cachorro.getCor());
            preparedStatement.setString(3, cachorro.getRaca());
            preparedStatement.setInt(4, cachorro.getIdDono());

            if (cachorro.getImagem() != null && cachorro.getImagem().length > 0) {
                preparedStatement.setBytes(5, cachorro.getImagem());
                preparedStatement.setString(6, cachorro.getNome());
                preparedStatement.setInt(7, cachorro.getIdCachorro());
            } else {
                preparedStatement.setString(5, cachorro.getNome());
                preparedStatement.setInt(6, cachorro.getIdCachorro());
            }

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            exibirAlerta("Erro", "Erro ao atualizar cachorro no banco de dados.");
        }
    }

    private byte[] obterImagemDoBancoDeDados(String nomeCachorro) {
        String query = "SELECT imagem FROM cachorros WHERE nome = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, nomeCachorro);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getBytes("imagem");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            exibirAlerta("Erro", "Erro ao obter imagem do banco de dados.");
        }

        return new byte[0];
    }

    private void atualizarImagemNoBancoDeDados(byte[] imagem, String nomeCachorro) {
        String query = "UPDATE cachorros SET imagem = ? WHERE nome = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setBytes(1, imagem);
            preparedStatement.setString(2, nomeCachorro);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            exibirAlerta("Erro", "Erro ao atualizar imagem no banco de dados.");
        }
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


    private void exibirAlerta(String titulo, String conteudo) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setContentText(conteudo);
        alert.showAndWait();
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


        String query = "INSERT INTO cachorros (nome, idade, raca, cor, id_dono, imagem) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, cachorro.getNome());
            preparedStatement.setInt(2, cachorro.getIdade());
            preparedStatement.setString(3, cachorro.getRaca());
            preparedStatement.setString(4, cachorro.getCor());
            preparedStatement.setInt(5, cachorro.getIdDono());

            // Configurar o parâmetro de imagem
            preparedStatement.setBytes(6, cachorro.getImagem());

            preparedStatement.executeUpdate();

            exibirAlerta("Sucesso", "Cachorro adicionado com sucesso!");

        } catch (SQLException e) {
            e.printStackTrace();
            exibirAlerta("Erro", "Erro ao adicionar o cachorro ao banco de dados.");
        }
    }

}
