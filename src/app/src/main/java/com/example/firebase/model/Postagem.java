package com.example.firebase.model;

import java.util.Date;

public class Postagem {

    private String idUser;
    private String idPost;
    private String nome;
    private String imagem;
    private String postagem;
    private String data;

    public Postagem() {
    }

    public Postagem(String idUser, String idPost, String nome, String imagem, String postagem, String data) {
        this.idUser = idUser;
        this.idPost = idPost;
        this.nome = nome;
        this.imagem = imagem;
        this.postagem = postagem;
        this.data = data;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getIdPost() {
        return idPost;
    }

    public void setIdPost(String idPost) {
        this.idPost = idPost;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public String getPostagem() {
        return postagem;
    }

    public void setPostagem(String postagem) {
        this.postagem = postagem;
    }

    public String getId() {
        return idUser;
    }

    public void setId(String id) {
        this.idUser = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Postagem{" +
                "idUser='" + idUser + '\'' +
                ", idPost='" + idPost + '\'' +
                ", nome='" + nome + '\'' +
                ", imagem='" + imagem + '\'' +
                ", postagem='" + postagem + '\'' +
                ", data='" + data + '\'' +
                '}';
    }
}

