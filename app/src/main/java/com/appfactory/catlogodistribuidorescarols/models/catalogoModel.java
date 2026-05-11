package com.appfactory.catlogodistribuidorescarols.models;

public class catalogoModel {
    private String foto;
    private String precio1;
    private String precio2;
    private String clasificacion;
    private String codigo;
    private String categoria;
    private String seo;


    public catalogoModel() {
    }

    public catalogoModel(String categoria, String codigo, String foto, String precio1, String precio2, String clasificacion, String seo) {
        this.foto = foto;
        this.precio1 = precio1;
        this.precio2 = precio2;
        this.clasificacion = clasificacion;
        this.codigo = codigo;
        this.categoria = categoria;
        this.seo = seo;


    }
    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getCodigo() {
        return codigo;
    }
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }


    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getPrecio1() {
        return precio1;
    }

    public void setPrecio1(String precio1) {
        this.precio1 = precio1;
    }


    public String getClasificacion() {
        return clasificacion;
    }

    public void setClasificacion(String clasificacion) {
        this.clasificacion = clasificacion;
    }

    public String getSeo() {
        return seo;
    }

    public void setSeo(String seo) {
        this.seo = seo;
    }

    public String getPrecio2() {
        return precio2;
    }

    public void setPrecio2(String precio2) {
        this.precio2 = precio2;
    }
}
