package com.dronemed.vista;

import java.awt.Color;

public class DronEnMapa {
    public int id;
    public String modelo;
    public int x;
    public int y;
    public Color color;
    
    public DronEnMapa(int id, String modelo, int x, int y, Color color) {
        this.id = id;
        this.modelo = modelo;
        this.x = x;
        this.y = y;
        this.color = color;
    }
}