package client;

import client.controller.ClientController;


 //Responsabilidad única: arrancar la aplicación en el hilo de Swing (EDT).

public class MainClient {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            ClientController controller = new ClientController();
            controller.iniciar();
        });
    }
}