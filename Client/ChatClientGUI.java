import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Cliente de chat con interfaz gráfica moderna
 * Implementa tanto comunicación broadcast como peer-to-peer
 */
public class ChatClientGUI extends UnicastRemoteObject implements ChatClientInterface {
    
    // Colores del tema moderno
    private static final Color PRIMARY_COLOR = new Color(37, 99, 235);      // Azul moderno
    private static final Color SECONDARY_COLOR = new Color(99, 102, 241);   // Púrpura
    private static final Color BACKGROUND_COLOR = new Color(248, 250, 252); // Gris muy claro
    private static final Color CARD_COLOR = Color.WHITE;                    // Blanco
    private static final Color TEXT_COLOR = new Color(51, 65, 85);          // Gris oscuro
    private static final Color BORDER_COLOR = new Color(226, 232, 240);     // Gris claro
    private static final Color SUCCESS_COLOR = new Color(34, 197, 94);      // Verde
    private static final Color WARNING_COLOR = new Color(245, 158, 11);     // Amarillo
    private static final Color ERROR_COLOR = new Color(239, 68, 68);        // Rojo
    
    // Componentes de la GUI
    private JFrame frame;
    private JTextPane chatArea;
    private JTextField messageField;
    private JList<String> userList;
    private DefaultListModel<String> userListModel;
    private JButton sendButton;
    private JButton directMessageButton;
    private JLabel statusLabel;
    private JLabel userCountLabel;
    
    // Variables del cliente
    private String username;
    private ChatServerInterface server;
    private String serverIP;
    private int serverPort;
    
    public ChatClientGUI(String username, String serverIP, int serverPort) throws RemoteException {
        super(0);
        this.username = username;
        this.serverIP = serverIP;
        this.serverPort = serverPort;
        
        // Configurar Look and Feel
        setLookAndFeel();
        initializeGUI();
        connectToServer();
    }
    
    private void setLookAndFeel() {
        // Método simplificado sin configuración especial
        // El Look and Feel por defecto es suficiente
    }
    
    private void initializeGUI() {
        frame = new JFrame();
        frame.setTitle("Chat RMI - " + username);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(900, 650);
        frame.setMinimumSize(new Dimension(700, 500));
        frame.getContentPane().setBackground(BACKGROUND_COLOR);
        
        // Layout principal con márgenes
        frame.setLayout(new BorderLayout(15, 15));
        ((JComponent) frame.getContentPane()).setBorder(new EmptyBorder(15, 15, 15, 15));
        
        createHeaderPanel();
        createChatPanel();
        createSidePanel();
        createInputPanel();
        
        setupEventListeners();
        
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    private void createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(CARD_COLOR);
        headerPanel.setBorder(createModernBorder());
        headerPanel.setPreferredSize(new Dimension(0, 70));
        
        // Título principal
        JLabel titleLabel = new JLabel("Chat Distribuido RMI");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setBorder(new EmptyBorder(15, 20, 5, 20));
        
        // Panel de estado
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        statusPanel.setBackground(CARD_COLOR);
        
        statusLabel = new JLabel("Conectando...");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(WARNING_COLOR);
        
        userCountLabel = new JLabel("0 usuarios conectados");
        userCountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userCountLabel.setForeground(TEXT_COLOR);
        
        statusPanel.add(statusLabel);
        statusPanel.add(userCountLabel);
        
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(statusPanel, BorderLayout.CENTER);
        
        frame.add(headerPanel, BorderLayout.NORTH);
    }
    
    private void createChatPanel() {
        JPanel chatPanel = new JPanel(new BorderLayout());
        chatPanel.setBackground(CARD_COLOR);
        chatPanel.setBorder(createModernBorder());
        
        // Título del chat
        JLabel chatTitle = new JLabel("Conversación");
        chatTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        chatTitle.setForeground(TEXT_COLOR);
        chatTitle.setBorder(new EmptyBorder(15, 20, 10, 20));
        
        // Área de chat moderna
        chatArea = new JTextPane();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        chatArea.setBackground(BACKGROUND_COLOR);
        chatArea.setBorder(new EmptyBorder(15, 15, 15, 15));
        chatArea.setContentType("text/html");
        
        JScrollPane chatScroll = new JScrollPane(chatArea);
        chatScroll.setBorder(null);
        chatScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        chatScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        // Personalizar scrollbar
        chatScroll.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        
        chatPanel.add(chatTitle, BorderLayout.NORTH);
        chatPanel.add(chatScroll, BorderLayout.CENTER);
        
        frame.add(chatPanel, BorderLayout.CENTER);
    }
    
    private void createSidePanel() {
        JPanel sidePanel = new JPanel(new BorderLayout());
        sidePanel.setBackground(CARD_COLOR);
        sidePanel.setBorder(createModernBorder());
        sidePanel.setPreferredSize(new Dimension(220, 0));
        
        // Título de usuarios
        JLabel usersTitle = new JLabel("Usuarios Online");
        usersTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        usersTitle.setForeground(TEXT_COLOR);
        usersTitle.setBorder(new EmptyBorder(15, 20, 10, 20));
        
        // Lista de usuarios moderna
        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userList.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        userList.setBackground(BACKGROUND_COLOR);
        userList.setBorder(new EmptyBorder(10, 15, 10, 15));
        userList.setCellRenderer(new ModernListCellRenderer());
        
        JScrollPane userScroll = new JScrollPane(userList);
        userScroll.setBorder(null);
        userScroll.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        
        sidePanel.add(usersTitle, BorderLayout.NORTH);
        sidePanel.add(userScroll, BorderLayout.CENTER);
        
        frame.add(sidePanel, BorderLayout.EAST);
    }
    
    private void createInputPanel() {
        JPanel inputPanel = new JPanel(new BorderLayout(10, 0));
        inputPanel.setBackground(CARD_COLOR);
        inputPanel.setBorder(createModernBorder());
        inputPanel.setPreferredSize(new Dimension(0, 80));
        
        // Campo de mensaje moderno
        messageField = new JTextField();
        messageField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageField.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(12, 15, 12, 15)
        ));
        messageField.setBackground(BACKGROUND_COLOR);
        
        // Panel de botones
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setBackground(CARD_COLOR);
        
        sendButton = createModernButton("Enviar a Todos", PRIMARY_COLOR);
        directMessageButton = createModernButton("Mensaje Directo", SECONDARY_COLOR);
        
        buttonPanel.add(sendButton);
        buttonPanel.add(directMessageButton);
        
        // Panel contenedor con márgenes
        JPanel container = new JPanel(new BorderLayout(15, 0));
        container.setBackground(CARD_COLOR);
        container.setBorder(new EmptyBorder(15, 20, 15, 20));
        container.add(messageField, BorderLayout.CENTER);
        container.add(buttonPanel, BorderLayout.EAST);
        
        inputPanel.add(container, BorderLayout.CENTER);
        frame.add(inputPanel, BorderLayout.SOUTH);
    }
    
    private JButton createModernButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setBorder(new EmptyBorder(12, 20, 12, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(150, 45));
        
        // Efectos hover
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    private Border createModernBorder() {
        return new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(0, 0, 0, 0)
        );
    }
    
    private void setupEventListeners() {
        sendButton.addActionListener(e -> sendBroadcastMessage());
        directMessageButton.addActionListener(e -> sendDirectMessage());
        messageField.addActionListener(e -> sendBroadcastMessage());
        
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                disconnect();
            }
        });
    }
    
    private void connectToServer() {
        try {
            appendToChat("Conectando al servidor " + serverIP + ":" + serverPort + "...", "system");
            
            String url = "//" + serverIP + ":" + serverPort + "/ChatServer";
            server = (ChatServerInterface) Naming.lookup(url);
            
            boolean registered = server.registerClient(username, this);
            
            if (registered) {
                statusLabel.setText("Conectado como: " + username);
                statusLabel.setForeground(SUCCESS_COLOR);
                
                appendToChat("Conectado exitosamente como: " + username, "success");
                appendToChat("Los mensajes de broadcast llegan a todos los usuarios", "info");
                appendToChat("Selecciona un usuario para mensajes directos", "info");
                
                updateUserList();
                
                Timer pollingTimer = new Timer(2000, e -> {
                    updateUserList();
                    fetchPendingMessages();
                });
                pollingTimer.start();
            } else {
                statusLabel.setText("Error: Nombre en uso");
                statusLabel.setForeground(ERROR_COLOR);
                
                JOptionPane.showMessageDialog(frame, 
                    "El nombre de usuario ya está en uso",
                    "Error", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
            
        } catch (Exception e) {
            statusLabel.setText("Error de conexión");
            statusLabel.setForeground(ERROR_COLOR);
            
            appendToChat("Error al conectar: " + e.getMessage(), "error");
            JOptionPane.showMessageDialog(frame,
                "No se pudo conectar al servidor\n" + e.getMessage(),
                "Error de Conexión", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
    
    private void sendBroadcastMessage() {
        String message = messageField.getText().trim();
        if (message.isEmpty()) return;
        
        try {
            server.broadcastMessage(username, message);
            messageField.setText("");
        } catch (RemoteException e) {
            appendToChat("Error al enviar mensaje: " + e.getMessage(), "error");
        }
    }
    
    private void sendDirectMessage() {
        String selectedUser = userList.getSelectedValue();
        if (selectedUser == null) {
            showModernDialog("Selecciona un usuario de la lista", "Selección requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String message = messageField.getText().trim();
        if (message.isEmpty()) {
            showModernDialog("Escribe un mensaje", "Mensaje vacío", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            server.sendDirectMessage(username, selectedUser, message);
            messageField.setText("");
        } catch (RemoteException e) {
            appendToChat("Error al enviar mensaje directo: " + e.getMessage(), "error");
        }
    }
    
    private void fetchPendingMessages() {
        try {
            Map<String, List<String>> pending = server.getPendingMessages(username);
            List<String> messages = pending.getOrDefault("messages", new ArrayList<>());
            
            for (String msg : messages) {
                if (msg.contains("→ Tú (Directo)")) {
                    appendToChat(msg, "direct");
                } else if (msg.startsWith("[" + username + "]")) {
                    appendToChat(msg, "own");
                } else {
                    appendToChat(msg, "broadcast");
                }
            }
        } catch (RemoteException e) {
            System.err.println("Error al obtener mensajes: " + e.getMessage());
        }
    }
    
    private void updateUserList() {
        try {
            List<String> users = server.getOnlineUsers();
            SwingUtilities.invokeLater(() -> {
                userListModel.clear();
                for (String user : users) {
                    if (!user.equals(username)) {
                        userListModel.addElement(user);
                    }
                }
                userCountLabel.setText(users.size() + " usuarios conectados");
            });
        } catch (RemoteException e) {
            System.err.println("Error al actualizar lista: " + e.getMessage());
        }
    }
    
    private void appendToChat(String text, String type) {
        SwingUtilities.invokeLater(() -> {
            String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
            String color = getColorForType(type);
            String icon = getIconForType(type);
            
            String html = String.format(
                "<div style='margin: 8px 0; padding: 8px 12px; background-color: %s; border-radius: 8px; font-family: Segoe UI;'>" +
                "<span style='color: #64748b; font-size: 11px;'>%s</span> " +
                "<span style='color: %s;'>%s %s</span>" +
                "</div>",
                getBackgroundForType(type), time, color, icon, text
            );
            
            try {
                javax.swing.text.html.HTMLDocument doc = (javax.swing.text.html.HTMLDocument) chatArea.getDocument();
                javax.swing.text.html.HTMLEditorKit kit = (javax.swing.text.html.HTMLEditorKit) chatArea.getEditorKit();
                kit.insertHTML(doc, doc.getLength(), html, 0, 0, null);
                chatArea.setCaretPosition(chatArea.getDocument().getLength());
            } catch (Exception e) {
                // Fallback a texto plano
                chatArea.setText(chatArea.getText() + "[" + time + "] " + text + "\n");
            }
        });
    }
    
    private String getColorForType(String type) {
        switch (type) {
            case "success": return "#22c55e";
            case "error": return "#ef4444";
            case "warning": return "#f59e0b";
            case "direct": return "#8b5cf6";
            case "own": return "#3b82f6";
            case "system": return "#64748b";
            default: return "#374151";
        }
    }
    
    private String getBackgroundForType(String type) {
        switch (type) {
            case "success": return "#f0fdf4";
            case "error": return "#fef2f2";
            case "warning": return "#fffbeb";
            case "direct": return "#faf5ff";
            case "own": return "#eff6ff";
            case "system": return "#f8fafc";
            default: return "#ffffff";
        }
    }
    
    private String getIconForType(String type) {
        switch (type) {
            case "success": return "[OK]";
            case "error": return "[ERROR]";
            case "warning": return "[WARN]";
            case "direct": return "[DIRECTO]";
            case "own": return "[ENVIADO]";
            case "system": return "[SISTEMA]";
            default: return "[CHAT]";
        }
    }
    
    private void showModernDialog(String message, String title, int type) {
        JOptionPane.showMessageDialog(frame, message, title, type);
    }
    
    private void disconnect() {
        try {
            if (server != null) {
                server.unregisterClient(username);
            }
            appendToChat("Desconectado del servidor", "system");
        } catch (RemoteException e) {
            appendToChat("Error al desconectar", "error");
        } finally {
            System.exit(0);
        }
    }
    
    // Implementación de ChatClientInterface
    @Override
    public void receiveMessage(String from, String message, boolean isDirect) throws RemoteException {
        String prefix = isDirect ? "[" + from + " → Tú (Directo)] " : "[" + from + "] ";
        String type = isDirect ? "direct" : "broadcast";
        appendToChat(prefix + message, type);
    }
    
    @Override
    public void userJoined(String username) throws RemoteException {
        appendToChat(username + " se ha conectado", "success");
        updateUserList();
    }
    
    @Override
    public void userLeft(String username) throws RemoteException {
        appendToChat(username + " se ha desconectado", "warning");
        updateUserList();
    }
    
    @Override
    public String getUsername() throws RemoteException {
        return username;
    }
    
    // Clases auxiliares para el diseño moderno
    private static class ModernScrollBarUI extends javax.swing.plaf.basic.BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = new Color(203, 213, 225);
            this.trackColor = new Color(248, 250, 252);
        }
        
        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createZeroButton();
        }
        
        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createZeroButton();
        }
        
        private JButton createZeroButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            return button;
        }
    }
    
    private static class ModernListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            setBorder(new EmptyBorder(8, 12, 8, 12));
            setFont(new Font("Segoe UI", Font.PLAIN, 13));
            
            if (isSelected) {
                setBackground(PRIMARY_COLOR);
                setForeground(Color.WHITE);
            } else {
                setBackground(Color.WHITE);
                setForeground(TEXT_COLOR);
            }
            
            setText(value.toString());
            return this;
        }
    }
    
    public static void main(String[] args) {
        showConnectionDialog();
    }
    
    private static void showConnectionDialog() {
        // Crear ventana de configuración profesional
        JDialog dialog = new JDialog((Frame) null, "Configuración del Chat RMI", true);
        dialog.setSize(450, 350);
        dialog.setLocationRelativeTo(null);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.getContentPane().setBackground(BACKGROUND_COLOR);
        
        // Panel principal con diseño moderno
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(25, 25, 25, 25));
        
        // Header con título e icono
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(CARD_COLOR);
        headerPanel.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel titleLabel = new JLabel("Chat Distribuido RMI");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel subtitleLabel = new JLabel("Ingresa tus datos de conexión");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(TEXT_COLOR);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        // Panel de formulario
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(CARD_COLOR);
        formPanel.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(25, 25, 25, 25)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Campos de entrada estilizados
        JTextField usernameField = createStyledTextField("", "Ej: Juan123");
        JTextField serverField = createStyledTextField("192.168.100.144", "IP del servidor");
        JTextField portField = createStyledTextField("1099", "Puerto RMI");
        
        // Labels estilizados
        JLabel userLabel = createStyledLabel("Nombre de usuario:");
        JLabel serverLabel = createStyledLabel("Dirección IP del servidor:");
        JLabel portLabel = createStyledLabel("Puerto:");
        
        // Agregar componentes al formulario
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
        formPanel.add(userLabel, gbc);
        gbc.gridy = 1;
        formPanel.add(usernameField, gbc);
        
        gbc.gridy = 2;
        formPanel.add(serverLabel, gbc);
        gbc.gridy = 3;
        formPanel.add(serverField, gbc);
        
        gbc.gridy = 4;
        formPanel.add(portLabel, gbc);
        gbc.gridy = 5;
        formPanel.add(portField, gbc);
        
        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setBackground(CARD_COLOR);
        buttonPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
        
        JButton cancelButton = createDialogButton("Cancelar", new Color(107, 114, 128));
        JButton connectButton = createDialogButton("Conectar", PRIMARY_COLOR);
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(connectButton);
        
        formPanel.add(buttonPanel, gbc.gridy = 6);
        
        // Ensamblar diálogo
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        dialog.add(mainPanel);
        
        // Event listeners
        cancelButton.addActionListener(e -> {
            dialog.dispose();
            System.exit(0);
        });
        
        connectButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String serverIP = serverField.getText().trim();
            String portStr = portField.getText().trim();
            
            if (validateInput(username, serverIP, portStr)) {
                try {
                    String clientIP = java.net.InetAddress.getLocalHost().getHostAddress();
                    System.setProperty("java.rmi.server.hostname", clientIP);
                    
                    int port = Integer.parseInt(portStr);
                    dialog.dispose();
                    new ChatClientGUI(username, serverIP, port);
                    
                } catch (NumberFormatException ex) {
                    showErrorDialog("El puerto debe ser un número válido", "Error de Formato");
                } catch (Exception ex) {
                    showErrorDialog("Error al iniciar cliente: " + ex.getMessage(), "Error de Conexión");
                    ex.printStackTrace();
                    System.exit(1);
                }
            }
        });
        
        // Enter para conectar
        usernameField.addActionListener(e -> connectButton.doClick());
        serverField.addActionListener(e -> connectButton.doClick());
        portField.addActionListener(e -> connectButton.doClick());
        
        dialog.setVisible(true);
    }
    
    private static JTextField createStyledTextField(String defaultText, String placeholder) {
        JTextField field = new JTextField(defaultText, 20);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(10, 12, 10, 12)
        ));
        field.setBackground(BACKGROUND_COLOR);
        field.setForeground(TEXT_COLOR);
        
        // Placeholder effect
        if (defaultText.isEmpty()) {
            field.setText(placeholder);
            field.setForeground(Color.GRAY);
            
            field.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (field.getText().equals(placeholder)) {
                        field.setText("");
                        field.setForeground(TEXT_COLOR);
                    }
                }
                
                @Override
                public void focusLost(FocusEvent e) {
                    if (field.getText().isEmpty()) {
                        field.setText(placeholder);
                        field.setForeground(Color.GRAY);
                    }
                }
            });
        }
        
        return field;
    }
    
    private static JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(TEXT_COLOR);
        return label;
    }
    
    private static JButton createDialogButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(100, 35));
        
        // Efectos hover
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    private static boolean validateInput(String username, String serverIP, String portStr) {
        if (username.isEmpty() || username.equals("Ej: Juan123")) {
            showErrorDialog("Por favor ingresa un nombre de usuario válido", "Campo Requerido");
            return false;
        }
        
        if (serverIP.isEmpty() || serverIP.equals("IP del servidor")) {
            showErrorDialog("Por favor ingresa la dirección IP del servidor", "Campo Requerido");
            return false;
        }
        
        if (portStr.isEmpty() || portStr.equals("Puerto RMI")) {
            showErrorDialog("Por favor ingresa el puerto del servidor", "Campo Requerido");
            return false;
        }
        
        try {
            int port = Integer.parseInt(portStr);
            if (port < 1 || port > 65535) {
                showErrorDialog("El puerto debe estar entre 1 y 65535", "Puerto Inválido");
                return false;
            }
        } catch (NumberFormatException e) {
            showErrorDialog("El puerto debe ser un número válido", "Formato Incorrecto");
            return false;
        }
        
        return true;
    }
    
    private static void showErrorDialog(String message, String title) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
    }

}