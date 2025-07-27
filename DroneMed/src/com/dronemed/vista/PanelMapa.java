package com.dronemed.vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Map;
import java.util.HashMap;
import com.dronemed.database.DAO.ClienteDAO;
import com.dronemed.database.DAO.PedidoDAO;
import com.dronemed.modelo.Cliente;
import com.dronemed.modelo.Pedido;

public class PanelMapa extends JPanel {
    private List<DronEnMapa> dronesEnMapa;
    private List<UbicacionCliente> ubicacionesClientes;
    private List<Pedido> pedidosPendientes;
    private Map<Integer, UbicacionCliente> mapaClientes;
    private Timer timerAnimacion;
    private Random random;
    private UbicacionCliente centroOperaciones;
    private UbicacionCliente destinoActual;
    private boolean enRuta = false;
    private boolean regresando = false;
    private ClienteDAO clienteDAO;
    private PedidoDAO pedidoDAO;
    private int indicePedidoActual = 0;
    private boolean simulandoPedidos = false;
    
    public PanelMapa() {
        random = new Random();
        dronesEnMapa = new ArrayList<>();
        ubicacionesClientes = new ArrayList<>();
        pedidosPendientes = new ArrayList<>();
        mapaClientes = new HashMap<>();
        clienteDAO = new ClienteDAO();
        pedidoDAO = new PedidoDAO();
        
        initComponents();
        inicializarDatos();
        iniciarAnimacion();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(173, 216, 230));
        
        JPanel panelControles = new JPanel(new FlowLayout());
        
        JButton btnIniciarSimulacion = new JButton("Iniciar Simulación");
        JButton btnPausarSimulacion = new JButton("Pausar");
        JButton btnReiniciar = new JButton("Reiniciar");
        JButton btnSimularPedidos = new JButton("Simular Pedidos Pendientes");
        
        btnIniciarSimulacion.addActionListener(e -> iniciarAnimacion());
        btnPausarSimulacion.addActionListener(e -> pausarAnimacion());
        btnReiniciar.addActionListener(e -> reiniciarSimulacion());
        btnSimularPedidos.addActionListener(e -> iniciarSimulacionPedidos());
        
        panelControles.add(btnIniciarSimulacion);
        panelControles.add(btnPausarSimulacion);
        panelControles.add(btnReiniciar);
        panelControles.add(btnSimularPedidos);
        
        add(panelControles, BorderLayout.NORTH);
        
        JPanel panelInfo = new JPanel();
        panelInfo.add(new JLabel("Mapa de Seguimiento en Tiempo Real"));
        add(panelInfo, BorderLayout.SOUTH);
    }
    
    private void inicializarDatos() {
        // Centro de operaciones DRONEMED
        centroOperaciones = new UbicacionCliente("DRONEMED - Centro de Operaciones", 400, 300, Color.MAGENTA);
        
        // Limpiar listas
        ubicacionesClientes.clear();
        mapaClientes.clear();
        dronesEnMapa.clear();
        
        // Cargar clientes desde la base de datos
        cargarClientesDesdeBaseDatos();
        
        // Cargar pedidos pendientes
        cargarPedidosPendientes();
        
        // Sin rutas activas inicialmente
        destinoActual = null;
        enRuta = false;
        regresando = false;
        simulandoPedidos = false;
        indicePedidoActual = 0;
    }
    
    private void iniciarAnimacion() {
        if (timerAnimacion != null) {
            timerAnimacion.stop();
        }
        
        timerAnimacion = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moverDrones();
                repaint();
            }
        });
        timerAnimacion.start();
    }
    
    private void pausarAnimacion() {
        if (timerAnimacion != null) {
            timerAnimacion.stop();
        }
    }
    
    private void reiniciarSimulacion() {
        pausarAnimacion();
        inicializarDatos();
        repaint();
    }
    
    private void cargarClientesDesdeBaseDatos() {
        try {
            List<Cliente> clientes = clienteDAO.leerTodos();
            
            // Coordenadas base para distribución en el mapa (simulando ubicaciones)
            int[][] coordenadas = {
                {150, 100}, {650, 150}, {200, 350}, {600, 400}, {120, 450},
                {700, 80}, {80, 60}, {350, 150}, {250, 250}, {150, 200},
                {100, 150}, {650, 200}
            };
            
            int index = 0;
            for (Cliente cliente : clientes) {
                Color color = obtenerColorPorTipo(cliente.getTipo());
                
                // Usar coordenadas predefinidas si están disponibles
                int x = (index < coordenadas.length) ? coordenadas[index][0] : 200 + (index * 50);
                int y = (index < coordenadas.length) ? coordenadas[index][1] : 200 + (index * 30);
                
                UbicacionCliente ubicacion = new UbicacionCliente(cliente.getNombre(), x, y, color);
                ubicacionesClientes.add(ubicacion);
                mapaClientes.put(cliente.getId(), ubicacion);
                
                index++;
            }
            
            System.out.println("Cargados " + clientes.size() + " establecimientos desde la base de datos");
            
        } catch (Exception e) {
            System.err.println("Error al cargar clientes: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private Color obtenerColorPorTipo(String tipo) {
        switch (tipo.toUpperCase()) {
            case "HOSPITAL": return Color.RED;
            case "CLINICA": return Color.BLUE;
            case "CENTRO_SALUD": return Color.GREEN;
            case "FARMACIA": return Color.ORANGE;
            default: return Color.GRAY;
        }
    }
    
    private void cargarPedidosPendientes() {
        try {
            pedidosPendientes = pedidoDAO.leerPorEstado("PENDIENTE");
            System.out.println("Cargados " + pedidosPendientes.size() + " pedidos pendientes");
        } catch (Exception e) {
            System.err.println("Error al cargar pedidos pendientes: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void iniciarSimulacionPedidos() {
        if (pedidosPendientes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay pedidos pendientes para simular", "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        simulandoPedidos = true;
        indicePedidoActual = 0;
        
        // Crear un dron simulado para la entrega
        dronesEnMapa.clear();
        DronEnMapa dronSimulado = new DronEnMapa(1, "DroneMed-SIM", centroOperaciones.x, centroOperaciones.y, Color.BLACK);
        dronesEnMapa.add(dronSimulado);
        
        // Iniciar la animación
        iniciarAnimacion();
        
        JOptionPane.showMessageDialog(this, "Iniciando simulación de " + pedidosPendientes.size() + " pedidos pendientes", "Simulación Iniciada", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void moverDrones() {
        if (!simulandoPedidos || dronesEnMapa.isEmpty() || pedidosPendientes.isEmpty()) {
            return;
        }
        
        DronEnMapa dron = dronesEnMapa.get(0);
        
        if (indicePedidoActual >= pedidosPendientes.size()) {
            // Terminar simulación
            simulandoPedidos = false;
            dronesEnMapa.clear();
            destinoActual = null;
            enRuta = false;
            regresando = false;
            JOptionPane.showMessageDialog(this, "Simulación de pedidos completada", "Simulación Terminada", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        Pedido pedidoActual = pedidosPendientes.get(indicePedidoActual);
        UbicacionCliente destino = mapaClientes.get(pedidoActual.getClienteId());
        
        if (destino == null) {
            // Si no se encuentra el destino, pasar al siguiente pedido
            indicePedidoActual++;
            return;
        }
        
        if (!enRuta && !regresando) {
            // Iniciar ruta hacia el destino
            destinoActual = destino;
            enRuta = true;
        }
        
        if (enRuta && !regresando) {
            // Mover hacia el destino
            moverHacia(dron, destinoActual);
            
            // Verificar si llegó al destino
            if (Math.abs(dron.x - destinoActual.x) < 5 && Math.abs(dron.y - destinoActual.y) < 5) {
                enRuta = false;
                regresando = true;
                // Simular tiempo de entrega
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        } else if (regresando) {
            // Regresar al centro de operaciones
            moverHacia(dron, centroOperaciones);
            
            // Verificar si regresó al centro
            if (Math.abs(dron.x - centroOperaciones.x) < 5 && Math.abs(dron.y - centroOperaciones.y) < 5) {
                regresando = false;
                indicePedidoActual++;
                destinoActual = null;
            }
        }
    }
    
    private void moverHacia(DronEnMapa dron, UbicacionCliente destino) {
        int deltaX = destino.x - dron.x;
        int deltaY = destino.y - dron.y;
        
        int velocidad = 3;
        
        if (Math.abs(deltaX) > velocidad) {
            dron.x += (deltaX > 0) ? velocidad : -velocidad;
        } else {
            dron.x = destino.x;
        }
        
        if (Math.abs(deltaY) > velocidad) {
            dron.y += (deltaY > 0) ? velocidad : -velocidad;
        } else {
            dron.y = destino.y;
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        dibujarGrid(g2d);
        
        // Dibujar centro de operaciones DRONEMED
        if (centroOperaciones != null) {
            dibujarCentroOperaciones(g2d, centroOperaciones);
        }
        
        // Dibujar rutas si hay simulación activa
        if (simulandoPedidos && destinoActual != null) {
            dibujarRuta(g2d);
        }
        
        for (UbicacionCliente ubicacion : ubicacionesClientes) {
            dibujarUbicacionCliente(g2d, ubicacion);
        }
        
        for (DronEnMapa dron : dronesEnMapa) {
            dibujarDron(g2d, dron);
        }
        
        dibujarLeyenda(g2d);
    }
    
    private void dibujarGrid(Graphics2D g2d) {
        g2d.setColor(new Color(200, 200, 200, 100));
        g2d.setStroke(new BasicStroke(1));
        
        int gridSize = 50;
        for (int x = 0; x < getWidth(); x += gridSize) {
            g2d.drawLine(x, 0, x, getHeight());
        }
        for (int y = 0; y < getHeight(); y += gridSize) {
            g2d.drawLine(0, y, getWidth(), y);
        }
    }
    
    private void dibujarUbicacionCliente(Graphics2D g2d, UbicacionCliente ubicacion) {
        g2d.setColor(ubicacion.color);
        g2d.fillRect(ubicacion.x - 15, ubicacion.y - 15, 30, 30);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(ubicacion.x - 15, ubicacion.y - 15, 30, 30);
        
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawLine(ubicacion.x - 8, ubicacion.y, ubicacion.x + 8, ubicacion.y);
        g2d.drawLine(ubicacion.x, ubicacion.y - 8, ubicacion.x, ubicacion.y + 8);
        
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 10));
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(ubicacion.nombre);
        g2d.drawString(ubicacion.nombre, ubicacion.x - textWidth/2, ubicacion.y + 25);
    }
    
    private void dibujarDron(Graphics2D g2d, DronEnMapa dron) {
        g2d.setColor(dron.color);
        g2d.fillOval(dron.x - 10, dron.y - 10, 20, 20);
        g2d.setColor(Color.BLACK);
        g2d.drawOval(dron.x - 10, dron.y - 10, 20, 20);
        
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(dron.x - 15, dron.y - 15, dron.x - 5, dron.y - 5);
        g2d.drawLine(dron.x + 5, dron.y - 5, dron.x + 15, dron.y - 15);
        g2d.drawLine(dron.x - 15, dron.y + 15, dron.x - 5, dron.y + 5);
        g2d.drawLine(dron.x + 5, dron.y + 5, dron.x + 15, dron.y + 15);
        
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 8));
        String id = String.valueOf(dron.id);
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(id);
        g2d.drawString(id, dron.x - textWidth/2, dron.y + 3);
        
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.PLAIN, 9));
        fm = g2d.getFontMetrics();
        textWidth = fm.stringWidth(dron.modelo);
        g2d.drawString(dron.modelo, dron.x - textWidth/2, dron.y + 25);
    }
    
    private void dibujarCentroOperaciones(Graphics2D g2d, UbicacionCliente centro) {
        // Dibujar un círculo más grande para el centro de operaciones
        g2d.setColor(centro.color);
        g2d.fillOval(centro.x - 15, centro.y - 15, 30, 30);
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawOval(centro.x - 15, centro.y - 15, 30, 30);
        
        // Dibujar el nombre
        g2d.setFont(new Font("Arial", Font.BOLD, 10));
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(centro.nombre);
        g2d.drawString(centro.nombre, centro.x - textWidth/2, centro.y - 20);
        
        g2d.setStroke(new BasicStroke(1)); // Restaurar stroke
    }
    
    private void dibujarRuta(Graphics2D g2d) {
        if (dronesEnMapa.isEmpty() || destinoActual == null) return;
        
        DronEnMapa dron = dronesEnMapa.get(0);
        
        // Dibujar línea de ruta
        g2d.setColor(Color.CYAN);
        g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{5}, 0));
        
        if (!regresando) {
            // Línea hacia el destino
            g2d.drawLine(dron.x, dron.y, destinoActual.x, destinoActual.y);
        } else {
            // Línea de regreso al centro
            g2d.drawLine(dron.x, dron.y, centroOperaciones.x, centroOperaciones.y);
        }
        
        g2d.setStroke(new BasicStroke(1)); // Restaurar stroke
    }
    
    private void dibujarLeyenda(Graphics2D g2d) {
        int x = 10, y = 30;
        int alturaLeyenda = simulandoPedidos ? 180 : 140;
        g2d.setColor(new Color(255, 255, 255, 200));
        g2d.fillRect(x - 5, y - 20, 250, alturaLeyenda);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(x - 5, y - 20, 250, alturaLeyenda);
        
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString("Leyenda:", x, y);
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        y += 20;
        
        g2d.setColor(Color.MAGENTA);
        g2d.fillOval(x, y - 5, 10, 10);
        g2d.setColor(Color.BLACK);
        g2d.drawString("DRONEMED", x + 15, y + 3);
        
        y += 15;
        g2d.setColor(Color.BLACK);
        g2d.fillOval(x, y - 5, 10, 10);
        g2d.setColor(Color.BLACK);
        g2d.drawString("Drones en vuelo", x + 15, y + 3);
        
        y += 15;
        g2d.setColor(Color.RED);
        g2d.fillRect(x, y - 5, 10, 10);
        g2d.setColor(Color.BLACK);
        g2d.drawString("Hospitales", x + 15, y + 3);
        
        y += 15;
        g2d.setColor(Color.BLUE);
        g2d.fillRect(x, y - 5, 10, 10);
        g2d.setColor(Color.BLACK);
        g2d.drawString("Clínicas", x + 15, y + 3);
        
        y += 15;
        g2d.setColor(Color.GREEN);
        g2d.fillRect(x, y - 5, 10, 10);
        g2d.setColor(Color.BLACK);
        g2d.drawString("Centros rurales", x + 15, y + 3);
        
        y += 15;
        g2d.setColor(Color.ORANGE);
        g2d.fillRect(x, y - 5, 10, 10);
        g2d.setColor(Color.BLACK);
        g2d.drawString("Farmacias", x + 15, y + 3);
        
        // Información de simulación de pedidos
        if (simulandoPedidos && !pedidosPendientes.isEmpty()) {
            y += 20;
            g2d.setFont(new Font("Arial", Font.BOLD, 10));
            g2d.drawString("Simulación Activa:", x, y);
            
            y += 15;
            g2d.setFont(new Font("Arial", Font.PLAIN, 9));
            g2d.drawString("Pedido " + (indicePedidoActual + 1) + " de " + pedidosPendientes.size(), x, y);
            
            if (indicePedidoActual < pedidosPendientes.size()) {
                Pedido pedidoActual = pedidosPendientes.get(indicePedidoActual);
                UbicacionCliente destino = mapaClientes.get(pedidoActual.getClienteId());
                if (destino != null) {
                    y += 12;
                    String nombreCorto = destino.nombre.length() > 25 ? 
                        destino.nombre.substring(0, 25) + "..." : destino.nombre;
                    g2d.drawString("Destino: " + nombreCorto, x, y);
                }
            }
        }
    }
}
