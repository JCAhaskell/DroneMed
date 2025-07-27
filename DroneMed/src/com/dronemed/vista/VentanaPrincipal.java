package com.dronemed.vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VentanaPrincipal extends JFrame {
    private JTabbedPane tabbedPane;
    private FormularioDron formularioDron;
    private FormularioPedido formularioPedido;
    private PanelMapa panelMapa;
    private PanelMantenimiento panelMantenimiento;
    private JLabel lblUsuarioActual;
    
    public VentanaPrincipal() {
        //configurar ventana principal
        setTitle("DroneMed - Sistema de Gestion de Entregas Medicas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        //crear barra de menu
        crearBarraMenu();

        //crear panel superior con informacion del usuario
        crearPanelSuperior();

        //crear pestanas principales
        crearPestanas();

        //crear barra de estado
        crearBarraEstado();
    }

    private void crearBarraMenu() {
        JMenuBar menuBar = new JMenuBar();

        //menu archivo
        JMenu menuArchivo = new JMenu("Archivo");
        JMenuItem itemSalir = new JMenuItem("Salir");
        itemSalir.addActionListener(e -> System.exit(0));
        menuArchivo.add(itemSalir);      
        
        //menu gestion
        JMenu menuGestion = new JMenu("Gestión");
        JMenuItem itemDrones = new JMenuItem("Gestionar Drones");
        JMenuItem itemPedidos = new JMenuItem("Gestionar Pedidos");
        JMenuItem itemClientes = new JMenuItem("Gestionar Clientes");
        JMenuItem itemMantenimiento = new JMenuItem("Mantenimiento");
        
        itemDrones.addActionListener(e -> tabbedPane.setSelectedIndex(0));
        itemPedidos.addActionListener(e -> tabbedPane.setSelectedIndex(1));
        itemMantenimiento.addActionListener(e -> tabbedPane.setSelectedIndex(3));
        
        menuGestion.add(itemDrones);
        menuGestion.add(itemPedidos);
        menuGestion.add(itemClientes);
        menuGestion.addSeparator();
        menuGestion.add(itemMantenimiento);
        
        //menu reportes
        JMenu menuReportes = new JMenu("Reportes");
        JMenuItem itemReporteDrones = new JMenuItem("Reporte de Drones");
        JMenuItem itemReporteEntregas = new JMenuItem("Reporte de Entregas");
        JMenuItem itemReporteMantenimiento = new JMenuItem("Reporte de Mantenimiento");
        
        itemReporteDrones.addActionListener(e -> {
            tabbedPane.setSelectedIndex(3);
            if (panelMantenimiento != null) {
                panelMantenimiento.actualizarDatos();
            }
        });
        itemReporteMantenimiento.addActionListener(e -> {
            tabbedPane.setSelectedIndex(3);
            if (panelMantenimiento != null) {
                panelMantenimiento.actualizarDatos();
            }
        });
        
        menuReportes.add(itemReporteDrones);
        menuReportes.add(itemReporteEntregas);
        menuReportes.addSeparator();
        menuReportes.add(itemReporteMantenimiento);
        
        //menu ayuda
        JMenu menuAyuda = new JMenu("Ayuda");
        JMenuItem itemAcerca = new JMenuItem("Acerca de");
        itemAcerca.addActionListener(this::mostrarAcercaDe);
        menuAyuda.add(itemAcerca);
        
        menuBar.add(menuArchivo);
        menuBar.add(menuGestion);
        menuBar.add(menuReportes);
        menuBar.add(menuAyuda);
        
        setJMenuBar(menuBar);        
    }

    private void crearPanelSuperior() {
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBorder(BorderFactory.createEtchedBorder());
        panelSuperior.setBackground(new Color(240, 248, 255));
        
        // Logo y título
        JLabel lblTitulo = new JLabel("DroneMed - Sistema de Entregas Médicas");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setForeground(new Color(25, 118, 210));
        
        // Información del usuario
        lblUsuarioActual = new JLabel("Usuario: Administrador | Rol: ADMIN");
        lblUsuarioActual.setFont(new Font("Arial", Font.PLAIN, 12));
        
        panelSuperior.add(lblTitulo, BorderLayout.WEST);
        panelSuperior.add(lblUsuarioActual, BorderLayout.EAST);
        
        add(panelSuperior, BorderLayout.NORTH);
    }
    
    private void crearPestanas() {
        tabbedPane = new JTabbedPane();
        
        // Pestaña Gestión de Drones
        formularioDron = new FormularioDron();
        tabbedPane.addTab("Gestión de Drones", formularioDron);
        
        // Pestaña Gestión de Pedidos
        formularioPedido = new FormularioPedido();
        tabbedPane.addTab("Gestión de Pedidos", formularioPedido);
        
        // Pestaña Mapa de Seguimiento
        panelMapa = new PanelMapa();
        tabbedPane.addTab("Mapa de Seguimiento", panelMapa);
        
        // Pestaña Mantenimiento y Reportes
        panelMantenimiento = new PanelMantenimiento();
        tabbedPane.addTab("Mantenimiento y Reportes", panelMantenimiento);
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private JPanel crearPanelDemo(String titulo, String mensaje) {
        JPanel panel = new JPanel(new BorderLayout());
        
        JLabel lblTitulo = new JLabel(titulo, JLabel.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        
        JTextArea textArea = new JTextArea(mensaje + "\n\nEsta funcionalidad estará disponible cuando se configure la base de datos.");
        textArea.setEditable(false);
        textArea.setMargin(new Insets(20, 20, 20, 20));
        
        panel.add(lblTitulo, BorderLayout.NORTH);
        panel.add(new JScrollPane(textArea), BorderLayout.CENTER);
        
        return panel;
    }
    
    // Método para actualizar datos de mantenimiento
    public void actualizarDatosMantenimiento() {
        if (panelMantenimiento != null) {
            panelMantenimiento.actualizarDatos();
        }
    }
    
    private void crearBarraEstado() {
        JPanel barraEstado = new JPanel(new FlowLayout(FlowLayout.LEFT));
        barraEstado.setBorder(BorderFactory.createLoweredBevelBorder());
        
        JLabel lblEstado = new JLabel("Sistema iniciado correctamente");
        JLabel lblHora = new JLabel("Hora: " + java.time.LocalTime.now().toString().substring(0, 8));
        
        barraEstado.add(lblEstado);
        barraEstado.add(Box.createHorizontalGlue());
        barraEstado.add(lblHora);
        
        add(barraEstado, BorderLayout.SOUTH);
    }
    
    private void configurarVentana() {
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        // icono de aplicacion
        try {
            // comentario para color icono FALTA
            // setIconImage(Toolkit.getDefaultToolkit().getImage("icon.png"));
        } catch (Exception e) {
            // Icono por defecto
        }
    }
    
    private void mostrarAcercaDe(ActionEvent e) {
        String mensaje = "DroneMed v1.0\n\n" +
                        "Sistema de Gestión de Entregas Médicas con Drones\n\n" +
                        "Desarrollado como proyecto final de\n" +
                        "Programación Orientada a Objetos\n\n" +
                        "Tecnologías utilizadas:\n" +
                        "• Java Swing\n" +
                        "• SQLite\n" +
                        "• JDBC\n\n" +
                        "© 2024 - Todos los derechos reservados";
        
        JOptionPane.showMessageDialog(this, mensaje, "Acerca de DroneMed", 
                                    JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void setUsuarioActual(String usuario, String rol) {
        lblUsuarioActual.setText("Usuario: " + usuario + " | Rol: " + rol);
    }    
}
