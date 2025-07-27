package com.dronemed.vista;

import com.dronemed.modelo.*;
import com.dronemed.modelo.DronConcreto;
import com.dronemed.controlador.ControladorPedido;
import com.dronemed.controlador.ControladorCliente;
import com.dronemed.controlador.ControladorDron;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FormularioPedido extends JPanel {
    private JTextField txtId, txtDescripcion, txtPeso, txtDistancia;
    private JComboBox<String> cmbPrioridad, cmbCliente;
    private JComboBox<Dron> cmbDronAsignado;
    private JTextArea txtObservaciones;
    private JTable tablaPedidos;
    private DefaultTableModel modeloTabla;
    private ControladorPedido controladorPedido;
    private ControladorCliente controladorCliente;
    private ControladorDron controladorDron;
    private JButton btnGuardar, btnActualizar, btnEliminar, btnLimpiar, btnAsignarDron;
    private JLabel lblTiempoEstimado, lblCostoEstimado;
    
    public FormularioPedido() {
        controladorPedido = new ControladorPedido();
        controladorCliente = new ControladorCliente();
        controladorDron = new ControladorDron();
        initComponents();
        cargarPedidosEnTabla();
        cargarClientesEnCombo();
        cargarDronesEnCombo();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Panel principal con scroll
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        
        // Panel de formulario
        JPanel panelFormulario = crearPanelFormulario();
        panelPrincipal.add(panelFormulario, BorderLayout.NORTH);
        
        // Panel de tabla
        JPanel panelTabla = crearPanelTabla();
        panelPrincipal.add(panelTabla, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel panelBotones = crearPanelBotones();
        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);
        
        JScrollPane scrollPane = new JScrollPane(panelPrincipal);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Registro de Pedidos"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Fila 1: ID y Cliente
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("ID Pedido:"), gbc);
        gbc.gridx = 1;
        txtId = new JTextField(10);
        txtId.setEditable(false);
        panel.add(txtId, gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel("Cliente:"), gbc);
        gbc.gridx = 3;
        cmbCliente = new JComboBox<>();
        cmbCliente.setPreferredSize(new Dimension(200, 25));
        cmbCliente.addActionListener(e -> actualizarDistanciaAutomatica());
        panel.add(cmbCliente, gbc);
        
        // Fila 2: Descripcion y Prioridad
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Descripcion:"), gbc);
        gbc.gridx = 1;
        txtDescripcion = new JTextField(15);
        panel.add(txtDescripcion, gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel("Prioridad:"), gbc);
        gbc.gridx = 3;
        cmbPrioridad = new JComboBox<>(new String[]{"URGENTE", "ESTANDAR"});
        cmbPrioridad.setPreferredSize(new Dimension(200, 25));
        panel.add(cmbPrioridad, gbc);
        
        // Fila 3: Peso y Distancia
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Peso (kg):"), gbc);
        gbc.gridx = 1;
        txtPeso = new JTextField(15);
        panel.add(txtPeso, gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel("Distancia (km):"), gbc);
        gbc.gridx = 3;
        txtDistancia = new JTextField(15);
        txtDistancia.setEditable(false); // Se calcula automaticamente
        panel.add(txtDistancia, gbc);
        
        // Fila 4: Dron Asignado
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Dron Asignado:"), gbc);
        gbc.gridx = 1;
        cmbDronAsignado = new JComboBox<>();
        cmbDronAsignado.setPreferredSize(new Dimension(200, 25));
        panel.add(cmbDronAsignado, gbc);
        

        
        // Fila 5: Estimaciones
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 1;
        panel.add(new JLabel("Tiempo Estimado:"), gbc);
        gbc.gridx = 1;
        lblTiempoEstimado = new JLabel("-- minutos");
        lblTiempoEstimado.setForeground(Color.BLUE);
        panel.add(lblTiempoEstimado, gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel("Costo Estimado:"), gbc);
        gbc.gridx = 3;
        lblCostoEstimado = new JLabel("$-- USD");
        lblCostoEstimado.setForeground(Color.GREEN);
        panel.add(lblCostoEstimado, gbc);
        
        // Fila 6: Observaciones
        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("Observaciones:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        txtObservaciones = new JTextArea(3, 30);
        txtObservaciones.setLineWrap(true);
        txtObservaciones.setWrapStyleWord(true);
        JScrollPane scrollObs = new JScrollPane(txtObservaciones);
        scrollObs.setPreferredSize(new Dimension(400, 60));
        panel.add(scrollObs, gbc);
        
        // Agregar listeners para calculos automaticos
        txtDistancia.addActionListener(e -> calcularEstimaciones());
        cmbDronAsignado.addActionListener(e -> calcularEstimaciones());
        txtPeso.addActionListener(e -> calcularEstimaciones());
        
        // Agregar DocumentListener para calcular estimaciones en tiempo real
        txtPeso.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { calcularEstimaciones(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { calcularEstimaciones(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { calcularEstimaciones(); }
        });
        
        return panel;
    }
 
    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Lista de Pedidos"));
        
        // Crear modelo de tabla
        String[] columnas = {"ID", "Cliente", "Descripcion", "Prioridad", "Estado", 
                           "Peso (kg)", "Distancia (km)", "Dron Asignado", "Fecha Creacion"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hacer tabla no editable
            }
        };
        
        tablaPedidos = new JTable(modeloTabla);
        tablaPedidos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaPedidos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarPedidoSeleccionado();
            }
        });
       
        // Configurar anchos de columnas
        tablaPedidos.getColumnModel().getColumn(0).setPreferredWidth(60);
        tablaPedidos.getColumnModel().getColumn(1).setPreferredWidth(150);
        tablaPedidos.getColumnModel().getColumn(2).setPreferredWidth(200);
        tablaPedidos.getColumnModel().getColumn(3).setPreferredWidth(80);
        tablaPedidos.getColumnModel().getColumn(4).setPreferredWidth(100);
        tablaPedidos.getColumnModel().getColumn(5).setPreferredWidth(80);
        tablaPedidos.getColumnModel().getColumn(6).setPreferredWidth(100);
        tablaPedidos.getColumnModel().getColumn(7).setPreferredWidth(150);
        tablaPedidos.getColumnModel().getColumn(8).setPreferredWidth(120);
        
        JScrollPane scrollPane = new JScrollPane(tablaPedidos);
        scrollPane.setPreferredSize(new Dimension(800, 200));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout());
        
        btnGuardar = new JButton("Guardar");
        btnActualizar = new JButton("Actualizar");
        btnEliminar = new JButton("Eliminar");
        btnLimpiar = new JButton("Limpiar");
        btnAsignarDron = new JButton("Asignar Dron Automatico");
        
        // Configurar colores
        btnGuardar.setBackground(new Color(76, 175, 80));
        btnGuardar.setForeground(Color.WHITE);
        btnActualizar.setBackground(new Color(33, 150, 243));
        btnActualizar.setForeground(Color.WHITE);
        btnEliminar.setBackground(new Color(244, 67, 54));
        btnEliminar.setForeground(Color.WHITE);
        btnAsignarDron.setBackground(new Color(255, 152, 0));
        btnAsignarDron.setForeground(Color.WHITE);
        
        // Agregar listeners
        btnGuardar.addActionListener(e -> guardarPedido());
        btnActualizar.addActionListener(e -> actualizarPedido());
        btnEliminar.addActionListener(e -> eliminarPedido());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        btnAsignarDron.addActionListener(e -> asignarDronAutomatico());
        
        panel.add(btnGuardar);
        panel.add(btnActualizar);
        panel.add(btnEliminar);
        panel.add(btnLimpiar);
        panel.add(btnAsignarDron);
        
        return panel;
    }
    
    private void guardarPedido() {
        if (validarCampos()) {
            try {
                Pedido pedido = crearPedidoDesdeFormulario();
                boolean exito = controladorPedido.crearPedido(
                    pedido.getClienteId(),
                    pedido.getMedicamento(),
                    pedido.getDescripcion(),
                    pedido.getPeso(),
                    pedido.getDireccionEntrega(),
                    pedido.getLatitud(),
                    pedido.getLongitud()
                );
                
                if (exito) {
                    JOptionPane.showMessageDialog(this, 
                        "Pedido guardado exitosamente", 
                        "Exito", 
                        JOptionPane.INFORMATION_MESSAGE);
                    limpiarFormulario();
                    cargarPedidosEnTabla();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Error al guardar el pedido", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error inesperado: " + e.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void actualizarPedido() {
        if (txtId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Seleccione un pedido de la tabla para actualizar", 
                "Advertencia", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (validarCampos()) {
            try {
                Pedido pedido = crearPedidoDesdeFormulario();
                boolean exito = controladorPedido.actualizarPedido(
                    pedido.getId(),
                    pedido.getClienteId(),
                    pedido.getMedicamento(),
                    pedido.getDescripcion(),
                    pedido.getPeso(),
                    pedido.getDireccionEntrega(),
                    pedido.getLatitud(),
                    pedido.getLongitud(),
                    pedido.getEstado(),
                    pedido.getPrioridad()
                );
                
                if (exito) {
                    JOptionPane.showMessageDialog(this, 
                        "Pedido actualizado exitosamente", 
                        "Exito", 
                        JOptionPane.INFORMATION_MESSAGE);
                    limpiarFormulario();
                    cargarPedidosEnTabla();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Error al actualizar el pedido", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error inesperado: " + e.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void eliminarPedido() {
        if (txtId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Seleccione un pedido de la tabla para eliminar", 
                "Advertencia", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirmacion = JOptionPane.showConfirmDialog(this, 
            "Esta seguro de que desea eliminar este pedido?", 
            "Confirmar Eliminacion", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                String id = txtId.getText().trim();
                boolean exito = controladorPedido.eliminarPedido(Integer.parseInt(id));
                
                if (exito) {
                    JOptionPane.showMessageDialog(this, 
                        "Pedido eliminado exitosamente", 
                        "Exito", 
                        JOptionPane.INFORMATION_MESSAGE);
                    limpiarFormulario();
                    cargarPedidosEnTabla();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Error al eliminar el pedido", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error inesperado: " + e.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void limpiarFormulario() {
        txtId.setText("");
        txtDescripcion.setText("");
        txtPeso.setText("");
        txtDistancia.setText("");
        txtObservaciones.setText("");
        cmbCliente.setSelectedIndex(0);
        cmbPrioridad.setSelectedIndex(0);
        cmbDronAsignado.setSelectedIndex(0);
        lblTiempoEstimado.setText("-- minutos");
        lblCostoEstimado.setText("$-- USD");
        tablaPedidos.clearSelection();
    }
    
    private void asignarDronAutomatico() {
        if (txtPeso.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Ingrese el peso del pedido para asignar un dron automaticamente", 
                "Informacion Requerida", 
                JOptionPane.INFORMATION_MESSAGE);
            txtPeso.requestFocus();
            return;
        }
        
        try {
            double peso = Double.parseDouble(txtPeso.getText().trim());
            asignarDronAutomaticamente(peso);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Ingrese valores numericos validos para peso y distancia", 
                "Error de Formato", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void asignarDronAutomaticamente(double peso) {
        try {
            List<Dron> dronesDisponibles = controladorDron.obtenerDronesDisponibles();
            Dron dronSeleccionado = null;
            
            // Buscar el dron más adecuado según el peso
            for (Dron dron : dronesDisponibles) {
                if (dron.getCapacidadKg() >= peso) {
                     if (dronSeleccionado == null || dron.getCapacidadKg() < dronSeleccionado.getCapacidadKg()) {
                        dronSeleccionado = dron;
                    }
                }
            }
            
            if (dronSeleccionado != null) {
                // Buscar y seleccionar el dron en el combo
                for (int i = 1; i < cmbDronAsignado.getItemCount(); i++) {
                    Dron dron = (Dron) cmbDronAsignado.getItemAt(i);
                    if (dron.getId().equals(dronSeleccionado.getId())) {
                        cmbDronAsignado.setSelectedIndex(i);
                        break;
                    }
                }
                
                calcularEstimaciones();
                
                JOptionPane.showMessageDialog(this, 
                     "Dron asignado automaticamente: " + dronSeleccionado.getModelo() + 
                     "\nCapacidad: " + dronSeleccionado.getCapacidadKg() + " kg", 
                    "Asignacion Automatica", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "No hay drones disponibles con capacidad suficiente para este peso (" + peso + " kg)", 
                    "Sin Drones Disponibles", 
                    JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error al asignar dron automaticamente: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void calcularEstimaciones() {
        try {
            // Siempre intentar calcular si hay distancia (incluso si es 0)
            String distanciaTexto = txtDistancia.getText().trim();
            String pesoTexto = txtPeso.getText().trim();
            
            if (!distanciaTexto.isEmpty()) {
                double distancia = Double.parseDouble(distanciaTexto);
                double peso = 0.0;
                
                // Si hay peso, usarlo; si no, usar peso por defecto de 1 kg
                if (!pesoTexto.isEmpty()) {
                    peso = Double.parseDouble(pesoTexto);
                } else {
                    peso = 1.0; // Peso por defecto para mostrar estimación
                }
                
                // Calculo simplificado del tiempo (velocidad promedio 60 km/h)
                double tiempoHoras = distancia / 60.0;
                int tiempoMinutos = Math.max(5, (int) (tiempoHoras * 60)); // Mínimo 5 minutos
                
                // Calculo simplificado del costo
                double costoBase = 10.0; // $10 base
                double costoPorKm = 2.0; // $2 por km
                double costoPorKg = 1.5; // $1.5 por kg
                double costoTotal = costoBase + (distancia * costoPorKm) + (peso * costoPorKg);
                
                lblTiempoEstimado.setText(tiempoMinutos + " minutos");
                lblCostoEstimado.setText(String.format("$%.2f USD", costoTotal));
            } else {
                lblTiempoEstimado.setText("-- minutos");
                lblCostoEstimado.setText("$-- USD");
            }
        } catch (NumberFormatException e) {
            lblTiempoEstimado.setText("-- minutos");
            lblCostoEstimado.setText("$-- USD");
        }
    }
    
    private boolean validarCampos() {
        if (txtDescripcion.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "La descripcion es obligatoria", "Campo Requerido", JOptionPane.WARNING_MESSAGE);
            txtDescripcion.requestFocus();
            return false;
        }
        
        if (cmbCliente.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente", "Campo Requerido", JOptionPane.WARNING_MESSAGE);
            cmbCliente.requestFocus();
            return false;
        }
        
        try {
            if (!txtPeso.getText().trim().isEmpty()) {
                double peso = Double.parseDouble(txtPeso.getText().trim());
                if (peso <= 0) {
                    throw new NumberFormatException("El peso debe ser mayor a 0");
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingrese un peso valido (mayor a 0)", "Formato Incorrecto", JOptionPane.WARNING_MESSAGE);
            txtPeso.requestFocus();
            return false;
        }
        
        // La distancia se calcula automáticamente cuando se selecciona un cliente
        // Siempre actualizamos la distancia automáticamente si hay un cliente seleccionado
        if (cmbCliente.getSelectedIndex() > 0) {
            actualizarDistanciaAutomatica();
        }
        
        // No validamos la distancia ya que se calcula automáticamente y siempre será válida
        
        return true;
    }
    
    private Pedido crearPedidoDesdeFormulario() {
        Pedido pedido = new Pedido();
        
        // ID se genera automáticamente, no se toma del formulario
        
        pedido.setDescripcion(txtDescripcion.getText().trim());
        pedido.setPrioridad(cmbPrioridad.getSelectedItem().toString());
        pedido.setEstado("Pendiente"); // Estado automático inicial
        
        if (cmbCliente.getSelectedIndex() > 0) {
            try {
                String clienteSeleccionado = cmbCliente.getSelectedItem().toString();
                String[] partes = clienteSeleccionado.split(" - ");
                if (partes.length > 0) {
                    String idCliente = partes[0].replace("CLI", "");
                    pedido.setClienteId(Integer.parseInt(idCliente));
                } else {
                    throw new IllegalArgumentException("Formato de cliente inválido: " + clienteSeleccionado);
                }
            } catch (Exception e) {
                System.err.println("Error al procesar cliente seleccionado: " + e.getMessage());
                throw new RuntimeException("Error al procesar el cliente seleccionado", e);
            }
        }
        
        if (!txtPeso.getText().trim().isEmpty()) {
            pedido.setPeso(Double.parseDouble(txtPeso.getText().trim()));
        }
        
        // Distancia se calcula automáticamente (no se guarda en el modelo actual)
        
        // Dirección de entrega se obtiene del cliente seleccionado
        if (cmbCliente.getSelectedIndex() > 0) {
            try {
                String clienteSeleccionado = cmbCliente.getSelectedItem().toString();
                String[] partes = clienteSeleccionado.split(" - ");
                if (partes.length > 0) {
                    String idCliente = partes[0].replace("CLI", "");
                    Cliente cliente = controladorCliente.obtenerCliente(Integer.parseInt(idCliente));
                    if (cliente != null) {
                        pedido.setDireccionEntrega(cliente.getDireccion());
                    }
                } else {
                    pedido.setDireccionEntrega("");
                }
            } catch (Exception e) {
                pedido.setDireccionEntrega("");
                System.err.println("Error al obtener dirección del cliente: " + e.getMessage());                
            }
        }
        
        if (cmbDronAsignado.getSelectedIndex() > 0) {
            Dron dronSeleccionado = (Dron) cmbDronAsignado.getSelectedItem();
            pedido.setDronAsignado(Integer.parseInt(dronSeleccionado.getId()));
        }
        
        pedido.setFechaPedido(LocalDateTime.now());
        pedido.setMedicamento(txtDescripcion.getText().trim());
        
        return pedido;
    }
    
    private void cargarPedidosEnTabla() {
        modeloTabla.setRowCount(0);
        
        List<Pedido> pedidos = controladorPedido.obtenerTodosLosPedidos();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        for (Pedido pedido : pedidos) {
            Object[] fila = {
                pedido.getId(),
                "CLI" + String.format("%03d", pedido.getClienteId()),
                pedido.getDescripcion(),
                pedido.getPrioridad(),
                pedido.getEstado(),
                pedido.getPeso(),
                "N/A", // Distancia no disponible en modelo actual
                pedido.getDronAsignado() != 0 ? "DRN" + String.format("%03d", pedido.getDronAsignado()) : "Sin asignar",
                pedido.getFechaPedido() != null ? pedido.getFechaPedido().format(formatter) : ""
            };
            modeloTabla.addRow(fila);
        }
    }
    
    private void cargarClientesEnCombo() {
        cmbCliente.removeAllItems();
        cmbCliente.addItem("-- Seleccionar Cliente --");
        
        try {
            List<Cliente> clientes = controladorCliente.obtenerTodosLosClientes();
            for (Cliente cliente : clientes) {
                String item = String.format("CLI%03d - %s", cliente.getId(), cliente.getNombre());
                cmbCliente.addItem(item);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error al cargar clientes: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cargarDronesEnCombo() {
        cmbDronAsignado.removeAllItems();
        cmbDronAsignado.addItem(new DronConcreto(0, "-- Seleccionar Dron --", "", "", 0, 0, 0, "", true));
        
        try {
            List<Dron> drones = controladorDron.obtenerDronesDisponibles();
            for (Dron dron : drones) {
                cmbDronAsignado.addItem(dron);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error al cargar drones: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cargarPedidoSeleccionado() {
        int filaSeleccionada = tablaPedidos.getSelectedRow();
        if (filaSeleccionada != -1) {
            // ID se muestra pero no es editable
            txtId.setText(modeloTabla.getValueAt(filaSeleccionada, 0).toString());
            
            // Buscar y seleccionar cliente
            String idCliente = modeloTabla.getValueAt(filaSeleccionada, 1).toString();
            for (int i = 0; i < cmbCliente.getItemCount(); i++) {
                if (cmbCliente.getItemAt(i).toString().startsWith(idCliente)) {
                    cmbCliente.setSelectedIndex(i);
                    break;
                }
            }
            
            txtDescripcion.setText(modeloTabla.getValueAt(filaSeleccionada, 2).toString());
            cmbPrioridad.setSelectedItem(modeloTabla.getValueAt(filaSeleccionada, 3));
            txtPeso.setText(modeloTabla.getValueAt(filaSeleccionada, 5).toString());
            
            // Calcular distancia automáticamente basada en el cliente
            if (cmbCliente.getSelectedIndex() > 0) {
                actualizarDistanciaAutomatica();
            }
            
            // Buscar y seleccionar dron
            String idDronStr = modeloTabla.getValueAt(filaSeleccionada, 7).toString();
            if (!idDronStr.equals("Sin asignar")) {
                try {
                    String idDron = idDronStr.replace("DRN", "").replaceAll("^0+", "");
                    int dronId = Integer.parseInt(idDron);
                    
                    for (int i = 1; i < cmbDronAsignado.getItemCount(); i++) {
                        Dron dron = (Dron) cmbDronAsignado.getItemAt(i);
                        if (Integer.parseInt(dron.getId()) == dronId) {
                            cmbDronAsignado.setSelectedIndex(i);
                            break;
                        }
                    }
                } catch (Exception e) {
                    cmbDronAsignado.setSelectedIndex(0);
                }
            } else {
                cmbDronAsignado.setSelectedIndex(0);
            }
            
            calcularEstimaciones();
        }
    }
    
    private void actualizarDistanciaAutomatica() {
        if (cmbCliente.getSelectedIndex() > 0) {
            try {
                String clienteSeleccionado = cmbCliente.getSelectedItem().toString();
                String[] partes = clienteSeleccionado.split(" - ");
                if (partes.length == 0) {
                    throw new IllegalArgumentException("Formato de cliente inválido: " + clienteSeleccionado);
                }
                String idCliente = partes[0].replace("CLI", "");
                int clienteId = Integer.parseInt(idCliente);
                
                // Calcular distancia real desde DRONEMED hasta el cliente
                double distancia = controladorCliente.calcularDistanciaACliente(clienteId);
                
                // Convertir a entero (sin decimales) y establecer valor válido
                int distanciaEntera = (int) Math.max(0.0, Math.round(distancia));
                txtDistancia.setText(String.valueOf(distanciaEntera));
                calcularEstimaciones();
                
            } catch (Exception e) {
                // En caso de error, establecer distancia como 0
                txtDistancia.setText("0");
                calcularEstimaciones();
                System.err.println("Error al calcular distancia automática: " + e.getMessage());
            }
        } else {
            txtDistancia.setText("0");
            calcularEstimaciones();
        }
    }
}