package com.dronemed.modelo;

public class DronCargaLigera extends Dron {
    
    public DronCargaLigera() {
        super();
    }
    
    public DronCargaLigera(String id, String modelo, double capacidadKg, double autonomiaKm, double velocidadMaxKmh) {
        super(id, modelo, "LIGERA", capacidadKg, autonomiaKm, velocidadMaxKmh);
    }
    
    @Override
    public int calcularTiempoEntrega(double distanciaKm) {
        //drones de carga ligera mas rapidos
        double velocidadPromedio = getVelocidadMaxKmh() * 0.9; //90% de velocidad maxima
        double tiempoHoras = distanciaKm / velocidadPromedio;
        int tiempoMinutos = (int) Math.ceil(tiempoHoras * 60);
        
        //tiempo minimo de 5 minutos
        return Math.max(tiempoMinutos, 5);
    }
    
    public boolean esRapido() {
        return getVelocidadMaxKmh() > 70;
    }
}
