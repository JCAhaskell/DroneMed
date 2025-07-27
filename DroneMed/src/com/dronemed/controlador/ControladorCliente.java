package com.dronemed.controlador;

import com.dronemed.database.DAO.ClienteDAO;
import com.dronemed.modelo.Cliente;
import java.util.List;
import java.util.logging.Logger;

public class ControladorCliente {
    private static final Logger LOGGER = Logger.getLogger(ControladorCliente.class.getName());
    private ClienteDAO clienteDAO;
    
    public ControladorCliente() {
        this.clienteDAO = new ClienteDAO();
    }
    
    // Obtener todos los clientes activos
    public List<Cliente> obtenerTodosLosClientes() {
        return clienteDAO.leerTodos();
    }
    
    // Obtener cliente por ID
    public Cliente obtenerCliente(int id) {
        if (id <= 0) {
            LOGGER.warning("ID de cliente no válido: " + id);
            return null;
        }
        
        return clienteDAO.leerPorId(id);
    }
    
    // Calcular distancia desde DroneMed hasta el cliente
    public double calcularDistanciaACliente(int clienteId) {
        if (clienteId <= 0) {
            LOGGER.warning("ID de cliente no válido para calcular distancia: " + clienteId);
            return 0.0;
        }
        
        double distancia = clienteDAO.calcularDistanciaACliente(clienteId);
        LOGGER.info("Distancia calculada para cliente " + clienteId + ": " + distancia + " km");
        
        return distancia;
    }
    
    // Obtener coordenadas de un cliente
    public double[] obtenerCoordenadasCliente(int clienteId) {
        if (clienteId <= 0) {
            LOGGER.warning("ID de cliente no válido para obtener coordenadas: " + clienteId);
            return null;
        }
        
        return clienteDAO.obtenerCoordenadas(clienteId);
    }
    
    // Validar si un cliente existe y está activo
    public boolean existeCliente(int clienteId) {
        Cliente cliente = obtenerCliente(clienteId);
        return cliente != null && cliente.isActivo();
    }
    
    // Obtener nombre del cliente por ID
    public String obtenerNombreCliente(int clienteId) {
        Cliente cliente = obtenerCliente(clienteId);
        return cliente != null ? cliente.getNombre() : "Cliente no encontrado";
    }
}