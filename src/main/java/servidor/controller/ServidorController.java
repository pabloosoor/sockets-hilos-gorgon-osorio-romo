package servidor.controller;

import servidor.model.ConexionHilo;
import servidor.model.ServidorModel;


  //Responsabilidad única: orquestar la lógica del servidor.

public class ServidorController {

    private final ServidorModel model;

    public ServidorController(ServidorModel model) {
        this.model = model;
    }

    public void clienteConectado(String nombre, ConexionHilo hilo) {
        model.agregarUsuario(nombre, hilo);
        System.out.println("[Servidor] Conectado: " + nombre);
        notificarUsuarios();
    }

    public void clienteDesconectado(String nombre) {
        model.removerUsuario(nombre);
        System.out.println("[Servidor] Desconectado: " + nombre);
        notificarUsuarios();
    }

    public void procesarMensajePrivado(String origen, String msg) {
        String[] partes = msg.split(" ", 3);
        if (partes.length < 3) {
            model.getUsuario(origen)
                    .enviarMensaje("[Sistema] Formato: MSG [nombre] [texto]");
            return;
        }
        String destino  = partes[1].trim().toLowerCase();
        String texto    = partes[2];
        ConexionHilo hiloDestino = model.getUsuario(destino);

        if (hiloDestino != null) {
            hiloDestino.enviarMensaje("[" + origen + " → vos] " + texto);
        } else {
            model.getUsuario(origen)
                    .enviarMensaje("[Sistema] Usuario " + destino + " no encontrado.");
        }
    }

    public void procesarMensajeTodos(String origen, String msg) {
        String texto = msg.substring(4).trim();
        model.getTodos().forEach((nombre, hilo) -> {
            if (!nombre.equals(origen))
                hilo.enviarMensaje("[" + origen + " → TODOS] " + texto);
        });
    }

    private void notificarUsuarios() {
        model.getTodos().forEach((nombre, hilo) -> {
            String lista = model.listarUsuarios(nombre);
            String aviso = (lista == null)
                    ? "[Sistema] No hay otros usuarios conectados."
                    : "[Sistema] Usuarios conectados: " + lista;
            hilo.enviarMensaje(aviso);
        });
    }
}