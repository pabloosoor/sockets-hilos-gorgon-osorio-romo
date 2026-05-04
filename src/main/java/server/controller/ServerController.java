package server.controller;

import server.model.ConexionHilo;
import server.model.ServerModel;


  //Responsabilidad única: orquestar la lógica del servidor.

public class ServerController {

    private final ServerModel model;

    public ServerController(ServerModel model) {
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

    public void procesarMensajePrivado(String emisor, String destinatariosTexto, String mensaje) {
        String[] listaDestinatarios = destinatariosTexto.split(",");

       for(String destino : listaDestinatarios) {
           String destinoLimpio = destino.trim();
           
           if(model.existeCliente(destinoLimpio)){
               if(!destinoLimpio.equals(emisor)){
                   ConexionHilo hiloDestino = model.getHiloCliente(destinoLimpio);
                String mensajeFormateado = "[" + emisor + "->" + destinatariosTexto + "]" + mensaje;
                hiloDestino.enviarMensaje(mensajeFormateado);
               }
           }
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