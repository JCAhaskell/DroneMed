package com.dronemed.database.DAO;

import com.dronemed.database.ConexionBD;
import com.dronemed.modelo.Usuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {
    private ConexionBD conexion;
    
    public UsuarioDAO() {
        this.conexion = ConexionBD.getInstance();
    }
    
    // Crear un nuevo usuario
    public boolean crear(Usuario usuario) {
        String sql = "INSERT INTO usuarios (nombre, apellido, email, telefono, direccion, tipo_usuario, activo, fecha_registro) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = conexion.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, usuario.getNombre());
            pstmt.setString(2, usuario.getApellido());
            pstmt.setString(3, usuario.getEmail());
            pstmt.setString(4, usuario.getTelefono());
            pstmt.setString(5, usuario.getDireccion());
            pstmt.setString(6, usuario.getTipoUsuario());
            pstmt.setBoolean(7, usuario.isActivo());
            pstmt.setTimestamp(8, Timestamp.valueOf(usuario.getFechaRegistro()));
            
            int filasAfectadas = pstmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        usuario.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al crear usuario: " + e.getMessage());
        }
        
        return false;
    }
    
    // Leer usuario por ID
    public Usuario leerPorId(int id) {
        String sql = "SELECT * FROM usuarios WHERE id = ?";
        
        try (Connection conn = conexion.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapearUsuario(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al leer usuario: " + e.getMessage());
        }
        
        return null;
    }
    
    // Leer usuario por email
    public Usuario leerPorEmail(String email) {
        String sql = "SELECT * FROM usuarios WHERE email = ?";
        
        try (Connection conn = conexion.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapearUsuario(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al leer usuario por email: " + e.getMessage());
        }
        
        return null;
    }
    
    // Leer todos los usuarios
    public List<Usuario> leerTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios ORDER BY nombre, apellido";
        
        try (Connection conn = conexion.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                usuarios.add(mapearUsuario(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al leer todos los usuarios: " + e.getMessage());
        }
        
        return usuarios;
    }
    
    // Leer usuarios activos
    public List<Usuario> leerActivos() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios WHERE activo = true ORDER BY nombre, apellido";
        
        try (Connection conn = conexion.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                usuarios.add(mapearUsuario(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al leer usuarios activos: " + e.getMessage());
        }
        
        return usuarios;
    }
    
    // Actualizar usuario
    public boolean actualizar(Usuario usuario) {
        String sql = "UPDATE usuarios SET nombre = ?, apellido = ?, email = ?, telefono = ?, direccion = ?, tipo_usuario = ?, activo = ? WHERE id = ?";
        
        try (Connection conn = conexion.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, usuario.getNombre());
            pstmt.setString(2, usuario.getApellido());
            pstmt.setString(3, usuario.getEmail());
            pstmt.setString(4, usuario.getTelefono());
            pstmt.setString(5, usuario.getDireccion());
            pstmt.setString(6, usuario.getTipoUsuario());
            pstmt.setBoolean(7, usuario.isActivo());
            pstmt.setInt(8, usuario.getId());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
        }
        
        return false;
    }
    
    // Eliminar usuario (eliminación lógica)
    public boolean eliminar(int id) {
        String sql = "UPDATE usuarios SET activo = false WHERE id = ?";
        
        try (Connection conn = conexion.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
        }
        
        return false;
    }
    
    // Verificar si existe un email
    public boolean existeEmail(String email) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE email = ?";
        
        try (Connection conn = conexion.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al verificar email: " + e.getMessage());
        }
        
        return false;
    }
    
    // Buscar usuarios por nombre
    public List<Usuario> buscarPorNombre(String nombre) {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios WHERE (nombre LIKE ? OR apellido LIKE ?) AND activo = true ORDER BY nombre, apellido";
        
        try (Connection conn = conexion.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String patron = "%" + nombre + "%";
            pstmt.setString(1, patron);
            pstmt.setString(2, patron);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    usuarios.add(mapearUsuario(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar usuarios por nombre: " + e.getMessage());
        }
        
        return usuarios;
    }
    
    // Método auxiliar para mapear ResultSet a Usuario
    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(rs.getInt("id"));
        usuario.setNombre(rs.getString("nombre"));
        usuario.setApellido(rs.getString("apellido"));
        usuario.setEmail(rs.getString("email"));
        usuario.setTelefono(rs.getString("telefono"));
        usuario.setDireccion(rs.getString("direccion"));
        usuario.setTipoUsuario(rs.getString("tipo_usuario"));
        usuario.setActivo(rs.getBoolean("activo"));
        usuario.setFechaRegistro(rs.getTimestamp("fecha_registro").toLocalDateTime());
        return usuario;
    }
}
