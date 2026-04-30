package shared;


 // Responsabilidad única: centralizar los comandos del protocolo de comunicación.
 // Lo usan tanto cliente como servidor.

public class Protocolo {

    // Cliente → Servidor
    public static final String SALIR     = "SALIR";
    public static final String MSG       = "MSG";       // MSG [destino] [texto]
    public static final String ALL       = "ALL";       // ALL [texto]

    // Servidor → Cliente
    public static final String SISTEMA   = "(Desde el servidor)";

    // Validación de usuario
    public static final int NOMBRE_MIN   = 3;
    public static final int NOMBRE_MAX   = 15;
    public static final String NOMBRE_REGEX = "^[a-zA-Z0-9_]+$";
}