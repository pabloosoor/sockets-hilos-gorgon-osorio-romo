package client.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientModel {

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 5000;

    // --- ESTADO DE RED ---
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String nombreUsuario;

    // --- ESTADO DE SESIÓN ---
    private Map<String, StringBuilder> historialesChat;
    private Map<String, Integer> mensajesNoLeidos;
    private String chatActual;
    private List<String> contactosDelServidor;
    private List<String> misGruposLocales;

    public ClientModel() {
        this.historialesChat = new HashMap<>();
        this.mensajesNoLeidos = new HashMap<>();
        this.chatActual = "TODOS";
        this.contactosDelServidor = new ArrayList<>();
        this.misGruposLocales = new ArrayList<>();

        this.historialesChat.put("TODOS", new StringBuilder());
        this.contactosDelServidor.add("TODOS");
    }

    // ==========================================
    //          MÉTODOS DE RED (SOCKETS)
    // ==========================================

    public void conectar(String nombre) throws IOException {
        this.nombreUsuario = nombre;
        socket = new Socket(HOST, PORT);
        in  = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
        out.writeUTF(nombre);
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

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    // ==========================================
    //      MÉTODOS DE ESTADO DE SESIÓN
    // ==========================================

    public String getChatActual() {
        return chatActual;
    }

    public void setChatActual(String chatActual) {
        this.chatActual = chatActual;
    }

    public void guardarEnHistorial(String chat, String mensaje) {
        historialesChat.putIfAbsent(chat, new StringBuilder());
        historialesChat.get(chat).append(mensaje).append("\n");
    }

    public String getHistorial(String chat) {
        return historialesChat.getOrDefault(chat, new StringBuilder()).toString();
    }

    public void actualizarContactosDelServidor(List<String> contactos) {
        this.contactosDelServidor.clear();
        this.contactosDelServidor.add("TODOS");
        this.contactosDelServidor.addAll(contactos);
    }

    public boolean registrarNuevoGrupo(String nombreGrupo) {
        if (!misGruposLocales.contains(nombreGrupo)) {
            misGruposLocales.add(nombreGrupo);
            return true;
        }
        return false;
    }

    public List<String> getContactosYGrupos() {
        List<String> combinados = new ArrayList<>(contactosDelServidor);
        for (String grupo : misGruposLocales) {
            if (!combinados.contains(grupo)) {
                combinados.add(grupo);
            }
        }
        return combinados;
    }

    // ==========================================
    //      MÉTODOS DE MENSAJES NO LEÍDOS
    // ==========================================

    public void incrementarNoLeidos(String chat) {
        mensajesNoLeidos.put(chat, mensajesNoLeidos.getOrDefault(chat, 0) + 1);
    }

    public void resetearNoLeidos(String chat) {
        mensajesNoLeidos.put(chat, 0);
    }

    public int getNoLeidos(String chat) {
        return mensajesNoLeidos.getOrDefault(chat, 0);
    }

    public Map<String, Integer> getTodosNoLeidos() {
        return mensajesNoLeidos;
    }
}