package com.dronemed.modelo;

import java.time.LocalDateTime;

public class DronConcreto extends Dron {
    
    // Constructor por defecto
    public DronConcreto() {
        super();
    }
    
    // Constructor completo
    public DronConcreto(String id, String modelo, String tipoCarga, double capacidadKg, 
                       double autonomiaKm, double velocidadMaxKmh) {
        super(id, modelo, tipoCarga, capacidadKg, autonomiaKm, velocidadMaxKmh);
    }
    
    // Constructor para placeholder
    public DronConcreto(int id, String nombre, String modelo, String tipoCarga, 
                       double capacidadKg, double autonomiaKm, double velocidadMaxKmh, 
                       String estado, boolean activo) {
        super();
        this.setId(String.valueOf(id));
        this.setModelo(modelo);
        this.setTipoCarga(tipoCarga);
        this.setCapacidadKg(capacidadKg);
        this.setAutonomiaKm(autonomiaKm);
        this.setVelocidadMaxKmh(velocidadMaxKmh);
        this.setEstado(estado);
    }
    
    @Override
    public int calcularTiempoEntrega(double distanciaKm) {
        // Cálculo simple: tiempo = distancia / velocidad (en minutos)
        if (getVelocidadMaxKmh() <= 0) {
            return 0;
        }
        double tiempoHoras = distanciaKm / getVelocidadMaxKmh();
        return (int) Math.ceil(tiempoHoras * 60); // Convertir a minutos y redondear hacia arriba
    }
    
    // Método para obtener el nombre del dron
    public String getNombre() {
        return getModelo();
    }
    
    @Override
    public String toString() {
        if ("0".equals(getId())) {
            return getNombre(); // Para el placeholder
        }
        return String.format("DRN%03d - %s", Integer.parseInt(getId()), getModelo());
    }
}