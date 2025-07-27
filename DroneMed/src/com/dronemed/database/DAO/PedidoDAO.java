package com.dronemed.database.DAO;

import com.dronemed.database.ConexionBD;
import com.dronemed.modelo.Pedido;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAO {
    private ConexionBD conexion;
    
    public PedidoDAO() {
        this.conexion = ConexionBD.getInstance();
    }
    
    // Crear un nuevo pedido
    public boolean crear(Pedido pedido) {
        String sql = "INSERT INTO pedidos (cliente_id, descripcion_medicamento, peso_kg, prioridad, estado, direccion_destino, latitud_destino, longitud_destino, fecha_solicitud) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = conexion.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, pedido.getClienteId());
            pstmt.setString(2, pedido.getMedicamento());
            pstmt.setString(3, pedido.getDescripcion());
            pstmt.setDouble(4, pedido.getPeso());
            pstmt.setString(5, pedido.getDireccionEntrega());
            pstmt.setDouble(6, pedido.getLatitud());
            pstmt.setDouble(7, pedido.getLongitud());
            pstmt.setString(8, pedido.getEstado());
            pstmt.setString(9, pedido.getPrioridad());
            pstmt.setTimestamp(10, Timestamp.valueOf(pedido.getFechaPedido()));
            pstmt.setDouble(11, pedido.getCosto());
            
            int filasAfectadas = pstmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        pedido.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al crear pedido: " + e.getMessage());
        }
        
        return false;
    }
    
    // Leer pedido por ID
    public Pedido leerPorId(int id) {
        String sql = "SELECT * FROM pedidos WHERE id = ?";
        
        try (Connection conn = conexion.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapearPedido(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al leer pedido: " + e.getMessage());
        }
        
        return null;
    }
    
    // Leer todos los pedidos
    public List<Pedido> leerTodos() {
        List<Pedido> pedidos = new ArrayList<>();
        String sql = "SELECT * FROM pedidos ORDER BY fecha_solicitud DESC";
        
        try (Connection conn = conexion.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                pedidos.add(mapearPedido(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al leer todos los pedidos: " + e.getMessage());
        }
        
        return pedidos;
    }
    
    // Leer pedidos por estado
    public List<Pedido> leerPorEstado(String estado) {
        List<Pedido> pedidos = new ArrayList<>();
        String sql = "SELECT * FROM pedidos WHERE estado = ? ORDER BY fecha_solicitud DESC";
        
        try (Connection conn = conexion.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, estado);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    pedidos.add(mapearPedido(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al leer pedidos por estado: " + e.getMessage());
        }
        
        return pedidos;
    }
    
    // Leer pedidos pendientes
    public List<Pedido> leerPendientes() {
        return leerPorEstado("PENDIENTE");
    }
    
    // Leer pedidos por cliente
    public List<Pedido> leerPorCliente(int clienteId) {
        List<Pedido> pedidos = new ArrayList<>();
        String sql = "SELECT * FROM pedidos WHERE cliente_id = ? ORDER BY fecha_solicitud DESC";
        
        try (Connection conn = conexion.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, clienteId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    pedidos.add(mapearPedido(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al leer pedidos por cliente: " + e.getMessage());
        }
        
        return pedidos;
    }
    
    // Leer pedidos por dron asignado
    public List<Pedido> leerPorDron(int dronId) {
        List<Pedido> pedidos = new ArrayList<>();
        String sql = "SELECT * FROM pedidos WHERE dron_id = ? ORDER BY fecha_solicitud DESC";
        
        try (Connection conn = conexion.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, dronId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    pedidos.add(mapearPedido(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al leer pedidos por dron: " + e.getMessage());
        }
        
        return pedidos;
    }
    
    // Actualizar pedido
    public boolean actualizar(Pedido pedido) {
        String sql = "UPDATE pedidos SET cliente_id = ?, descripcion_medicamento = ?, peso_kg = ?, prioridad = ?, estado = ?, direccion_destino = ?, latitud_destino = ?, longitud_destino = ?, dron_id = ?, fecha_entrega = ? WHERE id = ?";
        
        try (Connection conn = conexion.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, pedido.getClienteId());
            pstmt.setString(2, pedido.getMedicamento());
            pstmt.setDouble(3, pedido.getPeso());
            pstmt.setString(4, pedido.getPrioridad());
            pstmt.setString(5, pedido.getEstado());
            pstmt.setString(6, pedido.getDireccionEntrega());
            pstmt.setDouble(7, pedido.getLatitud());
            pstmt.setDouble(8, pedido.getLongitud());
            pstmt.setInt(9, pedido.getDronAsignado());
            
            if (pedido.getFechaEntrega() != null) {
                pstmt.setTimestamp(10, Timestamp.valueOf(pedido.getFechaEntrega()));
            } else {
                pstmt.setNull(10, Types.TIMESTAMP);
            }
            
            pstmt.setInt(11, pedido.getId());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar pedido: " + e.getMessage());
        }
        
        return false;
    }
    
    // Asignar dron a pedido
    public boolean asignarDron(int pedidoId, int dronId) {
        String sql = "UPDATE pedidos SET dron_asignado = ?, estado = 'ASIGNADO' WHERE id = ?";
        
        try (Connection conn = conexion.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, dronId);
            pstmt.setInt(2, pedidoId);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al asignar dron: " + e.getMessage());
        }
        
        return false;
    }
    
    // Cambiar estado del pedido
    public boolean cambiarEstado(int pedidoId, String nuevoEstado) {
        String sql = "UPDATE pedidos SET estado = ? WHERE id = ?";
        
        try (Connection conn = conexion.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, nuevoEstado);
            pstmt.setInt(2, pedidoId);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al cambiar estado: " + e.getMessage());
        }
        
        return false;
    }
    
    // Marcar como entregado
    public boolean marcarComoEntregado(int pedidoId) {
        String sql = "UPDATE pedidos SET estado = 'ENTREGADO', fecha_entrega = ? WHERE id = ?";
        
        try (Connection conn = conexion.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setTimestamp(1, Timestamp.valueOf(java.time.LocalDateTime.now()));
            pstmt.setInt(2, pedidoId);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al marcar como entregado: " + e.getMessage());
        }
        
        return false;
    }
    
    // Eliminar pedido
    public boolean eliminar(int id) {
        String sql = "DELETE FROM pedidos WHERE id = ?";
        
        try (Connection conn = conexion.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar pedido: " + e.getMessage());
        }
        
        return false;
    }
    
    // Buscar pedidos por medicamento
    public List<Pedido> buscarPorMedicamento(String medicamento) {
        List<Pedido> pedidos = new ArrayList<>();
        String sql = "SELECT * FROM pedidos WHERE descripcion_medicamento LIKE ? ORDER BY fecha_solicitud DESC";
        
        try (Connection conn = conexion.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + medicamento + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    pedidos.add(mapearPedido(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar pedidos por medicamento: " + e.getMessage());
        }
        
        return pedidos;
    }
    
    // Método auxiliar para mapear ResultSet a Pedido
    private Pedido mapearPedido(ResultSet rs) throws SQLException {
        Pedido pedido = new Pedido();
        pedido.setId(rs.getInt("id"));
        pedido.setClienteId(rs.getInt("cliente_id"));
        pedido.setMedicamento(rs.getString("descripcion_medicamento"));
        pedido.setDescripcion(rs.getString("descripcion_medicamento"));
        pedido.setPeso(rs.getDouble("peso_kg"));
        pedido.setDireccionEntrega(rs.getString("direccion_destino"));
        pedido.setLatitud(rs.getDouble("latitud_destino"));
        pedido.setLongitud(rs.getDouble("longitud_destino"));
        pedido.setEstado(rs.getString("estado"));
        pedido.setPrioridad(rs.getString("prioridad"));
        pedido.setFechaPedido(rs.getTimestamp("fecha_solicitud").toLocalDateTime());
        
        Timestamp fechaEntrega = rs.getTimestamp("fecha_entrega");
        if (fechaEntrega != null) {
            pedido.setFechaEntrega(fechaEntrega.toLocalDateTime());
        }
        
        pedido.setDronAsignado(rs.getInt("dron_id"));
        // La columna costo no existe en el esquema actual
        pedido.setCosto(0.0);
        
        return pedido;
    }
}
