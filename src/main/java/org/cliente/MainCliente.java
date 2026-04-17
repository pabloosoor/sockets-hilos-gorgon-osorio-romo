package org.cliente;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class MainCliente {
    public static void main(String[] args) {
        final String HOST = "127.0.0.1";
        final int PORT = 5000;

        try {
            Socket socket = new Socket(HOST, PORT);
            Scanner sc = new Scanner(System.in);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            //Login
            System.out.print("Ingrese su nombre: ");
            String inputNombrePropio = sc.nextLine();
            out.writeUTF(inputNombrePropio);


            Thread hiloEscucha = new Thread(( )-> {
                try {
                    while (true) {
                        String mensajeRecibido = in.readUTF();

                        if (mensajeRecibido.startsWith("USUARIOS_CONECTADOS:")) {
                            System.out.println("\n[SISTEMA] " + mensajeRecibido);
                        } else {
                            System.out.println("\n[MENSAJE] " + mensajeRecibido);
                        }
                        System.out.print("> ");
                    }
                } catch (IOException e) {
                    System.out.println("\n[SISTEMA] Conexión finalizada de tu lado.");
                }
            });
            hiloEscucha.start();

            System.out.println("Escribe tus comandos (MSG [NOMBRE] [TEXTO], o SALIR):");
            while (true) {
                System.out.print("> ");
                String teclado = sc.nextLine();
                out.writeUTF(teclado);

                if (teclado.equalsIgnoreCase("SALIR")) {
                    break;
                }
            }

            socket.close();
            sc.close();
            System.exit(0);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}