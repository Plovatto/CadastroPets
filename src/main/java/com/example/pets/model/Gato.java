package com.example.pets.model;

public class Gato {
    private String nome;
    private int idade;
    private String cor;
    private String pelagem;
    private int idDono;
    private byte[] imagem;
private int idGato;

    public Gato( String nome, int idade, String cor, String pelagem, int idDono, byte[] imagem, int idGato) {
this.idGato = idGato;
        this.nome = nome;
        this.idade = idade;
        this.cor = cor;
        this.pelagem = pelagem;
        this.idDono = idDono;
        this.imagem = imagem;
    }

    public String getNome() {
        return nome;
    }

    public int getIdGato() {
        return idGato;
    }

    public void setIdGato(int idGato) {
        this.idGato = idGato;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getIdade() {
        return idade;
    }

    public void setIdade(int idade) {
        this.idade = idade;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public String getPelagem() {
        return pelagem;
    }

    public void setPelagem(String pelagem) {
        this.pelagem = pelagem;
    }

    public int getIdDono() {
        return idDono;
    }

    public void setIdDono(int idDono) {
        this.idDono = idDono;
    }

    public byte[] getImagem() {
        return imagem;
    }

    public void setImagem(byte[] imagem) {
        this.imagem = imagem;
    }
}