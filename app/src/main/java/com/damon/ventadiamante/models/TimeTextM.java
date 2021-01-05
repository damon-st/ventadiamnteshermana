package com.damon.ventadiamante.models;

public class TimeTextM {

    private String time;
    private long fecha;

    public TimeTextM() {
    }


    public TimeTextM(String time, long fecha) {
        this.time = time;
        this.fecha = fecha;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public long getFecha() {
        return fecha;
    }

    public void setFecha(long fecha) {
        this.fecha = fecha;
    }
}
