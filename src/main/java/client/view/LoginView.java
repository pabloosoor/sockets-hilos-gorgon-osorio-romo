package client.view;

import client.controller.ClientController;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginView extends JFrame {

    private JTextField txtNombre;
    private JButton btnConectar;
    private ClientController controller;

    // El constructor recibe el controlador para poder avisarle cuando nos conectamos
    public LoginView(ClientController controller) {
        this.controller = controller;
        initComponents();
    }

    private void initComponents() {
        setTitle("Ingreso al Chat");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 150);
        setLocationRelativeTo(null);
        setResizable(false);

       
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblInstruccion = new JLabel("Ingresá tu nombre de usuario:", SwingConstants.CENTER);
        txtNombre = new JTextField();
        btnConectar = new JButton("Conectar");

        panel.add(lblInstruccion);
        panel.add(txtNombre);
        panel.add(btnConectar);

        add(panel);

        // --- Eventos ---
        
        //Clic al boton
        btnConectar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                intentarConexion();
            }
        });

        // Apretar enter
        txtNombre.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                intentarConexion();
            }
        });
    }

    private void intentarConexion() {
        String nombre = txtNombre.getText().trim();
        if (!nombre.isEmpty()) {
            controller.conectar(nombre);
        } else {
            mostrarError("El nombre no puede estar vacío.");
        }
    }

    // ClientController llama si algo falla al conectar
    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}