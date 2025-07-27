package com.dronemed.database;

import com.dronemed.utils.ConfiguracionSistema;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConexionBD {
    private static final Logger LOGGER = Logger.getLogger(ConexionBD.class.getName());
    private static Connection conexion = null;
    
    private static ConexionBD instance;
    
    //Singleton pattern
    private ConexionBD() {}
    
    public static ConexionBD getInstance() {
        if (instance == null) {
            instance = new ConexionBD();
        }
        return instance;
    }

    //Establece conexion con la base de datos SQLite y si no existe la crea
    public static Connection conectar() {
        try {
            if (conexion == null || conexion.isClosed()) {
                
                //verificar si existe
                boolean dbExiste = verificarBaseDatos();
                
                //cargar el driver SQLite
                Class.forName(ConfiguracionSistema.DB_DRIVER);
                
                //establecer conexion
                conexion = DriverManager.getConnection(ConfiguracionSistema.DB_URL);
                
                //habilitar foreign keys
                Statement stmt = conexion.createStatement();
                stmt.execute(ConfiguracionSistema.DB_PRAGMA_FK);
                stmt.close();
                
                if (!dbExiste) {
                    LOGGER.warning("Base de datos no encontrada. Ejecuta inicializadorBD.inicializarBaseDatos() primero.");
                } else {
                    LOGGER.info("Conexion a SQLite establecida exitosamente");
                }
            }    
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Driver SQLite no encontrado", e);
        } catch (SQLException e ) {
            LOGGER.log(Level.SEVERE, "Error al conectar con la base de datos", e);
        }
        return conexion;
    }
    
    //cerrar la conexion actual
    public static void cerrarConexion() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
                conexion = null;
                LOGGER.info("Conexion cerrada exitosamente");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al cerrar la conexion", e);
        }
    }
    
    //prueba conexion a base de datos
    public static boolean probarConexion() {
        try {
            Connection conn = conectar();
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Fallo en prueba de conexion", e);
            return false;
        }
    }
    
    //verifica si el archivo DB existe
    public static boolean verificarBaseDatos() {
        File dbFile = new File(ConfiguracionSistema.DB_FILE);
        return dbFile.exists() && dbFile.length() > 0;
    }
    
    //verificar si las tablas principales existen
    public static boolean verificarTablas() {
        try {
            Connection conn = conectar();
            if (conn == null) return false;
            
            Statement stmt = conn.createStatement();
            String[] tablas = {"usuarios", "clientes", "drones", "pedido", "mantenimiento"};
            
            for (String tabla : tablas) {
                ResultSet rs = stmt.executeQuery(
                    "SELECT name FROM sqlite_master WHERE type='table' AND name='" + tabla + "'");
                if (!rs.next()) {
                    rs.close();
                    stmt.close();
                    return false;
                }
                rs.close();
            }
            
            stmt.close();
            return true;
        
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error verificando tablas", e);
            return false;
        } 
    }
    
    //Obtener informacion del estado de la base de datos
    public static String obtenerEstadoBD() {
        StringBuilder estado = new StringBuilder();
        
        estado.append("Estado de la Base de Datos:\n");
        estado.append("- Archivo existe: ").append(verificarBaseDatos() ? "Sí" : "No").append("\n");
        estado.append("- Conexión: ").append(probarConexion() ? "OK" : "Error").append("\n");
        estado.append("- Tablas: ").append(verificarTablas() ? "Completas" : "Faltantes").append("\n");
        
        if (!verificarBaseDatos() || !verificarTablas()) {
            estado.append("\n Solución: Ejecutar InicializadorBD.inicializarBaseDatos()");
        }
        
        return estado.toString();
    }   
}
