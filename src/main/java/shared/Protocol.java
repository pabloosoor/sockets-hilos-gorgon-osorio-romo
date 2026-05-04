package shared;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Protocol {

    // Cliente → Servidor
    public static final String SALIR = "SALIR";
    public static final String MSG = "MSG";
    public static final String ALL = "ALL";

    // Servidor → Cliente
    public static final String SISTEMA = "(Desde el servidor)";

    /**
     * Verifica si el mensaje entrante es una lista de usuarios conectados.
     */
    public static boolean esActualizacionDeContactos(String msg) {
        return msg != null && msg.contains("Usuarios conectados:");
    }

    /**
     * Parsea el mensaje del servidor y devuelve una lista limpia de los
     * contactos disponibles, excluyendo al propio usuario.
     */
    public static List<String> extraerContactos(String msg, String miNombre) {
        List<String> contactos = new ArrayList<>();
        String[] partes = msg.split(":");

        if (partes.length > 1) {
            String[] usuarios = partes[1].trim().split(",");
            for (String u : usuarios) {
                String userLimpiado = u.trim();
                // Excluimos nuestro propio nombre y valores vacíos
                if (!userLimpiado.equals(miNombre) && !userLimpiado.isEmpty()) {
                    contactos.add(userLimpiado);
                }
            }
        }
        return contactos;
    }

    /**
     * Analiza las cabeceras de los mensajes [Emisor -> Destinatario] para saber
     * a qué pestaña del chat debe ir dirigido.
     */
    public static String determinarTabDestino(String msg, String miNombre) {
        String tabDestino = "TODOS"; // Valor por defecto

        int inicioCorchete = msg.indexOf("[");
        int finCorchete = msg.indexOf("]");

        if (inicioCorchete >= 0 && finCorchete > inicioCorchete) {
            String cabecera = msg.substring(inicioCorchete + 1, finCorchete);

            if (cabecera.contains("->") || cabecera.contains("→")) {
                String[] partesCabecera = cabecera.split("->|→");

                if (partesCabecera.length >= 2) {
                    String emisor = partesCabecera[0].trim();
                    String destinatario = partesCabecera[1].trim();

                    // Si el mensaje es para mí directamente, va a la pestaña del emisor
                    if (destinatario.equalsIgnoreCase("vos") || destinatario.equalsIgnoreCase(miNombre)) {
                        tabDestino = emisor;
                    } // Si el mensaje es para un grupo o persona que no soy yo, y no es "TODOS"
                    else if (!destinatario.equalsIgnoreCase("TODOS")) {
                        tabDestino = destinatario;
                    }
                }
            } else if (cabecera.toLowerCase().contains("sistema")) {
                tabDestino = "TODOS";
            }
        }

        return tabDestino;
    }

    /**
     * Construye el identificador único para un grupo basándose en sus
     * integrantes. Los ordena alfabéticamente para que "Juan,Pedro" sea el
     * mismo grupo que "Pedro,Juan".
     */
    public static String generarNombreGrupo(List<String> integrantesSeleccionados, String miNombre) {
        List<String> integrantes = new ArrayList<>(integrantesSeleccionados);
        if (!integrantes.contains(miNombre)) {
            integrantes.add(miNombre);
        }
        Collections.sort(integrantes);
        return String.join(",", integrantes);
    }

    /**
     * Da formato a los mensajes que envía el propio cliente para visualizarlos
     * en su historial.
     */
    public static String formatearMensajePropio(String texto) {
        return "Yo: " + texto;
    }
    public static String formatearMensajeEntrante(String msg) {
        int finCorchete = msg.indexOf("]");
        
        // Verificamos que tenga el formato pedido
        if (msg.startsWith("[") && finCorchete > 0) {
            String cabecera = msg.substring(1, finCorchete);
            String textoMensaje = msg.substring(finCorchete + 1).trim();

            if (cabecera.contains("->") || cabecera.contains("→")) {
                String[] partesCabecera = cabecera.split("->|→");
                String emisor = partesCabecera[0].trim();
                
                return emisor + ": " + textoMensaje;
                
            } else if (cabecera.toLowerCase().contains("sistema")) {
                return "SISTEMA: " + textoMensaje;
            }
        }
        
        return msg; 
    }
}
