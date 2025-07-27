package com.dronemed.database.DAO;

import com.dronemed.database.ConexionBD;
import com.dronemed.modelo.Cliente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {
    
    // Leer todos los clientes
    public List<Cliente> leerTodos() {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM clientes WHERE activo = 1 ORDER BY nombre";
        
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Cliente cliente = mapearResultSet(rs);
                clientes.add(cliente);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al leer todos los clientes: " + e.getMessage());
        }
        
        return clientes;
    }
    
    // Leer cliente por ID
    public Cliente leerPorId(int id) {
        String sql = "SELECT * FROM clientes WHERE id = ? AND activo = 1";
        
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al leer cliente por ID: " + e.getMessage());
        }
        
        return null;
    }
    
    // Obtener coordenadas de un cliente
    public double[] obtenerCoordenadas(int clienteId) {
        String sql = "SELECT latitud, longitud FROM clientes WHERE id = ? AND activo = 1";
        
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, clienteId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    double latitud = rs.getDouble("latitud");
                    double longitud = rs.getDouble("longitud");
                    return new double[]{latitud, longitud};
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener coordenadas del cliente: " + e.getMessage());
        }
        
        return null;
    }
    
    // Calcular distancia desde DroneMed hasta el cliente basada en coordenadas del mapa visual
    public double calcularDistanciaACliente(int clienteId) {
        // Coordenadas del centro de operaciones DroneMed en el mapa visual
        final int CENTRO_X = 400;
        final int CENTRO_Y = 300;
        
        // Coordenadas predefinidas del mapa visual para cada cliente (mismo orden que en PanelMapa)
        int[][] coordenadasMapa = {
            {150, 100}, {650, 150}, {200, 350}, {600, 400}, {120, 450},
            {700, 80}, {80, 60}, {350, 150}, {250, 250}, {150, 200},
            {100, 150}, {650, 200}
        };
        
        // El clienteId corresponde al índice + 1 (ya que los IDs empiezan en 1)
        int indice = clienteId - 1;
        
        if (indice >= 0 && indice < coordenadasMapa.length) {
            int clienteX = coordenadasMapa[indice][0];
            int clienteY = coordenadasMapa[indice][1];
            
            // Calcular distancia euclidiana en píxeles y convertir a km (escala visual)
            double distanciaPixeles = Math.sqrt(Math.pow(clienteX - CENTRO_X, 2) + Math.pow(clienteY - CENTRO_Y, 2));
            
            // Convertir píxeles a kilómetros con una escala apropiada (1 píxel = ~0.1 km)
            double distanciaKm = distanciaPixeles * 0.1;
            
            System.out.println("Cliente " + clienteId + " - Distancia visual: " + String.format("%.2f", distanciaKm) + " km");
            return distanciaKm;
        }
        
        return 0.0;
    }
    
    // Fórmula de Haversine para calcular distancia entre dos puntos geográficos
    private double calcularDistanciaHaversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radio de la Tierra en kilómetros
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c; // Distancia en kilómetros
    }
    
    // Mapear ResultSet a objeto Cliente
    private Cliente mapearResultSet(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente();
        cliente.setId(rs.getInt("id"));
        cliente.setNombre(rs.getString("nombre"));
        cliente.setTipo(rs.getString("tipo_institucion"));
        cliente.setDireccion(rs.getString("direccion"));
        cliente.setTelefono(rs.getString("telefono"));
        cliente.setEmail(rs.getString("email"));
        cliente.setActivo(rs.getBoolean("activo"));
        
        // Convertir timestamp a LocalDateTime si es necesario
        Timestamp fechaRegistro = rs.getTimestamp("fecha_registro");
        if (fechaRegistro != null) {
            cliente.setFechaRegistro(fechaRegistro.toLocalDateTime());
        }
        
        return cliente;
    }
}