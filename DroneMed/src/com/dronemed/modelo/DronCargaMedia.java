package com.dronemed.modelo;

public class DronCargaMedia  extends Dron {
    
    public DronCargaMedia() {
        super();
    }
    
    public DronCargaMedia(String id, String modelo, double capacidadKg, double autonomiaKm, double velocidadMaxKmh) {
        super(id, modelo, "MEDIA", capacidadKg, autonomiaKm, velocidadMaxKmh);
    }
    
    @Override
    public int calcularTiempoEntrega(double distanciaKm) {
        //drones de carga media balancean velocidad y estabilidad
        double velocidadPromedio = getVelocidadMaxKmh() * 0.8; //80% de velocidad
        double tiempoHoras = distanciaKm / velocidadPromedio;
        int tiempoMinutos = (int) Math.ceil(tiempoHoras * 60);
        
        //tiempo adicional por carga media
        tiempoMinutos += 10; //10 minutos extra
        
        return tiempoMinutos;
    }
    
    public boolean esVersatil() {
        return getCapacidadKg() >= 3.0 && getCapacidadKg() <= 8.0;
    }
    
}
