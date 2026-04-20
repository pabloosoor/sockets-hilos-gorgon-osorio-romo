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

            // Login
            System.out.print("Ingrese su nombre: ");
            String inputNombrePropio = sc.nextLine();
            inputNombrePropio = inputNombrePropio.toUpperCase().trim().replace(" ", "_");
            out.writeUTF(inputNombrePropio);

            // Hilo para recibir mensajes sin bloquear
            Thread hiloEscucha = new Thread(() -> {
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
                    System.out.println("\n[SISTEMA] Conexión finalizada.");
                }
            });
            hiloEscucha.start();

            System.out.println("Comandos: CHAT [nombre], FIN, /all [texto], SALIR");

            String destinoActual = null;

            while (true) {
                System.out.print("> ");
                String teclado = sc.nextLine();

                if (teclado.equalsIgnoreCase("SALIR")) {
                    out.writeUTF("SALIR");
                    break;
                }

                if (teclado.toLowerCase().startsWith("msg ")) {
                    System.out.println("[SISTEMA] El comando MSG fue removido. Usá CHAT [nombre] para iniciar una conversación.");
                    continue;
                }

                if (teclado.toLowerCase().startsWith("chat ")) {
                    // FIN implícito si ya había un chat abierto
                    if (destinoActual != null) {
                        System.out.println("[SISTEMA] Chat con " + destinoActual + " cerrado.");
                    }
                    destinoActual = teclado.substring(5).trim().toLowerCase();
                    if (destinoActual.isEmpty()) {
                        System.out.println("[SISTEMA] Uso: CHAT [nombre]");
                        destinoActual = null;
                        continue;
                    }
                    System.out.println("[SISTEMA] Chateando con " + destinoActual + ". Escribí FIN para salir.");
                    continue;
                }

                if (teclado.equalsIgnoreCase("FIN")) {
                    if (destinoActual == null) {
                        System.out.println("[SISTEMA] No hay ningún chat activo.");
                    } else {
                        System.out.println("[SISTEMA] Chat con " + destinoActual + " finalizado.");
                        destinoActual = null;
                    }
                    continue;
                }

                if (teclado.toLowerCase().startsWith("/all ")) {
                    String texto = teclado.substring(5).trim();
                    out.writeUTF("ALL " + texto);
                    continue;
                }

                if (destinoActual != null) {
                    out.writeUTF("MSG " + destinoActual + " " + teclado);
                } else {
                    System.out.println("[SISTEMA] No hay chat activo. Usá CHAT [nombre] para iniciar uno.");
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