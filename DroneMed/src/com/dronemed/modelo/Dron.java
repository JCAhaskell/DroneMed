package com.dronemed.modelo;

import com.dronemed.interfaces.Alertable;
import com.dronemed.interfaces.Mantenible;
import java.time.LocalDateTime;
import java.util.Objects;

public abstract class Dron implements Alertable, Mantenible {
    
    private String id;
    private String modelo;
    private String tipoCarga;
    private double capacidadKg;
    private double autonomiaKm;
    private double velocidadMaxKmh;
    private String estado; // DISPONIBLE, EN_VUELO, MANTENIMIENTO, FUERA_SERVICIO
    private String ubicacionActual;
    private double horasVuelo;
    private LocalDateTime ultimoMantenimiento;
    private int nivelBateria; // Nivel de batería en porcentaje (0-100)
    
    // Constructor por defecto
    public Dron() {
        this.estado = "DISPONIBLE";
        this.horasVuelo = 0.0;
        this.nivelBateria = 100; // Batería completa por defecto
    }
    
    // Constructor completo
    public Dron(String id, String modelo, String tipoCarga, double capacidadKg, 
                double autonomiaKm, double velocidadMaxKmh) {
        this();
        this.id = id;
        this.modelo = modelo;
        this.tipoCarga = tipoCarga;
        this.capacidadKg = capacidadKg;
        this.autonomiaKm = autonomiaKm;
        this.velocidadMaxKmh = velocidadMaxKmh;
    }
    
    // Método abstracto para polimorfismo
    public abstract int calcularTiempoEntrega(double distanciaKm);
    
    // Implementación de Alertable
    @Override
    public void enviarAlerta(String mensaje) {
        System.out.println("[ALERTA DRON " + id + "] " + mensaje);
    }
    
    // Implementación de Mantenible
    @Override
    public void programarMantenimiento(LocalDateTime fecha, String tipo) {
        System.out.println("Mantenimiento " + tipo + " programado para dron " + id + " el " + fecha);
    }
    
    @Override
    public boolean requiereMantenimiento() {
        return horasVuelo > 100 || 
               (ultimoMantenimiento != null && 
                getDiasDesdeUltimoMantenimiento() > 30);
    }
    
    @Override
    public LocalDateTime getUltimoMantenimiento() {
        return ultimoMantenimiento;
    }
    
    // Implementación del método de la interfaz Mantenible
    @Override
    public long getDiasDesdeUltimoMantenimiento() {
        if (ultimoMantenimiento == null) return Long.MAX_VALUE;
        return java.time.temporal.ChronoUnit.DAYS.between(ultimoMantenimiento, LocalDateTime.now());
    }
    
    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    
    public String getTipoCarga() { return tipoCarga; }
    public void setTipoCarga(String tipoCarga) { this.tipoCarga = tipoCarga; }
    
    public double getCapacidadKg() { return capacidadKg; }
    public void setCapacidadKg(double capacidadKg) { this.capacidadKg = capacidadKg; }
    
    public double getAutonomiaKm() { return autonomiaKm; }
    public void setAutonomiaKm(double autonomiaKm) { this.autonomiaKm = autonomiaKm; }
    
    public double getVelocidadMaxKmh() { return velocidadMaxKmh; }
    public void setVelocidadMaxKmh(double velocidadMaxKmh) { this.velocidadMaxKmh = velocidadMaxKmh; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    
    public String getUbicacionActual() { return ubicacionActual; }
    public void setUbicacionActual(String ubicacionActual) { this.ubicacionActual = ubicacionActual; }
    
    public double getHorasVuelo() { return horasVuelo; }
    public void setHorasVuelo(double horasVuelo) { this.horasVuelo = horasVuelo; }
    
    public void setUltimoMantenimiento(LocalDateTime ultimoMantenimiento) {
        this.ultimoMantenimiento = ultimoMantenimiento;
    }
    
    public int getNivelBateria() { return nivelBateria; }
    public void setNivelBateria(int nivelBateria) { 
        this.nivelBateria = Math.max(0, Math.min(100, nivelBateria)); // Asegurar rango 0-100
    }
    
    // Método para verificar si el dron está disponible
    public boolean estaDisponible() {
        return "DISPONIBLE".equals(estado);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dron dron = (Dron) o;
        return Objects.equals(id, dron.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("Dron{id='%s', modelo='%s', tipo='%s', capacidad=%.1fkg, estado='%s'}",
                id, modelo, tipoCarga, capacidadKg, estado);
    }
}