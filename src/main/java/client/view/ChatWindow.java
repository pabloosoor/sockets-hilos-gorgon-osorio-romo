package client.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;

public class ChatWindow extends JFrame {

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
        setLocationRelativeTo(null);

        // Botón desconectarse arriba
        btnDesconectar = new JButton("Desconectarse");
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(btnDesconectar);
        this.add(topPanel, BorderLayout.NORTH);

        // --- PANEL IZQUIERDO: Lista de Contactos ---
        contactListModel = new DefaultListModel<>();
        contactList = new JList<>(contactListModel);
        contactList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        // Renderer por defecto sin badges al inicio
        contactList.setCellRenderer(new ContactCellRenderer(new HashMap<>()));

        JScrollPane contactScroll = new JScrollPane(contactList);
        contactScroll.setPreferredSize(new Dimension(200, 0));

        btnCrearGrupo = new JButton("Crear Grupo");

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(contactScroll, BorderLayout.CENTER);
        leftPanel.add(btnCrearGrupo, BorderLayout.SOUTH);

        // --- PANEL DERECHO: Área de chat ---
        JPanel rightPanel = new JPanel(new BorderLayout());

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        JScrollPane chatScroll = new JScrollPane(chatArea);
        rightPanel.add(chatScroll, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout(5, 0));
        messageField = new JTextField();
        sendButton = new JButton("Enviar");
        bottomPanel.add(messageField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);
        rightPanel.add(bottomPanel, BorderLayout.SOUTH);

        // --- DIVISOR ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(200);

        getContentPane().add(splitPane);
    }

    // =====================================================
    //   MÉTODOS PÚBLICOS PARA EL CONTROLLER
    // =====================================================

    public void addDisconnectListener(ActionListener l) {
        btnDesconectar.addActionListener(l);
    }

    public void addCreateGroupListener(ActionListener l) {
        btnCrearGrupo.addActionListener(l);
    }

    public void addSendButtonListener(ActionListener listener) {
        sendButton.addActionListener(listener);
        messageField.addActionListener(listener);
    }

    public void addContactSelectionListener(ListSelectionListener listener) {
        contactList.addListSelectionListener(listener);
    }

    public String getMessageText() {
        return messageField.getText();
    }

    public void clearMessageText() {
        messageField.setText("");
    }

    public void appendMessage(String message) {
        chatArea.append(message + "\n");
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    public String getSelectedContact() {
        return contactList.getSelectedValue();
    }

    public List<String> getSelectedContacts() {
        return contactList.getSelectedValuesList();
    }

    public void setChatText(String text) {
        chatArea.setText(text);
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    public List<String> getContactListData() {
        List<String> currentData = new ArrayList<>();
        for (int i = 0; i < contactListModel.getSize(); i++) {
            currentData.add(contactListModel.getElementAt(i));
        }
        return currentData;
    }

    /**
     * Actualiza la lista de contactos con badges de mensajes no leídos.
     */
    public void updateContactList(String[] contacts, Map<String, Integer> noLeidos) {
        contactListModel.clear();
        for (String contact : contacts) {
            contactListModel.addElement(contact);
        }
        contactList.setCellRenderer(new ContactCellRenderer(noLeidos));
        contactList.repaint();
    }

    /**
     * Sobrecarga sin badges — para compatibilidad con llamadas sin contadores.
     */
    public void updateContactList(String[] contacts) {
        updateContactList(contacts, new HashMap<>());
    }

    // =====================================================
    //   RENDERER: dibuja el badge de no leídos en la lista
    // =====================================================

    private static class ContactCellRenderer extends DefaultListCellRenderer {

        private final Map<String, Integer> noLeidos;

        public ContactCellRenderer(Map<String, Integer> noLeidos) {
            this.noLeidos = noLeidos;
        }

        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {

            JLabel label = (JLabel) super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);

            String contacto = value.toString();
            int cantidad = noLeidos.getOrDefault(contacto, 0);

            if (cantidad > 0) {
                label.setText(contacto + "  (" + cantidad + ")");
                label.setFont(label.getFont().deriveFont(Font.BOLD));
                if (!isSelected) {
                    label.setForeground(new Color(0, 120, 215)); // azul
                }
            } else {
                label.setText(contacto);
                label.setFont(label.getFont().deriveFont(Font.PLAIN));
                if (!isSelected) {
                    label.setForeground(list.getForeground());
                }
            }

            return label;
        }
    }
}