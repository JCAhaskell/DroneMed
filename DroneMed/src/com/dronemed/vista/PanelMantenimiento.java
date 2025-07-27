package com.dronemed.vista;

import com.dronemed.controlador.ControladorMantenimiento;
import com.dronemed.controlador.ControladorDron;
import com.dronemed.modelo.Mantenimiento;
import com.dronemed.modelo.Dron;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class PanelMantenimiento extends JPanel {
    private ControladorMantenimiento controladorMantenimiento;
    private ControladorDron controladorDron;
    
    // Componentes de la interfaz
    private JTabbedPane tabbedPane;
    private JTable tablaMantenimientos;
    private DefaultTableModel modeloTablaMantenimientos;
    private JTable tablaDrones;
    private DefaultTableModel modeloTablaDrones;
    private JTextArea areaReportes;
    private JComboBox<String> comboEstados;
    private JComboBox<String> comboTipos;
    private JLabel lblEstadisticas;
    
    // Formulario de nuevo mantenimiento
    private JComboBox<Dron> comboDrones;
    private JComboBox<String> comboTipoMantenimiento;
    private JTextArea txtDescripcion;
    private JTextField txtTecnico;
    private JTextField txtCosto;
    private JSpinner spinnerFecha;
    
    public PanelMantenimiento() {
        this.controladorMantenimiento = new ControladorMantenimiento();
        this.controladorDron = new ControladorDron();
        
        initComponents();
        setupLayout();
        setupEventListeners();
        cargarDatos();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Crear el panel con pestañas
        tabbedPane = new JTabbedPane();
        
        // Pestaña de Mantenimientos
        JPanel panelMantenimientos = crearPanelMantenimientos();
        tabbedPane.addTab("Mantenimientos", new ImageIcon(), panelMantenimientos, "Gestión de mantenimientos");
        
        // Pestaña de Programar Mantenimiento
        JPanel panelProgramar = crearPanelProgramar();
        tabbedPane.addTab("Programar", new ImageIcon(), panelProgramar, "Programar nuevo mantenimiento");
        
        // Pestaña de Estado de Drones
        JPanel panelDrones = crearPanelDrones();
        tabbedPane.addTab("Estado Drones", new ImageIcon(), panelDrones, "Estado de los drones");
        
        // Pestaña de Reportes
        JPanel panelReportes = crearPanelReportes();
        tabbedPane.addTab("Reportes", new ImageIcon(), panelReportes, "Generar reportes");
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private JPanel crearPanelMantenimientos() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Panel superior con filtros y estadísticas
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        panelSuperior.add(new JLabel("Estado:"));
        comboEstados = new JComboBox<>(new String[]{"TODOS", "PROGRAMADO", "EN_PROGRESO", "COMPLETADO", "CANCELADO"});
        panelSuperior.add(comboEstados);
        
        panelSuperior.add(Box.createHorizontalStrut(20));
        
        JButton btnActualizar = new JButton("Actualizar");
        panelSuperior.add(btnActualizar);
        
        panelSuperior.add(Box.createHorizontalStrut(20));
        
        lblEstadisticas = new JLabel("Cargando estadísticas...");
        panelSuperior.add(lblEstadisticas);
        
        panel.add(panelSuperior, BorderLayout.NORTH);
        
        // Tabla de mantenimientos
        String[] columnas = {"ID", "Dron ID", "Tipo", "Descripción", "Estado", "Técnico", "Fecha Programada", "Costo"};
        modeloTablaMantenimientos = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaMantenimientos = new JTable(modeloTablaMantenimientos);
        tablaMantenimientos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaMantenimientos.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        JScrollPane scrollPane = new JScrollPane(tablaMantenimientos);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Panel inferior con botones de acción
        JPanel panelInferior = new JPanel(new FlowLayout());
        
        JButton btnIniciar = new JButton("Iniciar Mantenimiento");
        JButton btnCompletar = new JButton("Completar Mantenimiento");
        JButton btnCancelar = new JButton("Cancelar Mantenimiento");
        
        panelInferior.add(btnIniciar);
        panelInferior.add(btnCompletar);
        panelInferior.add(btnCancelar);
        
        panel.add(panelInferior, BorderLayout.SOUTH);
        
        // Event listeners
        btnActualizar.addActionListener(e -> cargarMantenimientos());
        comboEstados.addActionListener(e -> cargarMantenimientos());
        
        btnIniciar.addActionListener(e -> iniciarMantenimiento());
        btnCompletar.addActionListener(e -> completarMantenimiento());
        btnCancelar.addActionListener(e -> cancelarMantenimiento());
        
        return panel;
    }
    
    private JPanel crearPanelProgramar() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Formulario
        JPanel formulario = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Dron
        gbc.gridx = 0; gbc.gridy = 0;
        formulario.add(new JLabel("Dron:"), gbc);
        gbc.gridx = 1;
        comboDrones = new JComboBox<>();
        comboDrones.setPreferredSize(new Dimension(200, 25));
        formulario.add(comboDrones, gbc);
        
        // Tipo de mantenimiento
        gbc.gridx = 0; gbc.gridy = 1;
        formulario.add(new JLabel("Tipo:"), gbc);
        gbc.gridx = 1;
        comboTipoMantenimiento = new JComboBox<>(new String[]{
            "PREVENTIVO", "CORRECTIVO", "PREDICTIVO", "EMERGENCIA"
        });
        comboTipoMantenimiento.setPreferredSize(new Dimension(200, 25));
        formulario.add(comboTipoMantenimiento, gbc);
        
        // Descripción
        gbc.gridx = 0; gbc.gridy = 2;
        formulario.add(new JLabel("Descripción:"), gbc);
        gbc.gridx = 1;
        txtDescripcion = new JTextArea(3, 20);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        JScrollPane scrollDesc = new JScrollPane(txtDescripcion);
        formulario.add(scrollDesc, gbc);
        
        // Técnico responsable
        gbc.gridx = 0; gbc.gridy = 3;
        formulario.add(new JLabel("Técnico:"), gbc);
        gbc.gridx = 1;
        txtTecnico = new JTextField(20);
        formulario.add(txtTecnico, gbc);
        
        // Fecha programada
        gbc.gridx = 0; gbc.gridy = 4;
        formulario.add(new JLabel("Fecha programada:"), gbc);
        gbc.gridx = 1;
        spinnerFecha = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinnerFecha, "dd/MM/yyyy HH:mm");
        spinnerFecha.setEditor(editor);
        spinnerFecha.setPreferredSize(new Dimension(200, 25));
        formulario.add(spinnerFecha, gbc);
        
        // Costo estimado
        gbc.gridx = 0; gbc.gridy = 5;
        formulario.add(new JLabel("Costo estimado:"), gbc);
        gbc.gridx = 1;
        txtCosto = new JTextField(20);
        formulario.add(txtCosto, gbc);
        
        // Botones
        gbc.gridx = 1; gbc.gridy = 6;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel panelBotones = new JPanel(new FlowLayout());
        JButton btnProgramar = new JButton("Programar Mantenimiento");
        JButton btnLimpiar = new JButton("Limpiar");
        panelBotones.add(btnProgramar);
        panelBotones.add(btnLimpiar);
        formulario.add(panelBotones, gbc);
        
        panel.add(formulario, BorderLayout.NORTH);
        
        // Event listeners
        btnProgramar.addActionListener(e -> programarMantenimiento());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        
        return panel;
    }
    
    private JPanel crearPanelDrones() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Panel superior
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnActualizarDrones = new JButton("Actualizar Estado");
        panelSuperior.add(btnActualizarDrones);
        panel.add(panelSuperior, BorderLayout.NORTH);
        
        // Tabla de drones
        String[] columnasDrones = {"ID", "Modelo", "Estado", "Batería", "Último Mantenimiento", "Días desde Mant.", "Requiere Mant."};
        modeloTablaDrones = new DefaultTableModel(columnasDrones, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaDrones = new JTable(modeloTablaDrones);
        tablaDrones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPaneDrones = new JScrollPane(tablaDrones);
        panel.add(scrollPaneDrones, BorderLayout.CENTER);
        
        // Event listeners
        btnActualizarDrones.addActionListener(e -> cargarDrones());
        
        return panel;
    }
    
    private JPanel crearPanelReportes() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Panel superior con botones
        JPanel panelSuperior = new JPanel(new FlowLayout());
        JButton btnReporteMantenimientos = new JButton("Reporte de Mantenimientos");
        JButton btnReporteDrones = new JButton("Reporte de Drones");
        
        panelSuperior.add(btnReporteMantenimientos);
        panelSuperior.add(btnReporteDrones);
        
        panel.add(panelSuperior, BorderLayout.NORTH);
        
        // Área de texto para mostrar reportes
        areaReportes = new JTextArea();
        areaReportes.setEditable(false);
        areaReportes.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        
        JScrollPane scrollReportes = new JScrollPane(areaReportes);
        panel.add(scrollReportes, BorderLayout.CENTER);
        
        // Event listeners
        btnReporteMantenimientos.addActionListener(e -> generarReporteMantenimientos());
        btnReporteDrones.addActionListener(e -> generarReporteDrones());
        
        return panel;
    }
    
    private void setupLayout() {
        setBorder(BorderFactory.createTitledBorder("Mantenimiento y Reportes"));
    }
    
    private void setupEventListeners() {
        // Los event listeners ya están configurados en cada panel
    }
    
    private void cargarDatos() {
        cargarDrones();
        cargarMantenimientos();
        cargarEstadisticas();
        cargarDronesCombo();
    }
    
    private void cargarMantenimientos() {
        modeloTablaMantenimientos.setRowCount(0);
        
        List<Mantenimiento> mantenimientos;
        String estadoSeleccionado = (String) comboEstados.getSelectedItem();
        
        if ("TODOS".equals(estadoSeleccionado)) {
            mantenimientos = controladorMantenimiento.obtenerTodosLosMantenimientos();
        } else {
            mantenimientos = controladorMantenimiento.obtenerMantenimientosPorEstado(estadoSeleccionado);
        }
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        for (Mantenimiento m : mantenimientos) {
            Object[] fila = {
                m.getId(),
                m.getDronId(),
                m.getTipoMantenimiento(),
                m.getDescripcion(),
                m.getEstado(),
                m.getTecnicoResponsable(),
                m.getFechaInicio() != null ? m.getFechaInicio().format(formatter) : "N/A",
                String.format("$%.2f", m.getCosto())
            };
            modeloTablaMantenimientos.addRow(fila);
        }
        
        cargarEstadisticas();
    }
    
    private void cargarDrones() {
        modeloTablaDrones.setRowCount(0);
        
        List<Dron> drones = controladorDron.obtenerTodosLosDrones();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        for (Dron dron : drones) {
            Object[] fila = {
                dron.getId(),
                dron.getModelo(),
                dron.getEstado(),
                dron.getNivelBateria() + "%",
                dron.getUltimoMantenimiento() != null ? 
                    dron.getUltimoMantenimiento().format(formatter) : "N/A",
                dron.getDiasDesdeUltimoMantenimiento(),
                dron.requiereMantenimiento() ? "SÍ" : "NO"
            };
            modeloTablaDrones.addRow(fila);
        }
    }
    
    private void cargarEstadisticas() {
        Map<String, Object> estadisticas = controladorMantenimiento.obtenerEstadisticas();
        
        StringBuilder texto = new StringBuilder();
        texto.append("Total: ").append(estadisticas.get("total"));
        texto.append(" | Costo total: $").append(String.format("%.2f", estadisticas.get("costoTotal")));
        
        lblEstadisticas.setText(texto.toString());
    }
    
    private void cargarDronesCombo() {
        comboDrones.removeAllItems();
        List<Dron> drones = controladorDron.obtenerTodosLosDrones();
        
        for (Dron dron : drones) {
            comboDrones.addItem(dron);
        }
    }
    
    private void programarMantenimiento() {
        try {
            Dron dronSeleccionado = (Dron) comboDrones.getSelectedItem();
            if (dronSeleccionado == null) {
                JOptionPane.showMessageDialog(this, "Seleccione un dron", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String tipo = (String) comboTipoMantenimiento.getSelectedItem();
            String descripcion = txtDescripcion.getText().trim();
            String tecnico = txtTecnico.getText().trim();
            String costoTexto = txtCosto.getText().trim();
            
            if (descripcion.isEmpty() || tecnico.isEmpty() || costoTexto.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Complete todos los campos", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            double costo = Double.parseDouble(costoTexto);
            java.util.Date fecha = (java.util.Date) spinnerFecha.getValue();
            LocalDateTime fechaProgramada = LocalDateTime.ofInstant(fecha.toInstant(), java.time.ZoneId.systemDefault());
            
            boolean exito = controladorMantenimiento.programarMantenimiento(
                dronSeleccionado.getId(), tipo, descripcion, fechaProgramada, tecnico, costo
            );
            
            if (exito) {
                JOptionPane.showMessageDialog(this, "Mantenimiento programado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                limpiarFormulario();
                cargarMantenimientos();
                cargarDrones();
            } else {
                JOptionPane.showMessageDialog(this, "Error al programar mantenimiento", "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El costo debe ser un número válido", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void limpiarFormulario() {
        txtDescripcion.setText("");
        txtTecnico.setText("");
        txtCosto.setText("");
        spinnerFecha.setValue(new java.util.Date());
        comboTipoMantenimiento.setSelectedIndex(0);
    }
    
    private void iniciarMantenimiento() {
        int filaSeleccionada = tablaMantenimientos.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un mantenimiento", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int id = (Integer) modeloTablaMantenimientos.getValueAt(filaSeleccionada, 0);
        String estado = (String) modeloTablaMantenimientos.getValueAt(filaSeleccionada, 4);
        
        if (!"PROGRAMADO".equals(estado)) {
            JOptionPane.showMessageDialog(this, "Solo se pueden iniciar mantenimientos programados", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        boolean exito = controladorMantenimiento.iniciarMantenimiento(id);
        
        if (exito) {
            JOptionPane.showMessageDialog(this, "Mantenimiento iniciado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cargarMantenimientos();
            cargarDrones();
        } else {
            JOptionPane.showMessageDialog(this, "Error al iniciar mantenimiento", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void completarMantenimiento() {
        int filaSeleccionada = tablaMantenimientos.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un mantenimiento", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int id = (Integer) modeloTablaMantenimientos.getValueAt(filaSeleccionada, 0);
        String estado = (String) modeloTablaMantenimientos.getValueAt(filaSeleccionada, 4);
        
        if (!"EN_PROGRESO".equals(estado)) {
            JOptionPane.showMessageDialog(this, "Solo se pueden completar mantenimientos en progreso", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Diálogo para observaciones y costo final
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        JTextArea txtObservaciones = new JTextArea(3, 20);
        JTextField txtCostoFinal = new JTextField();
        
        panel.add(new JLabel("Observaciones:"));
        panel.add(new JScrollPane(txtObservaciones));
        panel.add(new JLabel("Costo final:"));
        panel.add(txtCostoFinal);
        
        int resultado = JOptionPane.showConfirmDialog(this, panel, "Completar Mantenimiento", JOptionPane.OK_CANCEL_OPTION);
        
        if (resultado == JOptionPane.OK_OPTION) {
            try {
                String observaciones = txtObservaciones.getText().trim();
                double costoFinal = Double.parseDouble(txtCostoFinal.getText().trim());
                
                boolean exito = controladorMantenimiento.completarMantenimiento(id, observaciones, costoFinal);
                
                if (exito) {
                    JOptionPane.showMessageDialog(this, "Mantenimiento completado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    cargarMantenimientos();
                    cargarDrones();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al completar mantenimiento", "Error", JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "El costo debe ser un número válido", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void cancelarMantenimiento() {
        int filaSeleccionada = tablaMantenimientos.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un mantenimiento", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int id = (Integer) modeloTablaMantenimientos.getValueAt(filaSeleccionada, 0);
        String estado = (String) modeloTablaMantenimientos.getValueAt(filaSeleccionada, 4);
        
        if ("COMPLETADO".equals(estado) || "CANCELADO".equals(estado)) {
            JOptionPane.showMessageDialog(this, "No se puede cancelar un mantenimiento completado o ya cancelado", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String motivo = JOptionPane.showInputDialog(this, "Motivo de cancelación:", "Cancelar Mantenimiento", JOptionPane.QUESTION_MESSAGE);
        
        if (motivo != null && !motivo.trim().isEmpty()) {
            boolean exito = controladorMantenimiento.cancelarMantenimiento(id, motivo);
            
            if (exito) {
                JOptionPane.showMessageDialog(this, "Mantenimiento cancelado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarMantenimientos();
                cargarDrones();
            } else {
                JOptionPane.showMessageDialog(this, "Error al cancelar mantenimiento", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void generarReporteMantenimientos() {
        // Solicitar período de fechas
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        JSpinner spinnerInicio = new JSpinner(new SpinnerDateModel());
        JSpinner spinnerFin = new JSpinner(new SpinnerDateModel());
        
        JSpinner.DateEditor editorInicio = new JSpinner.DateEditor(spinnerInicio, "dd/MM/yyyy");
        JSpinner.DateEditor editorFin = new JSpinner.DateEditor(spinnerFin, "dd/MM/yyyy");
        spinnerInicio.setEditor(editorInicio);
        spinnerFin.setEditor(editorFin);
        
        panel.add(new JLabel("Fecha inicio:"));
        panel.add(spinnerInicio);
        panel.add(new JLabel("Fecha fin:"));
        panel.add(spinnerFin);
        
        int resultado = JOptionPane.showConfirmDialog(this, panel, "Período del Reporte", JOptionPane.OK_CANCEL_OPTION);
        
        if (resultado == JOptionPane.OK_OPTION) {
            java.util.Date fechaInicio = (java.util.Date) spinnerInicio.getValue();
            java.util.Date fechaFin = (java.util.Date) spinnerFin.getValue();
            
            LocalDateTime inicio = LocalDateTime.ofInstant(fechaInicio.toInstant(), java.time.ZoneId.systemDefault());
            LocalDateTime fin = LocalDateTime.ofInstant(fechaFin.toInstant(), java.time.ZoneId.systemDefault());
            
            String reporte = controladorMantenimiento.generarReporteMantenimientos(inicio, fin);
            areaReportes.setText(reporte);
            areaReportes.setCaretPosition(0);
        }
    }
    
    private void generarReporteDrones() {
        String reporte = controladorMantenimiento.generarReporteDrones();
        areaReportes.setText(reporte);
        areaReportes.setCaretPosition(0);
    }
    

    
    // Método público para actualizar datos desde VentanaPrincipal
    public void actualizarDatos() {
        cargarDatos();
    }
}