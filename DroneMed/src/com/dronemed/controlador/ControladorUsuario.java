package com.dronemed.controlador;

import com.dronemed.database.DAO.UsuarioDAO;
import com.dronemed.modelo.Usuario;
import com.dronemed.utils.ValidadorDatos;
import java.util.List;

public class ControladorUsuario {
    private UsuarioDAO usuarioDAO;
    
    public ControladorUsuario() {
        this.usuarioDAO = new UsuarioDAO();
    }
    
    // Crear un nuevo usuario
    public boolean crearUsuario(String nombre, String apellido, String email, 
                               String telefono, String direccion, String tipoUsuario) {
        
        // Validar datos de entrada
        if (!validarDatosUsuario(nombre, apellido, email, telefono, direccion, tipoUsuario)) {
            return false;
        }
        
        // Verificar si el email ya existe
        if (usuarioDAO.existeEmail(email)) {
            System.err.println("Error: El email ya está registrado");
            return false;
        }
        
        // Crear el usuario
        Usuario usuario = new Usuario(nombre, apellido, email, telefono, direccion, tipoUsuario);
        
        return usuarioDAO.crear(usuario);
    }
    
    // Obtener usuario por ID
    public Usuario obtenerUsuario(int id) {
        if (!ValidadorDatos.validarId(id)) {
            System.err.println("Error: ID de usuario inválido");
            return null;
        }
        
        return usuarioDAO.leerPorId(id);
    }
    
    // Obtener usuario por email
    public Usuario obtenerUsuarioPorEmail(String email) {
        if (!ValidadorDatos.validarEmail(email)) {
            System.err.println("Error: Email inválido");
            return null;
        }
        
        return usuarioDAO.leerPorEmail(email);
    }
    
    // Obtener todos los usuarios
    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioDAO.leerTodos();
    }
    
    // Obtener usuarios activos
    public List<Usuario> obtenerUsuariosActivos() {
        return usuarioDAO.leerActivos();
    }
    
    // Actualizar usuario
    public boolean actualizarUsuario(int id, String nombre, String apellido, String email, 
                                    String telefono, String direccion, String tipoUsuario) {
        
        // Validar ID
        if (!ValidadorDatos.validarId(id)) {
            System.err.println("Error: ID de usuario inválido");
            return false;
        }
        
        // Obtener usuario existente
        Usuario usuario = usuarioDAO.leerPorId(id);
        if (usuario == null) {
            System.err.println("Error: Usuario no encontrado");
            return false;
        }
        
        // Validar datos de entrada
        if (!validarDatosUsuario(nombre, apellido, email, telefono, direccion, tipoUsuario)) {
            return false;
        }
        
        // Verificar si el email ya existe (excepto para el usuario actual)
        Usuario usuarioConEmail = usuarioDAO.leerPorEmail(email);
        if (usuarioConEmail != null && usuarioConEmail.getId() != id) {
            System.err.println("Error: El email ya está registrado por otro usuario");
            return false;
        }
        
        // Actualizar datos
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setEmail(email);
        usuario.setTelefono(telefono);
        usuario.setDireccion(direccion);
        usuario.setTipoUsuario(tipoUsuario);
        
        return usuarioDAO.actualizar(usuario);
    }
    
    // Eliminar usuario (eliminación lógica)
    public boolean eliminarUsuario(int id) {
        if (!ValidadorDatos.validarId(id)) {
            System.err.println("Error: ID de usuario inválido");
            return false;
        }
        
        Usuario usuario = usuarioDAO.leerPorId(id);
        if (usuario == null) {
            System.err.println("Error: Usuario no encontrado");
            return false;
        }
        
        return usuarioDAO.eliminar(id);
    }
    
    // Activar usuario
    public boolean activarUsuario(int id) {
        if (!ValidadorDatos.validarId(id)) {
            System.err.println("Error: ID de usuario inválido");
            return false;
        }
        
        Usuario usuario = usuarioDAO.leerPorId(id);
        if (usuario == null) {
            System.err.println("Error: Usuario no encontrado");
            return false;
        }
        
        usuario.setActivo(true);
        return usuarioDAO.actualizar(usuario);
    }
    
    // Desactivar usuario
    public boolean desactivarUsuario(int id) {
        if (!ValidadorDatos.validarId(id)) {
            System.err.println("Error: ID de usuario inválido");
            return false;
        }
        
        Usuario usuario = usuarioDAO.leerPorId(id);
        if (usuario == null) {
            System.err.println("Error: Usuario no encontrado");
            return false;
        }
        
        usuario.setActivo(false);
        return usuarioDAO.actualizar(usuario);
    }
    
    // Buscar usuarios por nombre
    public List<Usuario> buscarUsuariosPorNombre(String nombre) {
        if (!ValidadorDatos.validarTextoNoVacio(nombre)) {
            System.err.println("Error: Nombre de búsqueda inválido");
            return null;
        }
        
        return usuarioDAO.buscarPorNombre(nombre);
    }
    
    // Verificar si existe un email
    public boolean existeEmail(String email) {
        if (!ValidadorDatos.validarEmail(email)) {
            return false;
        }
        
        return usuarioDAO.existeEmail(email);
    }
    
    // Obtener estadísticas de usuarios
    public String obtenerEstadisticasUsuarios() {
        List<Usuario> todosLosUsuarios = usuarioDAO.leerTodos();
        List<Usuario> usuariosActivos = usuarioDAO.leerActivos();
        
        int total = todosLosUsuarios.size();
        int activos = usuariosActivos.size();
        int inactivos = total - activos;
        
        return String.format("Total: %d, Activos: %d, Inactivos: %d", total, activos, inactivos);
    }
    
    // Método privado para validar datos del usuario
    private boolean validarDatosUsuario(String nombre, String apellido, String email, 
                                       String telefono, String direccion, String tipoUsuario) {
        
        // Validar nombre
        if (!ValidadorDatos.validarNombre(nombre)) {
            System.err.println("Error: Nombre inválido");
            return false;
        }
        
        // Validar apellido
        if (!ValidadorDatos.validarNombre(apellido)) {
            System.err.println("Error: Apellido inválido");
            return false;
        }
        
        // Validar email
        if (!ValidadorDatos.validarEmail(email)) {
            System.err.println("Error: Email inválido");
            return false;
        }
        
        // Validar teléfono
        if (!ValidadorDatos.validarTelefono(telefono)) {
            System.err.println("Error: Teléfono inválido");
            return false;
        }
        
        // Validar dirección
        if (!ValidadorDatos.validarDireccion(direccion)) {
            System.err.println("Error: Dirección inválida");
            return false;
        }
        
        // Validar tipo de usuario
        if (!ValidadorDatos.validarTextoNoVacio(tipoUsuario)) {
            System.err.println("Error: Tipo de usuario inválido");
            return false;
        }
        
        return true;
    }
    
    // Limpiar datos de entrada
    public Usuario limpiarDatosUsuario(Usuario usuario) {
        if (usuario != null) {
            usuario.setNombre(ValidadorDatos.limpiarTexto(usuario.getNombre()));
            usuario.setApellido(ValidadorDatos.limpiarTexto(usuario.getApellido()));
            usuario.setEmail(ValidadorDatos.limpiarTexto(usuario.getEmail()));
            usuario.setTelefono(ValidadorDatos.limpiarTexto(usuario.getTelefono()));
            usuario.setDireccion(ValidadorDatos.limpiarTexto(usuario.getDireccion()));
            usuario.setTipoUsuario(ValidadorDatos.limpiarTexto(usuario.getTipoUsuario()));
        }
        return usuario;
    }
}
