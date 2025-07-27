package com.dronemed.controlador;

import com.dronemed.database.DAO.DronDAO;
import com.dronemed.modelo.Dron;
import java.util.List;
import java.util.logging.Logger;

public class ControladorDron {
    private static final Logger LOGGER = Logger.getLogger(ControladorDron.class.getName());
    private DronDAO dronDAO;
    
    public ControladorDron() {
        this.dronDAO = new DronDAO();
    }
    
    // Crear un nuevo dron
    public boolean crearDron(Dron dron) {
        if (dron == null) {
            LOGGER.warning("Intento de crear dron nulo");
            return false;
        }
        
        // Validar datos del dron
        if (!validarDron(dron)) {
            LOGGER.warning("Datos del dron no válidos: " + dron.getId());
            return false;
        }
        
        boolean resultado = dronDAO.crear(dron);
        if (resultado) {
            LOGGER.info("Dron creado exitosamente: " + dron.getId());
        }
        return resultado;
    }
    
    // Obtener un dron por ID
    public Dron obtenerDron(String id) {
        if (id == null || id.trim().isEmpty()) {
            LOGGER.warning("ID de dron no válido");
            return null;
        }
        
        return dronDAO.leerPorId(id);
    }
    
    // Obtener todos los drones
    public List<Dron> obtenerTodosLosDrones() {
        return dronDAO.leerTodos();
    }
    
    // Actualizar un dron
    public boolean actualizarDron(Dron dron) {
        if (dron == null) {
            LOGGER.warning("Intento de actualizar dron nulo");
            return false;
        }
        
        if (!validarDron(dron)) {
            LOGGER.warning("Datos del dron no válidos para actualización: " + dron.getId());
            return false;
        }
        
        boolean resultado = dronDAO.actualizar(dron);
        if (resultado) {
            LOGGER.info("Dron actualizado exitosamente: " + dron.getId());
        }
        return resultado;
    }
    
    // Eliminar un dron
    public boolean eliminarDron(String id) {
        if (id == null || id.trim().isEmpty()) {
            LOGGER.warning("ID de dron no válido para eliminación");
            return false;
        }
        
        boolean resultado = dronDAO.eliminar(id);
        if (resultado) {
            LOGGER.info("Dron eliminado exitosamente: " + id);
        }
        return resultado;
    }
    
    // Obtener drones disponibles
    public List<Dron> obtenerDronesDisponibles() {
        return dronDAO.buscarPorEstado("DISPONIBLE");
    }
    
    // Obtener drones en mantenimiento
    public List<Dron> obtenerDronesEnMantenimiento() {
        return dronDAO.buscarPorEstado("MANTENIMIENTO");
    }
    
    // Obtener estadísticas
    public java.util.Map<String, Integer> obtenerEstadisticas() {
        return dronDAO.obtenerEstadisticas();
    }
    
    // Validar datos del dron
    private boolean validarDron(Dron dron) {
        if (dron.getId() == null || dron.getId().trim().isEmpty()) {
            return false;
        }
        
        if (dron.getModelo() == null || dron.getModelo().trim().isEmpty()) {
            return false;
        }
        
        if (dron.getCapacidadKg() <= 0) {
            return false;
        }
        
        if (dron.getAutonomiaKm() <= 0) {
            return false;
        }
        
        if (dron.getVelocidadMaxKmh() <= 0) {
            return false;
        }
        
        return true;
    }
    
    // Cambiar estado de un dron
    public boolean cambiarEstadoDron(String id, String nuevoEstado) {
        Dron dron = obtenerDron(id);
        if (dron == null) {
            LOGGER.warning("No se encontró dron con ID: " + id);
            return false;
        }
        
        String estadoAnterior = dron.getEstado();
        dron.setEstado(nuevoEstado);
        
        boolean resultado = actualizarDron(dron);
        if (resultado) {
            LOGGER.info("Estado del dron " + id + " cambiado de " + estadoAnterior + " a " + nuevoEstado);
        }
        
        return resultado;
    }    
}
