package shared;

//Responsabilidad única: Establecer los comandos

public class Protocol{
    // Cliente → Servidor
    public static final String SALIR = "SALIR";
    public static final String MSG   = "MSG";
    public static final String ALL   = "ALL";

    // Servidor → Cliente
    public static final String SISTEMA = "(Desde el servidor)";
}