package com.dronemed.vista;

import com.dronemed.modelo.*;
import com.dronemed.controlador.ControladorDron;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class FormularioDron extends JPanel {
    private JTextField txtId, txtModelo, txtCapacidad, txtAutonomia, txtVelocidad;
    private JComboBox<String> cmbTipoCarga, cmbEstado;
    private JTable tablaDrones;
    private DefaultTableModel modeloTabla;
    private ControladorDron controladorDron;
    private JButton btnGuardar, btnActualizar, btnEliminar, btnLimpiar;

    public FormularioDron() {
        controladorDron = new ControladorDron();
        initComponents();
        cargarDronesEnTabla();
        // Aplicar especificaciones iniciales
        aplicarEspecificacionesTipoDron();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // panel de formulario
        JPanel panelFormulario = crearPanelFormulario();
        add(panelFormulario, BorderLayout.NORTH);

        // panel de tabla
        JPanel panelTabla = crearPanelTabla();
        add(panelTabla, BorderLayout.CENTER);

        // panel de botones
        JPanel panelBotones = crearPanelBotones();
        add(panelBotones, BorderLayout.SOUTH);
    }

    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Registro de Drones"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // ID
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1;
        txtId = new JTextField(10);
        panel.add(txtId, gbc);
        
        // Modelo
        gbc.gridx = 2; gbc.gridy = 0;
        panel.add(new JLabel("Modelo:"), gbc);
        gbc.gridx = 3;
        txtModelo = new JTextField(15);
        panel.add(txtModelo, gbc);
        
        // Tipo de Carga
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Tipo de Carga:"), gbc);
        gbc.gridx = 1;
        cmbTipoCarga = new JComboBox<>(new String[]{"LIGERA", "MEDIA", "PESADA"});
        cmbTipoCarga.addActionListener(e -> aplicarEspecificacionesTipoDron());
        panel.add(cmbTipoCarga, gbc);
        
        // Capacidad
        gbc.gridx = 2; gbc.gridy = 1;
        panel.add(new JLabel("Capacidad Máx (kg):"), gbc);
        gbc.gridx = 3;
        txtCapacidad = new JTextField(10);
        txtCapacidad.setEditable(false); // Solo lectura, se establece automáticamente
        txtCapacidad.setBackground(Color.LIGHT_GRAY);
        panel.add(txtCapacidad, gbc);
        
        // Autonomía
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Autonomía (km):"), gbc);
        gbc.gridx = 1;
        txtAutonomia = new JTextField(10);
        panel.add(txtAutonomia, gbc);
        
        // Velocidad
        gbc.gridx = 2; gbc.gridy = 2;
        panel.add(new JLabel("Velocidad Máx (km/h):"), gbc);
        gbc.gridx = 3;
        txtVelocidad = new JTextField(10);
        txtVelocidad.setEditable(false); // Solo lectura, se establece automáticamente
        txtVelocidad.setBackground(Color.LIGHT_GRAY);
        panel.add(txtVelocidad, gbc);
        
        // Estado
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Estado:"), gbc);
        gbc.gridx = 1;
        cmbEstado = new JComboBox<>(new String[]{"DISPONIBLE", "EN_VUELO", "MANTENIMIENTO", "FUERA_SERVICIO"});
        panel.add(cmbEstado, gbc);
        
        return panel;
    }
    
    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Lista de Drones"));

        // crear modelo de tabla
        String[] columnas = {"ID", "Modelo", "Tipo", "Capacidad (kg)", "Autonomia (km)", "Velocidad (km/h)", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // tabla no editable
            }
        };

        tablaDrones = new JTable(modeloTabla);
        tablaDrones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaDrones.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarDronSeleccionado();
            }
        });

        // configurar columnas
        tablaDrones.getColumnModel().getColumn(0).setPreferredWidth(50);
        tablaDrones.getColumnModel().getColumn(1).setPreferredWidth(150);
        tablaDrones.getColumnModel().getColumn(2).setPreferredWidth(80);
        tablaDrones.getColumnModel().getColumn(3).setPreferredWidth(100);
        tablaDrones.getColumnModel().getColumn(4).setPreferredWidth(100);
        tablaDrones.getColumnModel().getColumn(5).setPreferredWidth(100);
        tablaDrones.getColumnModel().getColumn(6).setPreferredWidth(120);
        
        JScrollPane scrollPane = new JScrollPane(tablaDrones);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;    
    }
    
    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout());

        btnGuardar = new JButton(" Guardar");
        btnActualizar = new JButton(" Actualizar");
        btnEliminar = new JButton("Eliminar");
        btnLimpiar = new JButton(" Limpiar");

        // configurar botones
        btnGuardar.addActionListener(this::guardarDron);
        btnActualizar.addActionListener(this::actualizarDron);
        btnEliminar.addActionListener(this::eliminarDron);
        btnLimpiar.addActionListener(this::limpiarFormulario);

        panel.add(btnGuardar);
        panel.add(btnActualizar);
        panel.add(btnEliminar);
        panel.add(btnLimpiar);

        return panel;
    }

    private void guardarDron(ActionEvent e) {
        try {
            if (!validarCampos()) return;

            Dron dron = crearDronDesdeFormulario();

            if (controladorDron.crearDron(dron)) {
                JOptionPane.showMessageDialog(this, "Dron guardado exitosamente", "Exito", JOptionPane.INFORMATION_MESSAGE);
                cargarDronesEnTabla();
                limpiarFormulario(null);
            } else {
                JOptionPane.showMessageDialog(this, "Error al guardar el dron", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarDron(ActionEvent e) {
        try {
            if (!validarCampos()) return;
            
            int filaSeleccionada = tablaDrones.getSelectedRow();
            if (filaSeleccionada == -1) {
                JOptionPane.showMessageDialog(this, "Seleccione un dron para actualizar", 
                                            "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            Dron dron = crearDronDesdeFormulario();
            
            if (controladorDron.actualizarDron(dron)) {
                JOptionPane.showMessageDialog(this, "Dron actualizado exitosamente", 
                                            "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarDronesEnTabla();
                limpiarFormulario(null);
            } else {
                JOptionPane.showMessageDialog(this, "Error al actualizar el dron", 
                                            "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void eliminarDron(ActionEvent e) {
        int filaSeleccionada = tablaDrones.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un dron para eliminar", 
                                        "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String id = modeloTabla.getValueAt(filaSeleccionada, 0).toString();
        
        int confirmacion = JOptionPane.showConfirmDialog(this, 
                "¿Está seguro de eliminar el dron con ID " + id + "?", 
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            if (controladorDron.eliminarDron(id)) {
                JOptionPane.showMessageDialog(this, "Dron eliminado exitosamente", 
                                            "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarDronesEnTabla();
                limpiarFormulario(null);
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar el dron", 
                                            "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void limpiarFormulario(ActionEvent e) {
        txtId.setText("");
        txtModelo.setText("");
        cmbTipoCarga.setSelectedIndex(0);
        cmbEstado.setSelectedIndex(0);
        tablaDrones.clearSelection();
        
        // Aplicar especificaciones del tipo seleccionado
        aplicarEspecificacionesTipoDron();
    }
    
    private boolean validarCampos() {
        if (txtId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El ID es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            txtId.requestFocus();
            return false;
        }
        
        if (txtModelo.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El modelo es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            txtModelo.requestFocus();
            return false;
        }
        
        // El ID puede ser alfanumérico, no necesita validación numérica
        
        try {
            Double.parseDouble(txtCapacidad.getText().trim());
            Double.parseDouble(txtAutonomia.getText().trim());
            Double.parseDouble(txtVelocidad.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Capacidad, autonomía y velocidad deben ser números", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    private Dron crearDronDesdeFormulario() {
        String id = txtId.getText().trim();
        String modelo = txtModelo.getText().trim();
        String tipoCarga = (String) cmbTipoCarga.getSelectedItem();
        double capacidad = Double.parseDouble(txtCapacidad.getText().trim());
        double autonomia = Double.parseDouble(txtAutonomia.getText().trim());
        double velocidad = Double.parseDouble(txtVelocidad.getText().trim());
        String estado = (String) cmbEstado.getSelectedItem();
        
        Dron dron;
        switch (tipoCarga) {
            case "LIGERA":
                dron = new DronCargaLigera(id, modelo, capacidad, autonomia, velocidad);
                break;
            case "MEDIA":
                dron = new DronCargaMedia(id, modelo, capacidad, autonomia, velocidad);
                break;
            case "PESADA":
                dron = new DronCargaPesada(id, modelo, capacidad, autonomia, velocidad);
                break;
            default:
                throw new IllegalArgumentException("Tipo de carga no válido: " + tipoCarga);
        }
        
        dron.setEstado(estado);
        return dron;
    }
    
    private void cargarDronesEnTabla() {
        modeloTabla.setRowCount(0); // Limpiar tabla
        
        List<Dron> drones = controladorDron.obtenerTodosLosDrones();
        for (Dron dron : drones) {
            Object[] fila = {
                dron.getId(),
                dron.getModelo(),
                dron.getTipoCarga(),
                dron.getCapacidadKg(),
                dron.getAutonomiaKm(),
                dron.getVelocidadMaxKmh(),
                dron.getEstado()
            };
            modeloTabla.addRow(fila);
        }
    }
    
    private void cargarDronSeleccionado() {
        int filaSeleccionada = tablaDrones.getSelectedRow();
        if (filaSeleccionada != -1) {
            txtId.setText(modeloTabla.getValueAt(filaSeleccionada, 0).toString());
            txtModelo.setText(modeloTabla.getValueAt(filaSeleccionada, 1).toString());
            cmbTipoCarga.setSelectedItem(modeloTabla.getValueAt(filaSeleccionada, 2));
            
            // Temporalmente habilitar campos para cargar datos existentes
            txtCapacidad.setEditable(true);
            txtVelocidad.setEditable(true);
            
            txtCapacidad.setText(modeloTabla.getValueAt(filaSeleccionada, 3).toString());
            txtAutonomia.setText(modeloTabla.getValueAt(filaSeleccionada, 4).toString());
            txtVelocidad.setText(modeloTabla.getValueAt(filaSeleccionada, 5).toString());
            cmbEstado.setSelectedItem(modeloTabla.getValueAt(filaSeleccionada, 6));
            
            // Volver a deshabilitar campos
            txtCapacidad.setEditable(false);
            txtVelocidad.setEditable(false);
        }
    }
    
    /**
     * Aplica automáticamente las especificaciones técnicas según el tipo de dron seleccionado
     */
    private void aplicarEspecificacionesTipoDron() {
        String tipoSeleccionado = (String) cmbTipoCarga.getSelectedItem();
        
        // Temporalmente habilitar campos para establecer valores
        txtCapacidad.setEditable(true);
        txtVelocidad.setEditable(true);
        
        switch (tipoSeleccionado) {
            case "LIGERA":
                // Especificaciones para drones de carga ligera
                txtCapacidad.setText("2.0");     // Máximo 2kg
                txtVelocidad.setText("80.0");    // Velocidad máxima 80 km/h
                txtAutonomia.setText("25.0");    // Autonomía típica 25km
                break;
                
            case "MEDIA":
                // Especificaciones para drones de carga media
                txtCapacidad.setText("5.0");     // Máximo 5kg
                txtVelocidad.setText("60.0");    // Velocidad máxima 60 km/h
                txtAutonomia.setText("20.0");    // Autonomía típica 20km
                break;
                
            case "PESADA":
                // Especificaciones para drones de carga pesada
                txtCapacidad.setText("10.0");    // Máximo 10kg
                txtVelocidad.setText("45.0");    // Velocidad máxima 45 km/h
                txtAutonomia.setText("15.0");    // Autonomía típica 15km
                break;
                
            default:
                txtCapacidad.setText("0.0");
                txtVelocidad.setText("0.0");
                txtAutonomia.setText("0.0");
                break;
        }
        
        // Volver a deshabilitar campos
        txtCapacidad.setEditable(false);
        txtVelocidad.setEditable(false);
        
        // Mostrar información al usuario
        mostrarInformacionTipoDron(tipoSeleccionado);
    }
    
    /**
     * Muestra información sobre las características del tipo de dron seleccionado
     */
    private void mostrarInformacionTipoDron(String tipo) {
        String mensaje;
        switch (tipo) {
            case "LIGERA":
                mensaje = "Dron de Carga Ligera:\n" +
                         "• Capacidad máxima: 2.0 kg\n" +
                         "• Velocidad máxima: 80 km/h\n" +
                         "• Autonomía típica: 25 km\n" +
                         "• Ideal para: Medicamentos pequeños, muestras";
                break;
            case "MEDIA":
                mensaje = "Dron de Carga Media:\n" +
                         "• Capacidad máxima: 5.0 kg\n" +
                         "• Velocidad máxima: 60 km/h\n" +
                         "• Autonomía típica: 20 km\n" +
                         "• Ideal para: Equipos médicos medianos, suministros";
                break;
            case "PESADA":
                mensaje = "Dron de Carga Pesada:\n" +
                         "• Capacidad máxima: 10.0 kg\n" +
                         "• Velocidad máxima: 45 km/h\n" +
                         "• Autonomía típica: 15 km\n" +
                         "• Ideal para: Equipos médicos pesados, suministros grandes";
                break;
            default:
                return;
        }
        
        // Mostrar tooltip con la información
        cmbTipoCarga.setToolTipText(mensaje);
    }
}