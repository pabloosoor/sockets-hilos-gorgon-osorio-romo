package shared;


 //Responsabilidad única: validar datos de entrada del usuario.

public class Validator {

    public static final int NOMBRE_MIN   = 3;
    public static final int NOMBRE_MAX   = 15;
    public static final String REGEX     = "^[a-zA-Z0-9_]+$";


     // Valida el nombre de usuario.
     // @return null si es válido, mensaje de error si no.

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
}