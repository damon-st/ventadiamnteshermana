package com.damon.ventadiamante.models;

public class VentaPrincipal {

    private Venta venta;
    private TimeTextM timeTextM;
    private int viewType;


    public VentaPrincipal(Venta venta){
        this.venta = venta;
        viewType = 2;
    }

    public VentaPrincipal(TimeTextM timeTextM) {
        this.timeTextM = timeTextM;
        viewType = 1;
    }

    public Venta getVenta() {
        return venta;
    }

    public void setVenta(Venta venta) {
        this.venta = venta;
    }

    public TimeTextM getTimeTextM() {
        return timeTextM;
    }

    public void setTimeTextM(TimeTextM timeTextM) {
        this.timeTextM = timeTextM;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }
}
