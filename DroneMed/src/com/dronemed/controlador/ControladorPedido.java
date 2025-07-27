package com.dronemed.controlador;

import com.dronemed.database.DAO.DronDAO;
import com.dronemed.database.DAO.PedidoDAO;
import com.dronemed.modelo.Dron;
import com.dronemed.modelo.Pedido;
import com.dronemed.utils.ValidadorDatos;
import java.util.List;
import java.util.stream.Collectors;

public class ControladorPedido {
    private PedidoDAO pedidoDAO;
    private DronDAO dronDAO;
    
    public ControladorPedido() {
        this.pedidoDAO = new PedidoDAO();
        this.dronDAO = new DronDAO();
    }
    
    // Crear un nuevo pedido
    public boolean crearPedido(int clienteId, String medicamento, String descripcion, 
                              double peso, String direccionEntrega, double latitud, double longitud) {
        
        // Validar datos de entrada
        if (!validarDatosPedido(clienteId, medicamento, descripcion, peso, direccionEntrega, latitud, longitud)) {
            return false;
        }
        
        // Crear el pedido
        Pedido pedido = new Pedido(clienteId, medicamento, descripcion, peso, direccionEntrega, latitud, longitud);
        
        // Calcular costo basado en peso y distancia (simulado)
        double costo = calcularCosto(peso, latitud, longitud);
        pedido.setCosto(costo);
        
        return pedidoDAO.crear(pedido);
    }
    
    // Obtener pedido por ID
    public Pedido obtenerPedido(int id) {
        if (!ValidadorDatos.validarId(id)) {
            System.err.println("Error: ID de pedido inválido");
            return null;
        }
        
        return pedidoDAO.leerPorId(id);
    }
    
    // Obtener todos los pedidos
    public List<Pedido> obtenerTodosLosPedidos() {
        return pedidoDAO.leerTodos();
    }
    
    // Obtener pedidos por estado
    public List<Pedido> obtenerPedidosPorEstado(String estado) {
        if (!ValidadorDatos.validarEstadoPedido(estado)) {
            System.err.println("Error: Estado de pedido inválido");
            return null;
        }
        
        return pedidoDAO.leerPorEstado(estado);
    }
    
    // Obtener pedidos pendientes
    public List<Pedido> obtenerPedidosPendientes() {
        return pedidoDAO.leerPendientes();
    }
    
    // Obtener pedidos por cliente
    public List<Pedido> obtenerPedidosPorCliente(int clienteId) {
        if (!ValidadorDatos.validarId(clienteId)) {
            System.err.println("Error: ID de cliente inválido");
            return null;
        }
        
        return pedidoDAO.leerPorCliente(clienteId);
    }
    
    // Obtener pedidos por dron
    public List<Pedido> obtenerPedidosPorDron(int dronId) {
        if (!ValidadorDatos.validarId(dronId)) {
            System.err.println("Error: ID de dron inválido");
            return null;
        }
        
        return pedidoDAO.leerPorDron(dronId);
    }
    
    // Actualizar pedido
    public boolean actualizarPedido(int id, int clienteId, String medicamento, String descripcion, 
                                   double peso, String direccionEntrega, double latitud, double longitud, 
                                   String estado, String prioridad) {
        
        // Validar ID
        if (!ValidadorDatos.validarId(id)) {
            System.err.println("Error: ID de pedido inválido");
            return false;
        }
        
        // Obtener pedido existente
        Pedido pedido = pedidoDAO.leerPorId(id);
        if (pedido == null) {
            System.err.println("Error: Pedido no encontrado");
            return false;
        }
        
        // Validar datos de entrada
        if (!validarDatosPedido(clienteId, medicamento, descripcion, peso, direccionEntrega, latitud, longitud)) {
            return false;
        }
        
        // Validar estado y prioridad
        if (!ValidadorDatos.validarEstadoPedido(estado)) {
            System.err.println("Error: Estado de pedido inválido");
            return false;
        }
        
        if (!ValidadorDatos.validarPrioridadPedido(prioridad)) {
            System.err.println("Error: Prioridad de pedido inválida");
            return false;
        }
        
        // Actualizar datos
        pedido.setClienteId(clienteId);
        pedido.setMedicamento(medicamento);
        pedido.setDescripcion(descripcion);
        pedido.setPeso(peso);
        pedido.setDireccionEntrega(direccionEntrega);
        pedido.setLatitud(latitud);
        pedido.setLongitud(longitud);
        pedido.setEstado(estado);
        pedido.setPrioridad(prioridad);
        
        // Recalcular costo si cambió el peso o ubicación
        double nuevoCosto = calcularCosto(peso, latitud, longitud);
        pedido.setCosto(nuevoCosto);
        
        return pedidoDAO.actualizar(pedido);
    }
    
    // Asignar dron a pedido
    public boolean asignarDron(int pedidoId, int dronId) {
        if (!ValidadorDatos.validarId(pedidoId) || !ValidadorDatos.validarId(dronId)) {
            System.err.println("Error: IDs inválidos");
            return false;
        }
        
        // Verificar que el pedido existe y está pendiente
        Pedido pedido = pedidoDAO.leerPorId(pedidoId);
        if (pedido == null) {
            System.err.println("Error: Pedido no encontrado");
            return false;
        }
        
        if (!pedido.estaPendiente()) {
            System.err.println("Error: El pedido no está pendiente");
            return false;
        }
        
        // Verificar que el dron existe y está disponible
        Dron dron = dronDAO.leerPorId(String.valueOf(dronId));
        if (dron == null) {
            System.err.println("Error: Dron no encontrado");
            return false;
        }
        
        if (!dron.estaDisponible()) {
            System.err.println("Error: El dron no está disponible");
            return false;
        }
        
        // Verificar capacidad de carga
        if (!ValidadorDatos.validarCapacidadCarga(dron.getTipoCarga(), pedido.getPeso())) {
            System.err.println("Error: El dron no puede cargar este peso");
            return false;
        }
        
        // Asignar dron
        boolean asignado = pedidoDAO.asignarDron(pedidoId, dronId);
        
        if (asignado) {
            // Cambiar estado del dron a EN_VUELO
            dron.setEstado("EN_VUELO");
            dronDAO.actualizar(dron);
        }
        
        return asignado;
    }
    
    // Cambiar estado del pedido
    public boolean cambiarEstado(int pedidoId, String nuevoEstado) {
        if (!ValidadorDatos.validarId(pedidoId)) {
            System.err.println("Error: ID de pedido inválido");
            return false;
        }
        
        if (!ValidadorDatos.validarEstadoPedido(nuevoEstado)) {
            System.err.println("Error: Estado inválido");
            return false;
        }
        
        return pedidoDAO.cambiarEstado(pedidoId, nuevoEstado);
    }
    
    // Marcar pedido como entregado
    public boolean marcarComoEntregado(int pedidoId) {
        if (!ValidadorDatos.validarId(pedidoId)) {
            System.err.println("Error: ID de pedido inválido");
            return false;
        }
        
        // Obtener el pedido para liberar el dron
        Pedido pedido = pedidoDAO.leerPorId(pedidoId);
        if (pedido != null && pedido.getDronAsignado() > 0) {
            // Liberar el dron
            Dron dron = dronDAO.leerPorId(String.valueOf(pedido.getDronAsignado()));
            if (dron != null) {
                dron.setEstado("DISPONIBLE");
                dronDAO.actualizar(dron);
            }
        }
        
        return pedidoDAO.marcarComoEntregado(pedidoId);
    }
    
    // Cancelar pedido
    public boolean cancelarPedido(int pedidoId) {
        if (!ValidadorDatos.validarId(pedidoId)) {
            System.err.println("Error: ID de pedido inválido");
            return false;
        }
        
        // Obtener el pedido para liberar el dron si está asignado
        Pedido pedido = pedidoDAO.leerPorId(pedidoId);
        if (pedido != null && pedido.getDronAsignado() > 0) {
            // Liberar el dron
            Dron dron = dronDAO.leerPorId(String.valueOf(pedido.getDronAsignado()));
            if (dron != null) {
                dron.setEstado("DISPONIBLE");
                dronDAO.actualizar(dron);
            }
        }
        
        return pedidoDAO.cambiarEstado(pedidoId, "CANCELADO");
    }
    
    // Eliminar pedido
    public boolean eliminarPedido(int id) {
        if (!ValidadorDatos.validarId(id)) {
            System.err.println("Error: ID de pedido inválido");
            return false;
        }
        
        Pedido pedido = pedidoDAO.leerPorId(id);
        if (pedido == null) {
            System.err.println("Error: Pedido no encontrado");
            return false;
        }
        
        // Solo permitir eliminar pedidos cancelados o entregados
        if (!pedido.estaEntregado() && !"CANCELADO".equals(pedido.getEstado())) {
            System.err.println("Error: Solo se pueden eliminar pedidos entregados o cancelados");
            return false;
        }
        
        return pedidoDAO.eliminar(id);
    }
    
    // Buscar pedidos por medicamento
    public List<Pedido> buscarPedidosPorMedicamento(String medicamento) {
        if (!ValidadorDatos.validarTextoNoVacio(medicamento)) {
            System.err.println("Error: Medicamento de búsqueda inválido");
            return null;
        }
        
        return pedidoDAO.buscarPorMedicamento(medicamento);
    }
    
    // Obtener pedidos urgentes
    public List<Pedido> obtenerPedidosUrgentes() {
        return pedidoDAO.leerTodos().stream()
                .filter(Pedido::esPedidoUrgente)
                .collect(Collectors.toList());
    }
    
    // Obtener estadísticas de pedidos
    public String obtenerEstadisticasPedidos() {
        List<Pedido> todosPedidos = pedidoDAO.leerTodos();
        
        long pendientes = todosPedidos.stream().filter(p -> "PENDIENTE".equals(p.getEstado())).count();
        long asignados = todosPedidos.stream().filter(p -> "ASIGNADO".equals(p.getEstado())).count();
        long enTransito = todosPedidos.stream().filter(p -> "EN_TRANSITO".equals(p.getEstado())).count();
        long entregados = todosPedidos.stream().filter(p -> "ENTREGADO".equals(p.getEstado())).count();
        long cancelados = todosPedidos.stream().filter(p -> "CANCELADO".equals(p.getEstado())).count();
        
        return String.format("Total: %d, Pendientes: %d, Asignados: %d, En Tránsito: %d, Entregados: %d, Cancelados: %d", 
                           todosPedidos.size(), pendientes, asignados, enTransito, entregados, cancelados);
    }
    
    // Método privado para validar datos del pedido
    private boolean validarDatosPedido(int clienteId, String medicamento, String descripcion, 
                                      double peso, String direccionEntrega, double latitud, double longitud) {
        
        // Validar cliente ID
        if (!ValidadorDatos.validarId(clienteId)) {
            System.err.println("Error: ID de cliente inválido");
            return false;
        }
        
        // Validar medicamento
        if (!ValidadorDatos.validarTextoNoVacio(medicamento)) {
            System.err.println("Error: Medicamento inválido");
            return false;
        }
        
        // Validar descripción
        if (!ValidadorDatos.validarDescripcion(descripcion)) {
            System.err.println("Error: Descripción inválida");
            return false;
        }
        
        // Validar peso
        if (!ValidadorDatos.validarPeso(peso)) {
            System.err.println("Error: Peso inválido");
            return false;
        }
        
        // Validar dirección
        if (!ValidadorDatos.validarDireccion(direccionEntrega)) {
            System.err.println("Error: Dirección de entrega inválida");
            return false;
        }
        
        // Validar coordenadas
        if (!ValidadorDatos.validarLatitud(latitud)) {
            System.err.println("Error: Latitud inválida");
            return false;
        }
        
        if (!ValidadorDatos.validarLongitud(longitud)) {
            System.err.println("Error: Longitud inválida");
            return false;
        }
        
        return true;
    }
    
    // Método privado para calcular costo
    private double calcularCosto(double peso, double latitud, double longitud) {
        // Costo base
        double costoBase = 10.0;
        
        // Costo por peso (2.0 por kg)
        double costoPeso = peso * 2.0;
        
        // Costo por distancia (simulado basado en coordenadas)
        double distancia = Math.sqrt(Math.pow(latitud, 2) + Math.pow(longitud, 2)) / 100;
        double costoDistancia = distancia * 1.5;
        
        return costoBase + costoPeso + costoDistancia;
    }
}
