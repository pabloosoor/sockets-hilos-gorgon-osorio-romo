package servidor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class MainServidor {

    // Lista de usuarios
    public static ConcurrentHashMap<String, ConexionHilo> usuariosConectados = new ConcurrentHashMap<>();
    static String inputNombre;

    public static void main(String[] args) {
        final int PORT = 5000;
        Socket socket = null;
        ServerSocket servidor = null;

        DataInputStream in;
        DataOutputStream out;
       

        try {
            servidor = new ServerSocket(PORT);
            System.out.println("(Desde servidor) Servidor conectado\n_______________________\n");

            while (true) {
                socket = servidor.accept();

                in = new DataInputStream(socket.getInputStream());
                out = new DataOutputStream(socket.getOutputStream());

                String respuestaMenu;
                
                inputNombre = in.readUTF();
                ConexionHilo usuarioConectado = new ConexionHilo(in, out,inputNombre);
                System.out.println("(Desde servidor) Cliente conectado");
                
                usuariosConectados.put(inputNombre, usuarioConectado);
                usuarioConectado.start();
                
                notificarUsuarios();
            }

        } catch (IOException e) {
            System.out.println("(Desde servidor) Conexión del servidor cerrada.");
        }
    }

    public static void notificarUsuarios() {
        String mensaje;
        for (ConexionHilo hilo : usuariosConectados.values()) {
            String usuariosFiltrados = usuariosConectados.keySet().stream()
                   .filter(nombreUsuario -> !nombreUsuario.equals(hilo.nombre))
                   .collect(Collectors.joining(", "));
            
        if (usuariosFiltrados.isEmpty()) {
            mensaje = "(Desde el servidor) NO HAY OTROS USUARIOS CONECTADOS.";
        } else {
            mensaje = "(Desde el servidor) USUARIOS CONECTADOS: " + usuariosFiltrados;
        }
      
            hilo.enviarMensaje(mensaje);
        }
    }

}
