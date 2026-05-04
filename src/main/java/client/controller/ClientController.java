package client.controller;

import client.model.ClientModel;
import client.view.ChatWindow;
import client.view.LoginView;
import shared.Protocol;    
import shared.Validator;  

import java.io.IOException;
import java.util.List;
import javax.swing.SwingUtilities;

public class ClientController {

    private final ClientModel model;
    private LoginView loginView;
    private ChatWindow chatWindow;
    private Thread hiloEscucha;
    private String miNombre;

    public ClientController() {
        this.model = new ClientModel(); 
    }

    public void iniciar() {
        loginView = new LoginView(this);
        loginView.setVisible(true);
    }

    public void conectar(String nombre) {
          // --- EVENTO: CONECTARSE ---
        try {
            this.miNombre = nombre;
            model.conectar(nombre);
            loginView.dispose();

            chatWindow = new ChatWindow();
            chatWindow.setTitle("Chat App - " + nombre + " (Chat: TODOS)");
            chatWindow.updateContactList(new String[]{"TODOS"});

            registrarEventosVista();

            chatWindow.setVisible(true);
            iniciarHiloEscucha();

        } catch (IOException e) {
            if (loginView != null) {
                loginView.mostrarError("No se pudo conectar al servidor.");
            }
        }
    }

    private void registrarEventosVista() {
        // --- EVENTO: DESCONECTARSE ---
        chatWindow.addDisconnectListener(e -> {
            desconectar();
            chatWindow.dispose();
            iniciar();
        });

        // --- EVENTO: CAMBIAR DE CHAT ---
        chatWindow.addContactSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String seleccionado = chatWindow.getSelectedContact();
                if (seleccionado != null) {
                    model.setChatActual(seleccionado); // El modelo recuerda dónde estoy
                    chatWindow.setTitle("Chat App - " + miNombre + " (Chat: " + seleccionado + ")");
                    chatWindow.setChatText(model.getHistorial(seleccionado));
                }
            }
        });

        // --- EVENTO: CREAR GRUPO ---
        chatWindow.addCreateGroupListener(e -> {
            List<String> seleccionados = chatWindow.getSelectedContacts();
            
            // Validación
            if (Validator.esGrupoValido(seleccionados)) {
                String nombreGrupo = Protocol.generarNombreGrupo(seleccionados, miNombre);
                
                if (model.registrarNuevoGrupo(nombreGrupo)) {
                    chatWindow.updateContactList(model.getContactosYGrupos().toArray(new String[0]));
                }
            }
        });

        // --- EVENTO: ENVIAR MENSAJE ---
        chatWindow.addSendButtonListener(e -> {
            String texto = chatWindow.getMessageText();
            String chatActual = model.getChatActual();

            if (!Validator.esMensajeVacio(texto)) {
                try {
                    if (chatActual.equals("TODOS")) {
                        model.enviarATodos(texto);
                    } else {
                        model.enviarPrivado(chatActual, texto);
                    }
                    
                    String miMensajeFormateado = Protocol.formatearMensajePropio(texto);

                    model.guardarEnHistorial(chatActual, miMensajeFormateado);
                    chatWindow.appendMessage(miMensajeFormateado + "\n");
                    
                    chatWindow.clearMessageText();
                } catch (IOException ex) {
                    chatWindow.appendMessage("[SISTEMA] Error al enviar mensaje.\n");
                }
            }
        });
    }

    private void iniciarHiloEscucha() {
        hiloEscucha = new Thread(() -> {
            try {
                while (model.estaConectado()) {
                    String msgRaw = model.recibirMensaje();
                    if (msgRaw == null) break;

                    SwingUtilities.invokeLater(() -> procesarMensajeEntrante(msgRaw.trim()));
                }
            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> {
                    if (chatWindow != null) chatWindow.appendMessage("[SISTEMA] Conexión perdida.\n");
                });
            }
        });
        hiloEscucha.setDaemon(true);
        hiloEscucha.start();
    }

    private void procesarMensajeEntrante(String msg) {
        // Formatear mensaje entrante
        if (Protocol.esActualizacionDeContactos(msg)) {
            List<String> contactosActualizados = Protocol.extraerContactos(msg, miNombre);
            model.actualizarContactosDelServidor(contactosActualizados);
            chatWindow.updateContactList(model.getContactosYGrupos().toArray(new String[0]));
            
        } else {
            String tabDestino = Protocol.determinarTabDestino(msg, miNombre);
            
            // Crear grupo si un mensaje viene de ahí
            if (Validator.esUnGrupo(tabDestino) && model.registrarNuevoGrupo(tabDestino)) {
                chatWindow.updateContactList(model.getContactosYGrupos().toArray(new String[0]));
            }

            model.guardarEnHistorial(tabDestino, msg);

            // Solo renderizamos si el usuario está mirando esa pestaña
            if (model.getChatActual().equals(tabDestino)) {
                chatWindow.appendMessage(msg + "\n");
            }
        }
    }

    public void desconectar() {
        try {
            model.desconectar();
        } catch (IOException e) {
            // Ignorado intencionalmente al salir
        }
        System.exit(0);
    }
}