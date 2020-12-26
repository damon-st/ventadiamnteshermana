package com.damon.ventadiamante.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Venta  implements Serializable {

    String vendedorName;
    String vendedorUID;
    String fechaVenta;
    String  colorValorPorVenta;
    String  colorVendedor;
    String descripcionDiamantes;
    double precioDiamante;
    String descripcion;
    long numeroVenta;
    String idVentaRef;
    List<String> image;

    public Venta() {
    }

    public Venta(String vendedorName, String vendedorUID, String fechaVenta, String colorValorPorVenta, String colorVendedor, String descripcionDiamantes, double precioDiamante, String descripcion, long numeroVenta, String idVentaRef, List<String> image) {
        this.vendedorName = vendedorName;
        this.vendedorUID = vendedorUID;
        this.fechaVenta = fechaVenta;
        this.colorValorPorVenta = colorValorPorVenta;
        this.colorVendedor = colorVendedor;
        this.descripcionDiamantes = descripcionDiamantes;
        this.precioDiamante = precioDiamante;
        this.descripcion = descripcion;
        this.numeroVenta = numeroVenta;
        this.idVentaRef = idVentaRef;
        this.image = image;
    }

    public List<String> getImage() {
        return image;
    }

    public void setImage(List<String> image) {
        this.image = image;
    }

    public String getVendedorName() {
        return vendedorName;
    }

    public void setVendedorName(String vendedorName) {
        this.vendedorName = vendedorName;
    }

    public String getVendedorUID() {
        return vendedorUID;
    }

    public void setVendedorUID(String vendedorUID) {
        this.vendedorUID = vendedorUID;
    }

    public String getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(String fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public String getColorValorPorVenta() {
        return colorValorPorVenta;
    }

    public void setColorValorPorVenta(String colorValorPorVenta) {
        this.colorValorPorVenta = colorValorPorVenta;
    }

    public String getColorVendedor() {
        return colorVendedor;
    }

    public void setColorVendedor(String colorVendedor) {
        this.colorVendedor = colorVendedor;
    }

    public String getDescripcionDiamantes() {
        return descripcionDiamantes;
    }

    public void setDescripcionDiamantes(String descripcionDiamantes) {
        this.descripcionDiamantes = descripcionDiamantes;
    }

    public double getPrecioDiamante() {
        return precioDiamante;
    }

    public void setPrecioDiamante(double precioDiamante) {
        this.precioDiamante = precioDiamante;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public long getNumeroVenta() {
        return numeroVenta;
    }

    public void setNumeroVenta(long numeroVenta) {
        this.numeroVenta = numeroVenta;
    }

    public String getIdVentaRef() {
        return idVentaRef;
    }

    public void setIdVentaRef(String idVentaRef) {
        this.idVentaRef = idVentaRef;
    }


}