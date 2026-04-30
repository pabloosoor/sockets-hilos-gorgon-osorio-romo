package org.cliente.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

// Responsabilidad única: manejar la conexión socket del cliente.
public class ClienteModel {

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 5000;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String nombreUsuario;

    public void conectar(String nombre) throws IOException {
        this.nombreUsuario = nombre;
        socket = new Socket(HOST, PORT);
        in  = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
        out.writeUTF(nombre); // primer mensaje: identificarse
    }

    public void desconectar() throws IOException {
        enviarRaw("SALIR");
        if (socket != null && !socket.isClosed()) socket.close();
    }

    public boolean estaConectado() {
        return socket != null && !socket.isClosed();
    }

    public void enviarRaw(String mensaje) throws IOException {
        out.writeUTF(mensaje);
    }

    public void enviarPrivado(String destino, String texto) throws IOException {
        out.writeUTF("MSG " + destino.toLowerCase() + " " + texto);
    }

    public void enviarATodos(String texto) throws IOException {
        out.writeUTF("ALL " + texto);
    }

    public String recibirMensaje() throws IOException {
        return in.readUTF();
    }

    public String getNombreUsuario() { return nombreUsuario; }
}