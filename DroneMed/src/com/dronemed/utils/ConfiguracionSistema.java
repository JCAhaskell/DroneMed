/*
 * Configuración centralizada del sistema DroneMed
 * Contiene todas las constantes y configuraciones utilizadas en la aplicación
 */
package com.dronemed.utils;

import java.awt.Color;
import java.awt.Font;

/**
 * Clase que centraliza todas las configuraciones del sistema DroneMed.
 * Incluye configuraciones de base de datos, límites operacionales,
 * configuraciones de interfaz gráfica y constantes del sistema.
 * 
 * @author Haskell
 */
public class ConfiguracionSistema {
    
    // ========== CONFIGURACIÓN DE BASE DE DATOS ==========
    public static final String DB_URL = "jdbc:sqlite:dronemed.db";
    public static final String DB_FILE = "dronemed.db";
    public static final String DB_DRIVER = "org.sqlite.JDBC";
    public static final String DB_PRAGMA_FK = "PRAGMA foreign_keys = ON;";
    
    // ========== CONFIGURACIÓN DE DRONES ==========
    // Límites operacionales
    public static final double HORAS_VUELO_MANTENIMIENTO = 100.0;
    public static final int DIAS_LIMITE_MANTENIMIENTO = 30;
    public static final int NIVEL_BATERIA_MINIMO = 20;
    public static final int NIVEL_BATERIA_CRITICO = 10;
    public static final int NIVEL_BATERIA_MAXIMO = 100;
    
    // Capacidades por tipo de dron
    public static final double CARGA_LIGERA_MAX_KG = 2.0;
    public static final double CARGA_MEDIA_MAX_KG = 10.0;
    public static final double CARGA_PESADA_MAX_KG = 25.0;
    
    // Distancias máximas por tipo de dron
    public static final double CARGA_LIGERA_MAX_DISTANCIA = 15.0;
    public static final double CARGA_MEDIA_MAX_DISTANCIA = 25.0;
    public static final double CARGA_PESADA_MAX_DISTANCIA = 50.0;
    
    // Estados de drones
    public static final String ESTADO_DISPONIBLE = "DISPONIBLE";
    public static final String ESTADO_EN_VUELO = "EN_VUELO";
    public static final String ESTADO_MANTENIMIENTO = "MANTENIMIENTO";
    public static final String ESTADO_FUERA_SERVICIO = "FUERA_SERVICIO";
    
    // Tipos de carga
    public static final String TIPO_CARGA_LIGERA = "LIGERA";
    public static final String TIPO_CARGA_MEDIA = "MEDIA";
    public static final String TIPO_CARGA_PESADA = "PESADA";
    
    // ========== CONFIGURACIÓN DE MANTENIMIENTO ==========
    public static final String MANTENIMIENTO_PROGRAMADO = "PROGRAMADO";
    public static final String MANTENIMIENTO_EN_PROGRESO = "EN_PROGRESO";
    public static final String MANTENIMIENTO_COMPLETADO = "COMPLETADO";
    public static final String MANTENIMIENTO_CANCELADO = "CANCELADO";
    
    // Tipos de mantenimiento
    public static final String TIPO_PREVENTIVO = "PREVENTIVO";
    public static final String TIPO_CORRECTIVO = "CORRECTIVO";
    public static final String TIPO_EMERGENCIA = "EMERGENCIA";
    
    // ========== CONFIGURACIÓN DE PEDIDOS ==========
    public static final String PEDIDO_PENDIENTE = "PENDIENTE";
    public static final String PEDIDO_ASIGNADO = "ASIGNADO";
    public static final String PEDIDO_EN_TRANSITO = "EN_TRANSITO";
    public static final String PEDIDO_ENTREGADO = "ENTREGADO";
    public static final String PEDIDO_CANCELADO = "CANCELADO";
    
    // Prioridades de pedido
    public static final String PRIORIDAD_BAJA = "BAJA";
    public static final String PRIORIDAD_MEDIA = "MEDIA";
    public static final String PRIORIDAD_ALTA = "ALTA";
    public static final String PRIORIDAD_URGENTE = "URGENTE";
    
    // ========== CONFIGURACIÓN DE USUARIOS ==========
    public static final String TIPO_ADMINISTRADOR = "ADMINISTRADOR";
    public static final String TIPO_OPERADOR = "OPERADOR";
    public static final String TIPO_SUPERVISOR = "SUPERVISOR";
    
    // ========== CONFIGURACIÓN DE CLIENTES ==========
    public static final String CLIENTE_HOSPITAL = "HOSPITAL";
    public static final String CLIENTE_FARMACIA = "FARMACIA";
    public static final String CLIENTE_CLINICA = "CLINICA";
    public static final String CLIENTE_PARTICULAR = "PARTICULAR";
    
    // ========== CONFIGURACIÓN DE INTERFAZ GRÁFICA ==========
    // Colores del sistema
    public static final Color COLOR_PRIMARIO = new Color(41, 128, 185);
    public static final Color COLOR_SECUNDARIO = new Color(52, 152, 219);
    public static final Color COLOR_EXITO = new Color(39, 174, 96);
    public static final Color COLOR_ADVERTENCIA = new Color(241, 196, 15);
    public static final Color COLOR_ERROR = new Color(231, 76, 60);
    public static final Color COLOR_INFO = new Color(142, 68, 173);
    
    // Colores de estado
    public static final Color COLOR_DISPONIBLE = new Color(46, 204, 113);
    public static final Color COLOR_EN_VUELO = new Color(52, 152, 219);
    public static final Color COLOR_MANTENIMIENTO = new Color(241, 196, 15);
    public static final Color COLOR_FUERA_SERVICIO = new Color(231, 76, 60);
    
    // Fuentes del sistema
    public static final Font FUENTE_TITULO = new Font("Arial", Font.BOLD, 16);
    public static final Font FUENTE_SUBTITULO = new Font("Arial", Font.BOLD, 14);
    public static final Font FUENTE_NORMAL = new Font("Arial", Font.PLAIN, 12);
    public static final Font FUENTE_PEQUENA = new Font("Arial", Font.PLAIN, 10);
    
    // Dimensiones de ventana
    public static final int VENTANA_ANCHO_MINIMO = 1000;
    public static final int VENTANA_ALTO_MINIMO = 700;
    public static final int VENTANA_ANCHO_DEFECTO = 1200;
    public static final int VENTANA_ALTO_DEFECTO = 800;
    
    // ========== CONFIGURACIÓN DE REPORTES ==========
    public static final String DIRECTORIO_REPORTES = "reportes";
    public static final String EXTENSION_REPORTE = ".txt";
    public static final String FORMATO_FECHA_REPORTE = "yyyy-MM-dd_HH-mm-ss";
    
    // ========== CONFIGURACIÓN DE LOGGING ==========
    public static final String DIRECTORIO_LOGS = "logs";
    public static final String ARCHIVO_LOG = "dronemed.log";
    
    // ========== CONFIGURACIÓN DE VALIDACIÓN ==========
    // Expresiones regulares
    public static final String REGEX_EMAIL = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    public static final String REGEX_TELEFONO = "^[0-9]{8,15}$";
    public static final String REGEX_ID_DRON = "^[A-Z]{2}[0-9]{3}$";
    
    // Límites de longitud
    public static final int LONGITUD_MINIMA_NOMBRE = 2;
    public static final int LONGITUD_MAXIMA_NOMBRE = 50;
    public static final int LONGITUD_MAXIMA_DESCRIPCION = 500;
    public static final int LONGITUD_MAXIMA_DIRECCION = 200;
    
    // ========== CONFIGURACIÓN DE SISTEMA ==========
    public static final String VERSION_APLICACION = "1.0.0";
    public static final String NOMBRE_APLICACION = "DroneMed";
    public static final String AUTOR_APLICACION = "Universidad - Proyecto Académico";
    
    // Configuración de rendimiento
    public static final int TIMEOUT_CONEXION_MS = 5000;
    public static final int REINTENTOS_CONEXION = 3;
    public static final int INTERVALO_ACTUALIZACION_MS = 30000; // 30 segundos
    
    // ========== MÉTODOS UTILITARIOS ==========
    
    /**
     * Obtiene el color asociado a un estado de dron
     * @param estado El estado del dron
     * @return Color correspondiente al estado
     */
    public static Color obtenerColorEstado(String estado) {
        switch (estado.toUpperCase()) {
            case ESTADO_DISPONIBLE:
                return COLOR_DISPONIBLE;
            case ESTADO_EN_VUELO:
                return COLOR_EN_VUELO;
            case ESTADO_MANTENIMIENTO:
                return COLOR_MANTENIMIENTO;
            case ESTADO_FUERA_SERVICIO:
                return COLOR_FUERA_SERVICIO;
            default:
                return Color.GRAY;
        }
    }
    
    /**
     * Obtiene la capacidad máxima según el tipo de carga
     * @param tipoCarga El tipo de carga del dron
     * @return Capacidad máxima en kg
     */
    public static double obtenerCapacidadMaxima(String tipoCarga) {
        switch (tipoCarga.toUpperCase()) {
            case TIPO_CARGA_LIGERA:
                return CARGA_LIGERA_MAX_KG;
            case TIPO_CARGA_MEDIA:
                return CARGA_MEDIA_MAX_KG;
            case TIPO_CARGA_PESADA:
                return CARGA_PESADA_MAX_KG;
            default:
                return 0.0;
        }
    }
    
    /**
     * Obtiene la distancia máxima según el tipo de carga
     * @param tipoCarga El tipo de carga del dron
     * @return Distancia máxima en km
     */
    public static double obtenerDistanciaMaxima(String tipoCarga) {
        switch (tipoCarga.toUpperCase()) {
            case TIPO_CARGA_LIGERA:
                return CARGA_LIGERA_MAX_DISTANCIA;
            case TIPO_CARGA_MEDIA:
                return CARGA_MEDIA_MAX_DISTANCIA;
            case TIPO_CARGA_PESADA:
                return CARGA_PESADA_MAX_DISTANCIA;
            default:
                return 0.0;
        }
    }
    
    /**
     * Valida si un nivel de batería está en rango crítico
     * @param nivelBateria Nivel de batería (0-100)
     * @return true si está en nivel crítico
     */
    public static boolean esBateriaCritica(int nivelBateria) {
        return nivelBateria <= NIVEL_BATERIA_CRITICO;
    }
    
    /**
     * Valida si un nivel de batería está en rango bajo
     * @param nivelBateria Nivel de batería (0-100)
     * @return true si está en nivel bajo
     */
    public static boolean esBateriaLow(int nivelBateria) {
        return nivelBateria <= NIVEL_BATERIA_MINIMO;
    }
    
    /**
     * Obtiene información completa del sistema
     * @return String con información del sistema
     */
    public static String obtenerInfoSistema() {
        StringBuilder info = new StringBuilder();
        info.append(NOMBRE_APLICACION).append(" v").append(VERSION_APLICACION).append("\n");
        info.append("Desarrollado por: ").append(AUTOR_APLICACION).append("\n");
        info.append("Base de datos: ").append(DB_FILE).append("\n");
        info.append("Configuración cargada exitosamente");
        return info.toString();
    }
    
    // Constructor privado para evitar instanciación
    private ConfiguracionSistema() {
        throw new UnsupportedOperationException("Esta es una clase de utilidades y no debe ser instanciada");
    }
}
