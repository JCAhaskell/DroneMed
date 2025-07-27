package com.dronemed.interfaces;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public interface Mantenible {

    void programarMantenimiento(LocalDateTime fecha, String tipo);

    boolean requiereMantenimiento();

    LocalDateTime getUltimoMantenimiento();

    default long getDiasDesdeUltimoMantenimiento() {
        LocalDateTime ultimo = getUltimoMantenimiento();
        if (ultimo == null) {
            return Long.MAX_VALUE;
        }
        return ChronoUnit.DAYS.between(ultimo, LocalDateTime.now());
    }

    default boolean mantenimientoVencido() {
        return getDiasDesdeUltimoMantenimiento() > 30;
    }

    default String getEstadoMantenimiento() {
        long dias = getDiasDesdeUltimoMantenimiento();

        if (dias == Long.MAX_VALUE) {
            return "Sin mantenimiento registrado";
        } else if (dias > 30) {
            return "Mantenimiento vencido (" + dias + " dias)";
        } else if (dias > 20) {
            return "Mantenimiento proximo (" + dias + " dias)";
        } else {
            return "Mantenimiento al dia (" + dias + " dias)";
        }
    }

    default void marcaMantenimientoCompletado(String tipo) {
        System.out.println("Mantenimiento " + tipo + " completado el " + LocalDateTime.now());
    }
}
