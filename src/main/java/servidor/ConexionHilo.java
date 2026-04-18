package servidor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ConexionHilo extends Thread {
    public DataInputStream in;
    public DataOutputStream out;
    public String nombre;

    public ConexionHilo(DataInputStream in, DataOutputStream out, String nombre) {
        this.in = in;
        this.out = out;
        this.nombre = nombre;
    }

    public void enviarMensaje(String mensaje) {
        try {
            out.writeUTF(mensaje);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                String respuestaMenu = in.readUTF();

                if (respuestaMenu.equalsIgnoreCase("SALIR")) {
                    MainServidor.usuariosConectados.remove(nombre);
                    MainServidor.notificarUsuarios();
                    break;
                }

                if (respuestaMenu.startsWith("MSG ")) {
                    String[] partes = respuestaMenu.split(" ", 3);
                    if (partes.length < 3) {
                        enviarMensaje("(Sistema) Error de formato. Usá: MSG [NOMBRE] [TEXTO]");
                        continue;
                    }
                    String destino = partes[1].trim().toLowerCase();
                    String mensaje = partes[2];

                    ConexionHilo hiloDestino = MainServidor.usuariosConectados.get(destino);
                    if (hiloDestino != null) {
                        hiloDestino.enviarMensaje(nombre + " dice: " + mensaje);
                    } else {
                        enviarMensaje("(Sistema) El usuario " + destino + " no existe o está desconectado.");
                    }
                }

                if (respuestaMenu.startsWith("ALL ")) {
                    String mensaje = respuestaMenu.substring(4).trim();
                    for (ConexionHilo hilo : MainServidor.usuariosConectados.values()) {
                        if (!hilo.nombre.equals(nombre)) {
                            hilo.enviarMensaje("[TODOS] " + nombre + ": " + mensaje);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Cliente desconectado abruptamente: " + nombre);
            MainServidor.usuariosConectados.remove(nombre);
            MainServidor.notificarUsuarios();
        }
    }
}