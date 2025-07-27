package com.dronemed.main;

import com.dronemed.vista.VentanaPrincipal;
import com.dronemed.database.ConexionBD;
import com.dronemed.database.InicializadorBD;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;
import java.util.logging.Logger;
import java.util.logging.Level;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    
    public static void main(String[] args) {
        try {
            System.out.println("=== INICIANDO DRONEMED ===");
            System.out.println("Iniciando DroneMed - Sistema de Gestion de Drones Medicos");
            System.out.flush();

            System.out.println("Configurando Look and Feel...");
            configurarLookAndFeel();

            System.out.println("Verificando dependencias...");
            if (!verificarDependenciasBasicas()) {
                mostrarErrorYSalir("Error: Dependencias basicas faltantes");
                return;
            }

            System.out.println("Inicializando base de datos...");
            if (!inicializarBaseDatos()) {
                mostrarErrorYSalir("Error: No se pudo inicializar la base de datos");
                return;
            }

            System.out.println("Iniciando interfaz grafica...");
            SwingUtilities.invokeLater(() -> {
                try {
                    System.out.println("Creando ventana principal...");
                    
                    VentanaPrincipal ventana = new VentanaPrincipal();
                    System.out.println("VentanaPrincipal creada");
                    
                    ventana.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
                    ventana.setSize(1200, 800);
                    ventana.setLocationRelativeTo(null);
                    
                    System.out.println("Configuracion de ventana completada");
                    ventana.setVisible(true);
                    
                    System.out.println("Ventana principal creada y mostrada");
                    mostrarMensajeBienvenida();

                    LOGGER.info("Aplicacion DroneMed iniciada exitosamente");
                    System.out.println("=== DRONEMED INICIADO CORRECTAMENTE ===");
                } catch (Exception e) {
                    System.err.println("ERROR EN SWING THREAD:");
                    LOGGER.log(Level.SEVERE, "Error al iniciar la aplicacion", e);
                    e.printStackTrace();
                    mostrarErrorYSalir("Error inesperado al iniciar la aplicacion: " + e.getMessage());
                }
            });
        } catch (Exception e) {
            System.err.println("ERROR CRITICO EN MAIN:");
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void configurarLookAndFeel() {
        try {
            // Usar Look and Feel por defecto para evitar problemas de compatibilidad
            System.out.println("Look and Feel configurado: " + UIManager.getLookAndFeel().getName());
        } catch (Exception e) {
            LOGGER.warning("No se pudo configurar Look and Feel del sistema, usando por defecto");
            System.out.println("Usando Look and Feel por defecto");
        }
    }

    private static void mostrarMensajeBienvenida() {
        String mensaje = "Bienvenido a DroneMed\n\n" +
                        "Sistema de Gestión de Drones Médicos\n\n" +
                        "Funcionalidades Implementadas:\n" +
                        "- Gestión de Usuarios\n" +
                        "- Gestión de Drones\n" +
                        "- Gestión de Pedidos\n" +
                        "- Base de Datos SQLite\n" +
                        "- Validación de Datos\n" +
                        "- Interfaz Gráfica\n" +
                        "- Mapa de Seguimiento\n" +
                        "- Mantenimiento y Reportes";
        
        JOptionPane.showMessageDialog(null, mensaje, "DroneMed - Bienvenida", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void mostrarErrorYSalir(String mensaje) {
        System.err.println("Error: " + mensaje);
        LOGGER.severe(mensaje);

        try {
            JOptionPane.showMessageDialog(null,
                mensaje + "\n\nLa aplicacion se cerrara.",
                "Error Critico - DroneMed",
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            System.err.println("No se pudo mostrar el dialogo de error");
        }

        System.exit(1);
    }
    
    //verificar que las dependencias basicas esten disponibles
    private static boolean verificarDependenciasBasicas() {
        try {
            //verificar que las clases principales existan
            Class.forName("com.dronemed.vista.VentanaPrincipal");
            Class.forName("com.dronemed.modelo.Dron");
            Class.forName("com.dronemed.modelo.Pedido");
            Class.forName("com.dronemed.controlador.ControladorDron");
            Class.forName("com.dronemed.controlador.ControladorPedido");
            Class.forName("com.dronemed.database.ConexionBD");
            Class.forName("com.dronemed.database.InicializadorBD");
            
            System.out.println("Dependencias básicas verificadas correctamente");
            return true;
        } catch (ClassNotFoundException e) {
            System.err.println("Error: Clase faltante - " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Error inesperado al verificar dependencias: " + e.getMessage());
            return false;
        }
    }
    
    //inicializar la base de datos
    private static boolean inicializarBaseDatos() {
        try {
            System.out.println("Inicializando base de datos...");
            
            //verificar conexion
            if (ConexionBD.conectar() == null) {
                System.err.println("Error: No se pudo establecer conexión con la base de datos");
                return false;
            }
            
            //inicializar esquema de base de datos
            InicializadorBD inicializador = new InicializadorBD();
            inicializador.inicializarBaseDatos();
            
            System.out.println("Base de datos inicializada correctamente");
            return true;
        } catch (Exception e) {
            System.err.println("Error al inicializar base de datos: " + e.getMessage());
            LOGGER.log(Level.SEVERE, "Error al inicializar base de datos", e);
            return false;
        }
    }
}
