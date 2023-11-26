package com.example.pets.model;

public class Cachorro {

    private String nome;
    private int idade;
    private String raca;
    private String cor;
    private int idDono;
    private byte[] imagem;

    private int idCachorro;
    public Cachorro(String nome, int idade, String raca, String cor, int idDono, byte[] imagem, int idCachorro) {
        this.nome = nome;
        this.idade = idade;
        this.raca = raca;
        this.cor = cor;
        this.idDono = idDono;
        this.imagem = imagem;
        this.idCachorro = idCachorro;

    }

    public int getIdCachorro() {
        return idCachorro;
    }

    public void setIdCachorro(int idCachorro) {
        this.idCachorro = idCachorro;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setIdade(int idade) {
        this.idade = idade;
    }

    public void setRaca(String raca) {
        this.raca = raca;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public void setIdDono(int idDono) {
        this.idDono = idDono;
    }

    public void setImagem(byte[] imagem) {
        this.imagem = imagem;
    }

    public String getNome() {
        return nome;
    }

    public int getIdade() {
        return idade;
    }

    public String getRaca() {
        return raca;
    }

    public String getCor() {
        return cor;
    }
    public byte[] getImagem() {
        return imagem;
    }
    public int getIdDono() {
        return idDono;
    }
}
