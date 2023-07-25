package com.example.firebase.model;

public class Comentario {
    String idUser;
    String idPost;
    String idComentario;
    String comentario;

    public Comentario(String idUser, String idPost, String idComentario, String comentario) {
        this.idUser = idUser;
        this.idPost = idPost;
        this.idComentario = idComentario;
        this.comentario = comentario;
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

    public String getIdComentario() {
        return idComentario;
    }

    public void setIdComentario(String idComentario) {
        this.idComentario = idComentario;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    @Override
    public String toString() {
        return "Comentario{" +
                "idUser='" + idUser + '\'' +
                ", idPost='" + idPost + '\'' +
                ", idComentario='" + idComentario + '\'' +
                ", comentario='" + comentario + '\'' +
                '}';
    }
}
