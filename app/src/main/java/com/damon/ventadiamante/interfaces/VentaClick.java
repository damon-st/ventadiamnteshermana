package com.damon.ventadiamante.interfaces;


import com.damon.ventadiamante.models.Venta;

public interface VentaClick {

    void onCLickDiamante(Venta venta, int position);
    void onLongClickDiamante(Venta venta,int position);
}
