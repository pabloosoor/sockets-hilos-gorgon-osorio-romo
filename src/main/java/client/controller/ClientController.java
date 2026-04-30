package client.controller;

import client.model.ClientModel;
import org.cliente.view.ChatView;
import org.cliente.view.LoginView;

import java.io.IOException;

// Responsabilidad única: coordinar LoginView/ChatView con ClientModel.
public class ClientController {

    private final ClientModel model;
    //private LoginView loginView;
    //private ChatView chatView;

    private Thread hiloEscucha;

    public ClientController() {
        this.model = new ClientModel();
    }

    public void iniciar() {
        loginView = new LoginView(this);
        loginView.setVisible(true);
    }

    // ── Llamado desde LoginView al presionar "Conectar" ──────────────
    public void conectar(String nombre) {
        try {
            model.conectar(nombre);
            loginView.dispose();

            chatView = new ChatView(this, nombre);
            chatView.setVisible(true);

            iniciarHiloEscucha();

        } catch (IOException e) {
            loginView.mostrarError("No se pudo conectar al servidor. ¿Está corriendo?");
        }
    }

    // ── Hilo que escucha mensajes del servidor sin bloquear la UI ────
    private void iniciarHiloEscucha() {
        hiloEscucha = new Thread(() -> {
            try {
                while (model.estaConectado()) {
                    String msg = model.recibirMensaje();

                    if (msg.startsWith("(Desde el servidor)")) {
                        chatView.mostrarSistema(msg);
                    } else {
                        chatView.mostrarMensaje(msg);
                    }
                }
            } catch (IOException e) {
                if (chatView != null)
                    chatView.mostrarSistema("[SISTEMA] Conexión con el servidor perdida.");
            }
        });
        hiloEscucha.setDaemon(true);
        hiloEscucha.start();
    }

    // ── Acciones desde ChatView
    public void enviarPrivado(String destino, String texto) {
        try {
            if (destino == null || destino.isBlank()) {
                chatView.mostrarSistema("[SISTEMA] Seleccioná un destinatario.");
                return;
            }
            model.enviarPrivado(destino, texto);
        } catch (IOException e) {
            chatView.mostrarSistema("[SISTEMA] Error al enviar mensaje.");
        }
    }

    public void enviarATodos(String texto) {
        try {
            model.enviarATodos(texto);
        } catch (IOException e) {
            chatView.mostrarSistema("[SISTEMA] Error al enviar mensaje.");
        }
    }

    public void desconectar() {
        try {
            model.desconectar();
        } catch (IOException e) {
        }
        System.exit(0);
    }
}