package com.dronemed.database.DAO;

import com.dronemed.database.ConexionBD;
import com.dronemed.modelo.*;
import com.dronemed.modelo.DronConcreto;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.*;

public class DronDAO {
    private static final Logger LOGGER = Logger.getLogger(DronDAO.class.getName());
    private ConexionBD conexion;
    
    public DronDAO() {
        this.conexion = ConexionBD.getInstance();
    }
    
    // Crear un nuevo dron
    public boolean crear(Dron dron) {
        String sql = "INSERT INTO drones (id, modelo, tipo_carga, capacidad_kg, " +
                    "autonomia_km, velocidad_max_kmh, estado, ubicacion_actual, " +
                    "horas_vuelo, ultimo_mantenimiento) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = conexion.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, dron.getId());
            pstmt.setString(2, dron.getModelo());
            pstmt.setString(3, dron.getTipoCarga());
            pstmt.setDouble(4, dron.getCapacidadKg());
            pstmt.setDouble(5, dron.getAutonomiaKm());
            pstmt.setDouble(6, dron.getVelocidadMaxKmh());
            pstmt.setString(7, dron.getEstado());
            pstmt.setString(8, dron.getUbicacionActual());
            pstmt.setDouble(9, dron.getHorasVuelo());
            
            if (dron.getUltimoMantenimiento() != null) {
                pstmt.setString(10, dron.getUltimoMantenimiento().toString());
            } else {
                pstmt.setNull(10, Types.VARCHAR);
            }
            
            int filasAfectadas = pstmt.executeUpdate();
            LOGGER.info("Dron creado exitosamente: ID " + dron.getId());
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al crear dron: " + dron.getId(), e);
            return false;
        }
    }
    
    // Leer un dron por ID
    public Dron leerPorId(String id) {
        String sql = "SELECT * FROM drones WHERE id = ?";
        
        try (Connection conn = conexion.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return crearDronDesdeResultSet(rs);
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al leer dron: " + id, e);
        }
        
        return null;
    }
    
    // Leer todos los drones
    public List<Dron> leerTodos() {
        List<Dron> drones = new ArrayList<>();
        String sql = "SELECT * FROM drones ORDER BY id";
        
        try (Connection conn = conexion.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Dron dron = crearDronDesdeResultSet(rs);
                if (dron != null) {
                    drones.add(dron);
                }
            }
            
            LOGGER.info("Cargados " + drones.size() + " drones desde la base de datos");
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al leer todos los drones", e);
        }
        
        return drones;
    }
    
    // Actualizar un dron
    public boolean actualizar(Dron dron) {
        String sql = "UPDATE drones SET modelo = ?, tipo_carga = ?, capacidad_kg = ?, " +
                    "autonomia_km = ?, velocidad_max_kmh = ?, estado = ?, " +
                    "ubicacion_actual = ?, horas_vuelo = ?, ultimo_mantenimiento = ? " +
                    "WHERE id = ?";
        
        try (Connection conn = conexion.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, dron.getModelo());
            pstmt.setString(2, dron.getTipoCarga());
            pstmt.setDouble(3, dron.getCapacidadKg());
            pstmt.setDouble(4, dron.getAutonomiaKm());
            pstmt.setDouble(5, dron.getVelocidadMaxKmh());
            pstmt.setString(6, dron.getEstado());
            pstmt.setString(7, dron.getUbicacionActual());
            pstmt.setDouble(8, dron.getHorasVuelo());
            
            if (dron.getUltimoMantenimiento() != null) {
                pstmt.setString(9, dron.getUltimoMantenimiento().toString());
            } else {
                pstmt.setNull(9, Types.VARCHAR);
            }
            
            pstmt.setString(10, dron.getId());
            
            int filasAfectadas = pstmt.executeUpdate();
            LOGGER.info("Dron actualizado: ID " + dron.getId());
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al actualizar dron: " + dron.getId(), e);
            return false;
        }
    }
    
    // Eliminar un dron
    public boolean eliminar(String id) {
        String sql = "DELETE FROM drones WHERE id = ?";
        
        try (Connection conn = conexion.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, id);
            int filasAfectadas = pstmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                LOGGER.info("Dron eliminado: ID " + id);
                return true;
            } else {
                LOGGER.warning("No se encontró dron con ID: " + id);
                return false;
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al eliminar dron: " + id, e);
            return false;
        }
    }
    
    // Buscar drones por estado
    public List<Dron> buscarPorEstado(String estado) {
        List<Dron> drones = new ArrayList<>();
        String sql = "SELECT * FROM drones WHERE estado = ? ORDER BY id";
        
        try (Connection conn = conexion.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, estado);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Dron dron = crearDronDesdeResultSet(rs);
                if (dron != null) {
                    drones.add(dron);
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al buscar drones por estado: " + estado, e);
        }
        
        return drones;
    }
    
    // Obtener estadísticas
    public Map<String, Integer> obtenerEstadisticas() {
        Map<String, Integer> estadisticas = new HashMap<>();
        String sql = "SELECT estado, COUNT(*) as cantidad FROM drones GROUP BY estado";
        
        try (Connection conn = conexion.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            int total = 0;
            while (rs.next()) {
                String estado = rs.getString("estado");
                int cantidad = rs.getInt("cantidad");
                estadisticas.put(estado, cantidad);
                total += cantidad;
            }
            estadisticas.put("TOTAL", total);
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener estadísticas", e);
        }
        
        return estadisticas;
    }
    
    // Método auxiliar para crear objetos Dron desde ResultSet
    private Dron crearDronDesdeResultSet(ResultSet rs) throws SQLException {
        String tipoCarga = rs.getString("tipo_carga");
        Dron dron;
        
        // Factory pattern para crear el tipo correcto de dron
        try {
            switch (tipoCarga.toUpperCase()) {
                case "LIGERA":
                    dron = new DronCargaLigera();
                    break;
                case "MEDIA":
                    dron = new DronCargaMedia();
                    break;
                case "PESADA":
                    dron = new DronCargaPesada();
                    break;
                default:
                    // Usar DronConcreto como fallback
                    dron = new DronConcreto();
                    break;
            }
        } catch (Exception e) {
            // Si hay error con las clases específicas, usar DronConcreto
            LOGGER.warning("Error creando dron específico, usando DronConcreto: " + e.getMessage());
            dron = new DronConcreto();
        }
        
        // Establecer propiedades
        dron.setId(rs.getString("id"));
        dron.setModelo(rs.getString("modelo"));
        dron.setTipoCarga(rs.getString("tipo_carga"));
        dron.setCapacidadKg(rs.getDouble("capacidad_kg"));
        dron.setAutonomiaKm(rs.getDouble("autonomia_km"));
        dron.setVelocidadMaxKmh(rs.getDouble("velocidad_max_kmh"));
        dron.setEstado(rs.getString("estado"));
        dron.setUbicacionActual(rs.getString("ubicacion_actual"));
        dron.setHorasVuelo(rs.getDouble("horas_vuelo"));
        
        String ultimoMantenimientoStr = rs.getString("ultimo_mantenimiento");
        if (ultimoMantenimientoStr != null && !ultimoMantenimientoStr.isEmpty()) {
            try {
                dron.setUltimoMantenimiento(LocalDateTime.parse(ultimoMantenimientoStr));
            } catch (Exception e) {
                LOGGER.warning("Error al parsear fecha de mantenimiento: " + ultimoMantenimientoStr);
            }
        }
        
        return dron;
    }
}
