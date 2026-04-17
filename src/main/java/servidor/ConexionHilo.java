package servidor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ConexionHilo extends Thread{
    public DataInputStream in;
    public DataOutputStream out;
    public String nombre;

    public ConexionHilo(DataInputStream in, DataOutputStream out, String nombre) {
        this.in = in;
        this.out = out;
        this.nombre = nombre;
    }

    public void enviarMensaje(String mensaje) {
        try {
            out.writeUTF(mensaje);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true){
                String respuestaMenu = in.readUTF();

                if (respuestaMenu.equalsIgnoreCase("SALIR")) {
                    MainServidor.usuariosConectados.remove(nombre);
                    MainServidor.notificarUsuarios();
                    break;
                }

                if (respuestaMenu.startsWith("MSG ")){
                    String[] partesMensaje = respuestaMenu.split(" ", 3);
                    
                    if (partesMensaje.length < 3) {
                        enviarMensaje("(Sistema) Error de formato. Usá: MSG [NOMBRE] [TEXTO]");
                        continue;
                    }
                    
                    String destino = partesMensaje[1];
                    String mensaje = partesMensaje[2];

                    ConexionHilo hiloDestino = MainServidor.usuariosConectados.get(destino);

                    if (hiloDestino != null){
                        hiloDestino.enviarMensaje("Mensaje de " + nombre + ": " + mensaje);
                    }else{
                        enviarMensaje("(Sistema) El usuario " + destino + " no existe o está desconectado.");                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Cliente desconectado abruptamente: " + nombre);
            MainServidor.usuariosConectados.remove(nombre);
            MainServidor.notificarUsuarios();
        }

    }
}
