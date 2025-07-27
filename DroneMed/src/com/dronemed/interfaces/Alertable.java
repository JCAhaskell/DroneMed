// Source code is decompiled from a .class file using FernFlower decompiler.
package com.dronemed.interfaces;

import java.io.PrintStream;
import java.time.LocalDateTime;

public interface Alertable {
   void enviarAlerta(String var1);

   default void enviarAlertaEmergencia(String var1, String var2) {
      this.enviarAlerta("EMERGENCIA en " + var2 + ": " + var1);
   }

   default void programarAlerta(String var1, LocalDateTime var2) {
      PrintStream var10000 = System.out;
      String var10001 = String.valueOf(var2);
      var10000.println("Alerta programada para " + var10001 + ": " + var1);
   }

   default boolean puedeEnviarAlertas() {
      return true;
   }
}
