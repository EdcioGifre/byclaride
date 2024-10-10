package com.example.byclarider.modelo;

public class ReportesModelo {
    String categoria, comentario;

    public ReportesModelo() {

    }

    public ReportesModelo(String categoria, String comentario) {
        this.categoria = categoria;
        this.comentario = comentario;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}
