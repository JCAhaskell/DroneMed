package com.dronemed.vista;

import java.awt.Color;

public class UbicacionCliente {
    public String nombre;
    public int x;
    public int y;
    public Color color;
    
    public UbicacionCliente(String nombre, int x, int y, Color color) {
        this.nombre = nombre;
        this.x = x;
        this.y = y;
        this.color = color;
    }
}