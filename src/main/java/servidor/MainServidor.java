package servidor;

import servidor.controller.ServidorController;
import servidor.model.ConexionHilo;
import servidor.model.ServidorModel;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

 //Responsabilidad única: aceptar conexiones y delegar al controller.

public class MainServidor {

    private static final int PORT = 5000;

    public static void main(String[] args) {
        ServidorModel model = new ServidorModel();
        ServidorController controller = new ServidorController(model);

        try (ServerSocket servidor = new ServerSocket(PORT)) {
            System.out.println("[Servidor] Escuchando en puerto " + PORT);

            while (true) {
                Socket socket = servidor.accept();

                DataInputStream in  = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                String nombre = in.readUTF().trim().toLowerCase().replace(" ", "_");

                ConexionHilo hilo = new ConexionHilo(in, out, nombre, controller);
                controller.clienteConectado(nombre, hilo);
                hilo.start();
            }

        } catch (IOException e) {
            System.out.println("[Servidor] Cerrado.");
        }
    }
}