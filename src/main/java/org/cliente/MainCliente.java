package org.cliente;

import org.cliente.controller.ClienteController;


 //Responsabilidad única: arrancar la aplicación en el hilo de Swing (EDT).

public class MainCliente {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            ClienteController controller = new ClienteController();
            controller.iniciar();
        });
    }
}