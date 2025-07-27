package com.dronemed.controlador;

import com.dronemed.database.DAO.MantenimientoDAO;
import com.dronemed.database.DAO.DronDAO;
import com.dronemed.modelo.Mantenimiento;
import com.dronemed.modelo.Dron;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ControladorMantenimiento {
    private static final Logger LOGGER = Logger.getLogger(ControladorMantenimiento.class.getName());
    private MantenimientoDAO mantenimientoDAO;
    private DronDAO dronDAO;
    
    public ControladorMantenimiento() {
        this.mantenimientoDAO = new MantenimientoDAO();
        this.dronDAO = new DronDAO();
    }
    
    // Programar mantenimiento
    public boolean programarMantenimiento(String dronId, String tipo, String descripcion, 
                                         LocalDateTime fechaProgramada, String tecnico, double costo) {
        try {
            Mantenimiento mantenimiento = new Mantenimiento();
            mantenimiento.setDronId(dronId);
            mantenimiento.setTipoMantenimiento(tipo);
            mantenimiento.setDescripcion(descripcion);
            mantenimiento.setFechaInicio(fechaProgramada);
            mantenimiento.setTecnicoResponsable(tecnico);
            mantenimiento.setCosto(costo);
            mantenimiento.setEstado("PROGRAMADO");
            mantenimiento.setObservaciones("");
            
            boolean resultado = mantenimientoDAO.crear(mantenimiento);
            
            if (resultado) {
                LOGGER.info("Mantenimiento programado exitosamente para dron ID: " + dronId);
                // Actualizar estado del dron si es necesario
                actualizarEstadoDron(dronId);
            }
            
            return resultado;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al programar mantenimiento", e);
            return false;
        }
    }
    
    // Iniciar mantenimiento
    public boolean iniciarMantenimiento(int mantenimientoId) {
        try {
            Mantenimiento mantenimiento = mantenimientoDAO.leerPorId(mantenimientoId);
            if (mantenimiento != null && "PROGRAMADO".equals(mantenimiento.getEstado())) {
                mantenimiento.setEstado("EN_PROGRESO");
                mantenimiento.setFechaInicio(LocalDateTime.now());
                
                boolean resultado = mantenimientoDAO.actualizar(mantenimiento);
                
                if (resultado) {
                    LOGGER.info("Mantenimiento iniciado: ID " + mantenimientoId);
                    // Actualizar estado del dron
                    actualizarEstadoDron(mantenimiento.getDronId());
                }
                
                return resultado;
            }
            return false;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al iniciar mantenimiento: " + mantenimientoId, e);
            return false;
        }
    }
    
    // Completar mantenimiento
    public boolean completarMantenimiento(int mantenimientoId, String observaciones, double costoFinal) {
        try {
            Mantenimiento mantenimiento = mantenimientoDAO.leerPorId(mantenimientoId);
            if (mantenimiento != null && "EN_PROGRESO".equals(mantenimiento.getEstado())) {
                mantenimiento.setEstado("COMPLETADO");
                mantenimiento.setFechaFin(LocalDateTime.now());
                mantenimiento.setObservaciones(observaciones);
                mantenimiento.setCosto(costoFinal);
                
                boolean resultado = mantenimientoDAO.actualizar(mantenimiento);
                
                if (resultado) {
                    LOGGER.info("Mantenimiento completado: ID " + mantenimientoId);
                    // Actualizar último mantenimiento del dron
                    actualizarUltimoMantenimientoDron(mantenimiento.getDronId());
                    // Actualizar estado del dron
                    actualizarEstadoDron(mantenimiento.getDronId());
                }
                
                return resultado;
            }
            return false;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al completar mantenimiento: " + mantenimientoId, e);
            return false;
        }
    }
    
    // Cancelar mantenimiento
    public boolean cancelarMantenimiento(int mantenimientoId, String motivo) {
        try {
            Mantenimiento mantenimiento = mantenimientoDAO.leerPorId(mantenimientoId);
            if (mantenimiento != null && !"COMPLETADO".equals(mantenimiento.getEstado())) {
                mantenimiento.setEstado("CANCELADO");
                mantenimiento.setObservaciones("Cancelado: " + motivo);
                
                boolean resultado = mantenimientoDAO.actualizar(mantenimiento);
                
                if (resultado) {
                    LOGGER.info("Mantenimiento cancelado: ID " + mantenimientoId);
                    // Actualizar estado del dron
                    actualizarEstadoDron(mantenimiento.getDronId());
                }
                
                return resultado;
            }
            return false;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al cancelar mantenimiento: " + mantenimientoId, e);
            return false;
        }
    }
    
    // Obtener todos los mantenimientos
    public List<Mantenimiento> obtenerTodosLosMantenimientos() {
        return mantenimientoDAO.leerTodos();
    }
    
    // Obtener mantenimientos por dron
    public List<Mantenimiento> obtenerMantenimientosPorDron(String dronId) {
        return mantenimientoDAO.leerPorDron(dronId);
    }
    
    // Obtener mantenimientos por estado
    public List<Mantenimiento> obtenerMantenimientosPorEstado(String estado) {
        return mantenimientoDAO.buscarPorEstado(estado);
    }
    
    // Obtener mantenimientos pendientes
    public List<Mantenimiento> obtenerMantenimientosPendientes() {
        return mantenimientoDAO.buscarPorEstado("PROGRAMADO");
    }
    
    // Obtener mantenimientos en progreso
    public List<Mantenimiento> obtenerMantenimientosEnProgreso() {
        return mantenimientoDAO.buscarPorEstado("EN_PROGRESO");
    }
    
    // Obtener drones que requieren mantenimiento
    public List<Dron> obtenerDronesQueRequierenMantenimiento() {
        List<Dron> todosDrones = dronDAO.leerTodos();
        return todosDrones.stream()
                .filter(Dron::requiereMantenimiento)
                .collect(Collectors.toList());
    }
    
    // Generar reporte de mantenimientos
    public String generarReporteMantenimientos(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        StringBuilder reporte = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        reporte.append("=== REPORTE DE MANTENIMIENTOS ===\n");
        reporte.append("Período: ").append(fechaInicio.format(formatter))
               .append(" - ").append(fechaFin.format(formatter)).append("\n\n");
        
        List<Mantenimiento> mantenimientos = mantenimientoDAO.leerTodos();
        
        // Filtrar por fechas
        List<Mantenimiento> mantenimientosFiltrados = mantenimientos.stream()
                .filter(m -> m.getFechaInicio() != null && 
                           !m.getFechaInicio().isBefore(fechaInicio) && 
                           !m.getFechaInicio().isAfter(fechaFin))
                .collect(Collectors.toList());
        
        // Estadísticas generales
        Map<String, Object> estadisticas = mantenimientoDAO.obtenerEstadisticas();
        reporte.append("ESTADÍSTICAS GENERALES:\n");
        reporte.append("Total de mantenimientos: ").append(estadisticas.get("total")).append("\n");
        reporte.append("Costo total: $").append(String.format("%.2f", estadisticas.get("costoTotal"))).append("\n\n");
        
        // Por estado
        @SuppressWarnings("unchecked")
        Map<String, Integer> porEstado = (Map<String, Integer>) estadisticas.get("porEstado");
        reporte.append("POR ESTADO:\n");
        porEstado.forEach((estado, cantidad) -> 
            reporte.append("- ").append(estado).append(": ").append(cantidad).append("\n"));
        reporte.append("\n");
        
        // Por tipo
        @SuppressWarnings("unchecked")
        Map<String, Integer> porTipo = (Map<String, Integer>) estadisticas.get("porTipo");
        reporte.append("POR TIPO:\n");
        porTipo.forEach((tipo, cantidad) -> 
            reporte.append("- ").append(tipo).append(": ").append(cantidad).append("\n"));
        reporte.append("\n");
        
        // Detalle de mantenimientos en el período
        reporte.append("DETALLE DE MANTENIMIENTOS EN EL PERÍODO:\n");
        for (Mantenimiento m : mantenimientosFiltrados) {
            reporte.append("ID: ").append(m.getId())
                   .append(" | Dron: ").append(m.getDronId())
                   .append(" | Tipo: ").append(m.getTipoMantenimiento())
                   .append(" | Estado: ").append(m.getEstado())
                   .append(" | Costo: $").append(String.format("%.2f", m.getCosto()))
                   .append("\n");
        }
        
        return reporte.toString();
    }
    
    // Generar reporte de drones
    public String generarReporteDrones() {
        StringBuilder reporte = new StringBuilder();
        
        reporte.append("=== REPORTE DE ESTADO DE DRONES ===\n\n");
        
        List<Dron> drones = dronDAO.leerTodos();
        
        // Estadísticas generales
        long dronesActivos = drones.stream().filter(d -> "ACTIVO".equals(d.getEstado())).count();
        long dronesMantenimiento = drones.stream().filter(d -> "MANTENIMIENTO".equals(d.getEstado())).count();
        long dronesInactivos = drones.stream().filter(d -> "INACTIVO".equals(d.getEstado())).count();
        
        reporte.append("ESTADÍSTICAS GENERALES:\n");
        reporte.append("Total de drones: ").append(drones.size()).append("\n");
        reporte.append("Drones activos: ").append(dronesActivos).append("\n");
        reporte.append("Drones en mantenimiento: ").append(dronesMantenimiento).append("\n");
        reporte.append("Drones inactivos: ").append(dronesInactivos).append("\n\n");
        
        // Drones que requieren mantenimiento
        List<Dron> dronesRequierenMantenimiento = obtenerDronesQueRequierenMantenimiento();
        reporte.append("DRONES QUE REQUIEREN MANTENIMIENTO (" + dronesRequierenMantenimiento.size() + "):\n");
        for (Dron dron : dronesRequierenMantenimiento) {
            reporte.append("- ID: ").append(dron.getId())
                   .append(" | Modelo: ").append(dron.getModelo())
                   .append(" | Días desde último mantenimiento: ").append(dron.getDiasDesdeUltimoMantenimiento())
                   .append("\n");
        }
        reporte.append("\n");
        
        // Detalle de todos los drones
        reporte.append("DETALLE DE TODOS LOS DRONES:\n");
        for (Dron dron : drones) {
            reporte.append("ID: ").append(dron.getId())
                   .append(" | Modelo: ").append(dron.getModelo())
                   .append(" | Estado: ").append(dron.getEstado())
                   .append(" | Batería: ").append(dron.getNivelBateria()).append("%")
                   .append(" | Último mantenimiento: ")
                   .append(dron.getUltimoMantenimiento() != null ? 
                           dron.getUltimoMantenimiento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A")
                   .append("\n");
        }
        
        return reporte.toString();
    }
    
    // Obtener estadísticas de mantenimiento
    public Map<String, Object> obtenerEstadisticas() {
        return mantenimientoDAO.obtenerEstadisticas();
    }
    
    // Métodos auxiliares
    private void actualizarEstadoDron(String dronId) {
        try {
            Dron dron = dronDAO.leerPorId(dronId);
            if (dron != null) {
                // Verificar si hay mantenimientos en progreso
                List<Mantenimiento> mantenimientosEnProgreso = 
                    mantenimientoDAO.leerPorDron(dronId).stream()
                        .filter(m -> "EN_PROGRESO".equals(m.getEstado()))
                        .collect(Collectors.toList());
                
                if (!mantenimientosEnProgreso.isEmpty()) {
                    dron.setEstado("MANTENIMIENTO");
                } else if ("MANTENIMIENTO".equals(dron.getEstado())) {
                    // Si no hay mantenimientos en progreso, volver a activo
                    dron.setEstado("ACTIVO");
                }
                
                dronDAO.actualizar(dron);
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error al actualizar estado del dron: " + dronId, e);
        }
    }
    
    private void actualizarUltimoMantenimientoDron(String dronId) {
        try {
            Dron dron = dronDAO.leerPorId(dronId);
            if (dron != null) {
                dron.setUltimoMantenimiento(LocalDateTime.now());
                dronDAO.actualizar(dron);
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error al actualizar último mantenimiento del dron: " + dronId, e);
        }
    }
}