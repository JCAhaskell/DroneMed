package com.dronemed.modelo;

public class DronCargaPesada extends Dron {
    
    public DronCargaPesada() {
        super();
    }
    
    public DronCargaPesada(String id, String modelo, double capacidadKg, double autonomiaKm, double velocidadMaxKmh) {
        super(id, modelo, "PESADA", capacidadKg, autonomiaKm, velocidadMaxKmh);
    }
    
    @Override
    public int calcularTiempoEntrega(double distanciaKm) {
        //drones de carga pesada son mas lentos pero mas estables
        double velocidadPromedio = getVelocidadMaxKmh() * 0.7; //70% de velocidad maximo
        double tiempoHoras = distanciaKm / velocidadPromedio;
        int tiempoMinutos = (int) Math.ceil(tiempoHoras * 60);
        
        //tiempo adicional por peso
        tiempoMinutos += 15; //15 minutos extra por carga pesada
        
        return tiempoMinutos;
    }
    
    public boolean puedeCargar(double pesoKg) {
        return pesoKg <= getCapacidadKg() && pesoKg >= 5.0; //minimo 5kg para carga pesada 
    }
    
}
