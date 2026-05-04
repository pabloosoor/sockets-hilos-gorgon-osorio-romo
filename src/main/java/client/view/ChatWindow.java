package client.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.util.List; // Importante para devolver listas
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;

public class ChatWindow extends JFrame {

    // Componentes de la UI
    private JList<String> contactList;
    private DefaultListModel<String> contactListModel;
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;
    private JButton btnDesconectar;
    private JButton btnCrearGrupo;

    public ChatWindow() {
        initComponentsManually();
    }

    private void initComponentsManually() {
        setTitle("Chat App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null); // Centrar en pantalla

        //Boton para desconectarse
        btnDesconectar = new JButton("Desconectarse");
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(btnDesconectar);
        this.add(topPanel, BorderLayout.NORTH);
        
        // --- PANEL IZQUIERDO: Lista de Contactos ---
        // Inicializamos el modelo y la lista
        contactListModel = new DefaultListModel<>();
        contactList = new JList<>(contactListModel);
        
        // Permitir múltiple selección
        contactList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
        JScrollPane contactScroll = new JScrollPane(contactList);
        contactScroll.setPreferredSize(new Dimension(200, 0)); 

        // Crear grupo
        btnCrearGrupo = new JButton("Crear Grupo");
        
        // Panel Lista + Botón de grupo abajo
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(contactScroll, BorderLayout.CENTER);
        leftPanel.add(btnCrearGrupo, BorderLayout.SOUTH); 

        // Área de chat 
        JPanel rightPanel = new JPanel(new BorderLayout());

        // Área donde se ven los mensajes
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        JScrollPane chatScroll = new JScrollPane(chatArea);
        rightPanel.add(chatScroll, BorderLayout.CENTER);

        // Panel inferior para escribir y enviar
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 0)); 
        messageField = new JTextField();
        sendButton = new JButton("Enviar");

        bottomPanel.add(messageField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);

        rightPanel.add(bottomPanel, BorderLayout.SOUTH);

        // --- DIVISOR PRINCIPAL ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(200);

        // Agregamos todo a la ventana principal
        getContentPane().add(splitPane);
    }

    // MÉTODOS PARA QUE EL CONTROLADOR INTERACTÚE CON LA VISTA
    public void addDisconnectListener(ActionListener l) {
        btnDesconectar.addActionListener(l);
    }

    public void addCreateGroupListener(ActionListener l) {
        btnCrearGrupo.addActionListener(l);
    }

    // Devuelve una lista (por si selecciona varios)
    public List<String> getSelectedContacts() {
        return contactList.getSelectedValuesList();
    }

    // Permite al controlador agregar el listener al botón de enviar
    public void addSendButtonListener(ActionListener listener) {
        sendButton.addActionListener(listener);
        messageField.addActionListener(listener); 
    }

    // Permite al controlador obtener el texto escrito
    public String getMessageText() {
        return messageField.getText();
    }

    // Permite al controlador limpiar la caja de texto después de enviar
    public void clearMessageText() {
        messageField.setText("");
    }

    // Permite al controlador mostrar un mensaje en la pantalla
    public void appendMessage(String message) {
        chatArea.append(message + "\n");
        // Auto-scroll hacia abajo
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    // Permite al controlador saber con quién querés chatear
    public String getSelectedContact() {
        return contactList.getSelectedValue();
    }

    // Permite al controlador actualizar la lista de conectados
    public void updateContactList(String[] contacts) {
        contactListModel.clear();
        for (String contact : contacts) {
            contactListModel.addElement(contact);
        }
    }
    
    // Obtener la lista actual
    public List<String> getContactListData() {
        List<String> currentData = new ArrayList<>();
        for (int i = 0; i < contactListModel.getSize(); i++) {
            currentData.add(contactListModel.getElementAt(i));
        }
        return currentData;
    }

    public void addContactSelectionListener(ListSelectionListener listener) {
        contactList.addListSelectionListener(listener);
    }

    // Reemplaza todo el texto del chat (para cuando cambiamos de pestaña)
    public void setChatText(String text) {
        chatArea.setText(text);
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }
}