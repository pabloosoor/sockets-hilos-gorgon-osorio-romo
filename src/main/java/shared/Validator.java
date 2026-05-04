package shared;

import java.util.List;

/**
 * Responsabilidad única: Validar reglas de negocio y datos de entrada del usuario.
 */
public class Validator {

    public static final int NOMBRE_MIN   = 3;
    public static final int NOMBRE_MAX   = 15;
    public static final String REGEX     = "^[a-zA-Z0-9_]+$";

    // Valida el nombre de usuario para el inicio de sesión.
     
    public static String validarNombre(String nombre) {
        if (nombre == null || nombre.isEmpty())
            return "El nombre no puede estar vacío.";
        if (nombre.length() < NOMBRE_MIN)
            return "Mínimo " + NOMBRE_MIN + " caracteres.";
        if (nombre.length() > NOMBRE_MAX)
            return "Máximo " + NOMBRE_MAX + " caracteres.";
        if (nombre.contains(" "))
            return "Sin espacios. Usá _ en su lugar.";
        if (!nombre.matches(REGEX))
            return "Solo letras, números y guion bajo (_).";
        return null;
    }

    //Valida si la selección de usuarios en la interfaz gráfica cumple con los requisitos 
 
    public static boolean esGrupoValido(List<String> seleccionados) {
        return seleccionados != null 
            && seleccionados.size() >= 2 
            && !seleccionados.contains("TODOS");
    }

    // Determina si un identificador de destino corresponde a un grupo.
   
    public static boolean esUnGrupo(String destino) {
        return destino != null && destino.contains(",");
    }

    // Valida que el texto que el usuario intenta enviar contenga información real.
     public static boolean esMensajeVacio(String texto) {
        return texto == null || texto.isBlank();
    }
}