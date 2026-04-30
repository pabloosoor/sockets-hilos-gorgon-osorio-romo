package servidor.model;

import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


//Responsabilidad única: manejar el estado del servidor (usuarios conectados).

public class ServidorModel {

    private final ConcurrentHashMap<String, ConexionHilo> usuariosConectados
            = new ConcurrentHashMap<>();

    public void agregarUsuario(String nombre, ConexionHilo hilo) {
        usuariosConectados.put(nombre, hilo);
    }

    public void removerUsuario(String nombre) {
        usuariosConectados.remove(nombre);
    }

    public ConexionHilo getUsuario(String nombre) {
        return usuariosConectados.get(nombre);
    }

    public boolean existeUsuario(String nombre) {
        return usuariosConectados.containsKey(nombre);
    }

    /** Devuelve lista de usuarios conectados excluyendo a 'excluir'. */
    public String listarUsuarios(String excluir) {
        String lista = usuariosConectados.keySet().stream()
                .filter(n -> !n.equals(excluir))
                .collect(Collectors.joining(", "));
        return lista.isEmpty() ? null : lista;
    }

    public ConcurrentHashMap<String, ConexionHilo> getTodos() {
        return usuariosConectados;
    }
}