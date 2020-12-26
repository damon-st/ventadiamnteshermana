package com.damon.ventadiamante.models;

public class Diamante {

    String diamantes;
    double valor;
    int path;
    String colorPrice;

    public Diamante(String diamantes, double valor, int path, String colorPrice) {
        this.diamantes = diamantes;
        this.valor = valor;
        this.path = path;
        this.colorPrice = colorPrice;
    }

    public String getColorPrice() {
        return colorPrice;
    }

    public void setColorPrice(String colorPrice) {
        this.colorPrice = colorPrice;
    }

    public String getDiamantes() {
        return diamantes;
    }

    public void setDiamantes(String diamantes) {
        this.diamantes = diamantes;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public int getPath() {
        return path;
    }

    public void setPath(int path) {
        this.path = path;
    }
}
