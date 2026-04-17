package servidor;

import com.sun.tools.javac.Main;

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
                }

                if (respuestaMenu.startsWith("MSG ")){
                    String[] partesMensaje = respuestaMenu.split(" ", 3);
                    String destino = partesMensaje[1];
                    String mensaje = partesMensaje[2];

                    ConexionHilo hiloDestino = MainServidor.usuariosConectados.get(destino);

                    if (hiloDestino != null){
                        hiloDestino.enviarMensaje("Mensaje de " + nombre + ": " + mensaje);
                    }else{
                        System.out.println("El usuario " + destino + " está desconectado");
                    }
                }
            }
        } catch (IOException e) {
                System.out.println("Cliente desconectado");
        }

    }
}
