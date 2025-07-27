package com.dronemed.database.DAO;

import com.dronemed.database.ConexionBD;
import com.dronemed.modelo.Mantenimiento;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MantenimientoDAO {
    private static final Logger LOGGER = Logger.getLogger(MantenimientoDAO.class.getName());
    private ConexionBD conexion;
    
    public MantenimientoDAO() {
        this.conexion = ConexionBD.getInstance();
    }
    
    // Crear un nuevo mantenimiento
    public boolean crear(Mantenimiento mantenimiento) {
        String sql = "INSERT INTO mantenimientos (dron_id, tipo, descripcion, fecha_programada, " +
                    "tecnico_responsable, costo, estado, observaciones) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = conexion.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, mantenimiento.getDronId());
            pstmt.setString(2, mantenimiento.getTipoMantenimiento());
            pstmt.setString(3, mantenimiento.getDescripcion());
            pstmt.setTimestamp(4, Timestamp.valueOf(mantenimiento.getFechaInicio()));
            pstmt.setString(5, mantenimiento.getTecnicoResponsable());
            pstmt.setDouble(6, mantenimiento.getCosto());
            pstmt.setString(7, mantenimiento.getEstado());
            pstmt.setString(8, mantenimiento.getObservaciones());
            
            int filasAfectadas = pstmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        mantenimiento.setId(generatedKeys.getInt(1));
                    }
                }
                LOGGER.info("Mantenimiento creado exitosamente: ID " + mantenimiento.getId());
                return true;
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al crear mantenimiento", e);
        }
        
        return false;
    }
    
    // Leer mantenimiento por ID
    public Mantenimiento leerPorId(int id) {
        String sql = "SELECT * FROM mantenimientos WHERE id = ?";
        
        try (Connection conn = conexion.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapearMantenimiento(rs);
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al leer mantenimiento: " + id, e);
        }
        
        return null;
    }
    
    // Leer todos los mantenimientos
    public List<Mantenimiento> leerTodos() {
        List<Mantenimiento> mantenimientos = new ArrayList<>();
        String sql = "SELECT * FROM mantenimientos ORDER BY fecha_programada DESC";
        
        try (Connection conn = conexion.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                mantenimientos.add(mapearMantenimiento(rs));
            }
            
            LOGGER.info("Cargados " + mantenimientos.size() + " mantenimientos desde la base de datos");
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al leer todos los mantenimientos", e);
        }
        
        return mantenimientos;
    }
    
    // Leer mantenimientos por dron
    public List<Mantenimiento> leerPorDron(String dronId) {
        List<Mantenimiento> mantenimientos = new ArrayList<>();
        String sql = "SELECT * FROM mantenimientos WHERE dron_id = ? ORDER BY fecha_programada DESC";
        
        try (Connection conn = conexion.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, dronId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    mantenimientos.add(mapearMantenimiento(rs));
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al leer mantenimientos del dron: " + dronId, e);
        }
        
        return mantenimientos;
    }
    
    // Actualizar mantenimiento
    public boolean actualizar(Mantenimiento mantenimiento) {
        String sql = "UPDATE mantenimientos SET dron_id = ?, tipo = ?, descripcion = ?, " +
                    "fecha_programada = ?, fecha_inicio = ?, fecha_fin = ?, " +
                    "tecnico_responsable = ?, costo = ?, estado = ?, observaciones = ? WHERE id = ?";
        
        try (Connection conn = conexion.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, mantenimiento.getDronId());
            pstmt.setString(2, mantenimiento.getTipoMantenimiento());
            pstmt.setString(3, mantenimiento.getDescripcion());
            pstmt.setTimestamp(4, Timestamp.valueOf(mantenimiento.getFechaInicio()));
            
            if (mantenimiento.getFechaInicio() != null) {
                pstmt.setTimestamp(5, Timestamp.valueOf(mantenimiento.getFechaInicio()));
            } else {
                pstmt.setNull(5, Types.TIMESTAMP);
            }
            
            if (mantenimiento.getFechaFin() != null) {
                pstmt.setTimestamp(6, Timestamp.valueOf(mantenimiento.getFechaFin()));
            } else {
                pstmt.setNull(6, Types.TIMESTAMP);
            }
            
            pstmt.setString(7, mantenimiento.getTecnicoResponsable());
            pstmt.setDouble(8, mantenimiento.getCosto());
            pstmt.setString(9, mantenimiento.getEstado());
            pstmt.setString(10, mantenimiento.getObservaciones());
            pstmt.setInt(11, mantenimiento.getId());
            
            int filasAfectadas = pstmt.executeUpdate();
            LOGGER.info("Mantenimiento actualizado: ID " + mantenimiento.getId());
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al actualizar mantenimiento: " + mantenimiento.getId(), e);
        }
        
        return false;
    }
    
    // Eliminar mantenimiento
    public boolean eliminar(int id) {
        String sql = "DELETE FROM mantenimientos WHERE id = ?";
        
        try (Connection conn = conexion.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            int filasAfectadas = pstmt.executeUpdate();
            LOGGER.info("Mantenimiento eliminado: ID " + id);
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al eliminar mantenimiento: " + id, e);
        }
        
        return false;
    }
    
    // Buscar mantenimientos por estado
    public List<Mantenimiento> buscarPorEstado(String estado) {
        List<Mantenimiento> mantenimientos = new ArrayList<>();
        String sql = "SELECT * FROM mantenimientos WHERE estado = ? ORDER BY fecha_programada DESC";
        
        try (Connection conn = conexion.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, estado);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    mantenimientos.add(mapearMantenimiento(rs));
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al buscar mantenimientos por estado: " + estado, e);
        }
        
        return mantenimientos;
    }
    
    // Obtener estadísticas de mantenimiento
    public Map<String, Object> obtenerEstadisticas() {
        Map<String, Object> estadisticas = new HashMap<>();
        
        try (Connection conn = conexion.conectar()) {
            // Estadísticas por estado
            String sqlEstados = "SELECT estado, COUNT(*) as cantidad FROM mantenimientos GROUP BY estado";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sqlEstados)) {
                
                Map<String, Integer> porEstado = new HashMap<>();
                int total = 0;
                while (rs.next()) {
                    String estado = rs.getString("estado");
                    int cantidad = rs.getInt("cantidad");
                    porEstado.put(estado, cantidad);
                    total += cantidad;
                }
                estadisticas.put("porEstado", porEstado);
                estadisticas.put("total", total);
            }
            
            // Estadísticas por tipo
            String sqlTipos = "SELECT tipo, COUNT(*) as cantidad FROM mantenimientos GROUP BY tipo";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sqlTipos)) {
                
                Map<String, Integer> porTipo = new HashMap<>();
                while (rs.next()) {
                    String tipo = rs.getString("tipo");
                    int cantidad = rs.getInt("cantidad");
                    porTipo.put(tipo, cantidad);
                }
                estadisticas.put("porTipo", porTipo);
            }
            
            // Costo total
            String sqlCosto = "SELECT SUM(costo) as costo_total FROM mantenimientos WHERE estado = 'COMPLETADO'";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sqlCosto)) {
                
                if (rs.next()) {
                    double costoTotal = rs.getDouble("costo_total");
                    estadisticas.put("costoTotal", costoTotal);
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener estadísticas de mantenimiento", e);
        }
        
        return estadisticas;
    }
    
    // Mapear ResultSet a objeto Mantenimiento
    private Mantenimiento mapearMantenimiento(ResultSet rs) throws SQLException {
        Mantenimiento mantenimiento = new Mantenimiento();
        
        mantenimiento.setId(rs.getInt("id"));
        mantenimiento.setDronId(rs.getString("dron_id"));
        mantenimiento.setTipoMantenimiento(rs.getString("tipo"));
        mantenimiento.setDescripcion(rs.getString("descripcion"));
        mantenimiento.setEstado(rs.getString("estado"));
        mantenimiento.setTecnicoResponsable(rs.getString("tecnico_responsable"));
        mantenimiento.setCosto(rs.getDouble("costo"));
        mantenimiento.setObservaciones(rs.getString("observaciones"));
        
        // Fechas
        Timestamp fechaProgramada = rs.getTimestamp("fecha_programada");
        if (fechaProgramada != null) {
            mantenimiento.setFechaInicio(fechaProgramada.toLocalDateTime());
        }
        
        Timestamp fechaInicio = rs.getTimestamp("fecha_inicio");
        if (fechaInicio != null) {
            mantenimiento.setFechaInicio(fechaInicio.toLocalDateTime());
        }
        
        Timestamp fechaFin = rs.getTimestamp("fecha_fin");
        if (fechaFin != null) {
            mantenimiento.setFechaFin(fechaFin.toLocalDateTime());
        }
        
        return mantenimiento;
    }
}