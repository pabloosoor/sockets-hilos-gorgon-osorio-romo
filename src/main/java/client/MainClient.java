package client;

import client.controller.ClientController;


public class MainClient {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            ClientController controller = new ClientController();
            controller.iniciar();
        });
    }
}