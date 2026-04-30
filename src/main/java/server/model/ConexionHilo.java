package server.model;

import server.controller.ServerController;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

  //Responsabilidad única: leer mensajes de UN cliente y delegarlos al controller.

public class ConexionHilo extends Thread {

    public final DataInputStream in;
    public final DataOutputStream out;
    public final String nombre;

    private final ServerController controller;

    public ConexionHilo(DataInputStream in, DataOutputStream out,
                        String nombre, ServerController controller) {
        this.in = in;
        this.out = out;
        this.nombre = nombre;
        this.controller = controller;
    }

    public void enviarMensaje(String mensaje) {
        try {
            out.writeUTF(mensaje);
        } catch (IOException e) {
            System.out.println("[Servidor] Error enviando a " + nombre);
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                String msg = in.readUTF();

                if (msg.equalsIgnoreCase("SALIR")) {
                    controller.clienteDesconectado(nombre);
                    break;
                }
                if (msg.startsWith("MSG ")) {
                    controller.procesarMensajePrivado(nombre, msg);
                    continue;
                }
                if (msg.startsWith("ALL ")) {
                    controller.procesarMensajeTodos(nombre, msg);
                }
            }
        } catch (IOException e) {
            System.out.println("[Servidor] Cliente desconectado abruptamente: " + nombre);
            controller.clienteDesconectado(nombre);
        }
    }
}