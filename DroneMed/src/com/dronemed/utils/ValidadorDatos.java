package com.dronemed.utils;

import java.util.regex.Pattern;

public class ValidadorDatos {
    
    // Patrones de validación
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
    
    private static final Pattern TELEFONO_PATTERN = 
        Pattern.compile("^[+]?[0-9]{8,15}$");
    
    private static final Pattern NOMBRE_PATTERN = 
        Pattern.compile("^[A-Za-zÀ-ÿ\\s]{2,50}$");
    
    // Validación de email
    public static boolean validarEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }
    
    // Validación de teléfono
    public static boolean validarTelefono(String telefono) {
        if (telefono == null || telefono.trim().isEmpty()) {
            return false;
        }
        return TELEFONO_PATTERN.matcher(telefono.replaceAll("\\s+", "")).matches();
    }
    
    // Validación de nombre
    public static boolean validarNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return false;
        }
        return NOMBRE_PATTERN.matcher(nombre.trim()).matches();
    }
    
    // Validación de peso
    public static boolean validarPeso(double peso) {
        return peso > 0 && peso <= 10.0; // Máximo 10kg
    }
    
    // Validación de coordenadas
    public static boolean validarLatitud(double latitud) {
        return latitud >= -90.0 && latitud <= 90.0;
    }
    
    public static boolean validarLongitud(double longitud) {
        return longitud >= -180.0 && longitud <= 180.0;
    }
    
    // Validación de texto no vacío
    public static boolean validarTextoNoVacio(String texto) {
        return texto != null && !texto.trim().isEmpty();
    }
    
    // Validación de texto con longitud mínima y máxima
    public static boolean validarTextoLongitud(String texto, int minimo, int maximo) {
        if (texto == null) {
            return false;
        }
        int longitud = texto.trim().length();
        return longitud >= minimo && longitud <= maximo;
    }
    
    // Validación de ID positivo
    public static boolean validarId(int id) {
        return id > 0;
    }
    
    // Validación de estado de dron
    public static boolean validarEstadoDron(String estado) {
        if (estado == null) {
            return false;
        }
        return estado.equals("DISPONIBLE") || 
               estado.equals("EN_VUELO") || 
               estado.equals("MANTENIMIENTO") || 
               estado.equals("FUERA_DE_SERVICIO");
    }
    
    // Validación de estado de pedido
    public static boolean validarEstadoPedido(String estado) {
        if (estado == null) {
            return false;
        }
        return estado.equals("PENDIENTE") || 
               estado.equals("ASIGNADO") || 
               estado.equals("EN_TRANSITO") || 
               estado.equals("ENTREGADO") || 
               estado.equals("CANCELADO");
    }
    
    // Validación de prioridad de pedido
    public static boolean validarPrioridadPedido(String prioridad) {
        if (prioridad == null) {
            return false;
        }
        return prioridad.equals("BAJA") || 
               prioridad.equals("NORMAL") || 
               prioridad.equals("ALTA") || 
               prioridad.equals("URGENTE");
    }
    
    // Validación de tipo de carga
    public static boolean validarTipoCarga(String tipoCarga) {
        if (tipoCarga == null) {
            return false;
        }
        return tipoCarga.equals("LIGERA") || 
               tipoCarga.equals("MEDIA") || 
               tipoCarga.equals("PESADA");
    }
    
    // Validación de capacidad de carga según tipo de dron
    public static boolean validarCapacidadCarga(String tipoDron, double peso) {
        switch (tipoDron) {
            case "LIGERA":
                return peso <= 2.0;
            case "MEDIA":
                return peso <= 5.0;
            case "PESADA":
                return peso <= 10.0;
            default:
                return false;
        }
    }
    
    // Validación de dirección
    public static boolean validarDireccion(String direccion) {
        return validarTextoLongitud(direccion, 10, 200);
    }
    
    // Validación de descripción
    public static boolean validarDescripcion(String descripcion) {
        return validarTextoLongitud(descripcion, 5, 500);
    }
    
    // Validación de costo
    public static boolean validarCosto(double costo) {
        return costo >= 0;
    }
    
    // Método para limpiar texto
    public static String limpiarTexto(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.trim().replaceAll("\\s+", " ");
    }
}
