package gui;

import javax.swing.*;
import java.awt.*;
import enums.Role;
import models.User;
import repository.UserRepository;

public class LoginScreen extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<Role> roleComboBox;
    private JButton loginButton;
    private JLabel messageLabel;
    private final UserRepository userRepo = new UserRepository();

    private static final Color BROWN = new Color(44, 30, 22);
    private static final Color BEIGE = new Color(245, 242, 235);
    private static final Color TEXT_DARK = new Color(60, 45, 35);
    private static final Color TEXT_MUTED = new Color(130, 115, 105);
    private static final Color INPUT_LINE = new Color(200, 185, 175);

    public LoginScreen() {
        setTitle("CafeSync - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 680);
        setLocationRelativeTo(null);
        setResizable(false);
        buildUI();
    }

    private void buildUI() {
        JPanel contentPane = new JPanel(new BorderLayout());

        // left panel
        JPanel brandPanel = new JPanel(new GridBagLayout());
        brandPanel.setBackground(BROWN);
        brandPanel.setPreferredSize(new Dimension(320, 450));
        GridBagConstraints gbcBrand = new GridBagConstraints();
        gbcBrand.gridx = 0;
        gbcBrand.gridy = 0;

        JLabel logoText = new JLabel("CafeSync");
        logoText.setFont(new Font("Serif", Font.BOLD, 42));
        logoText.setForeground(Color.WHITE);
        brandPanel.add(logoText, gbcBrand);
        gbcBrand.gridy = 1;
        gbcBrand.insets = new Insets(5, 0, 0, 0);
        JLabel subText = new JLabel("Point of Sale System");
        subText.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subText.setForeground(new Color(200, 180, 160));
        brandPanel.add(subText, gbcBrand);

        // right panel
        JPanel rightOuterPanel = new JPanel(new GridBagLayout());
        rightOuterPanel.setBackground(BEIGE);
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(BEIGE);
        formPanel.setPreferredSize(new Dimension(280, 320));

        JLabel loginTitle = new JLabel("Welcome Back");
        loginTitle.setFont(new Font("SansSerif", Font.BOLD, 28));
        loginTitle.setForeground(TEXT_DARK);
        loginTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(loginTitle);
        formPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // role dropdown
        JLabel roleLabel = createMicroLabel("ROLE");
        formPanel.add(roleLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        roleComboBox = new JComboBox<>(Role.values());
        roleComboBox.setBackground(Color.WHITE);
        roleComboBox.setForeground(TEXT_DARK);
        roleComboBox.setFont(new Font("SansSerif", Font.PLAIN, 14));
        roleComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        roleComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(roleComboBox);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel userLabel = createMicroLabel("USERNAME");
        formPanel.add(userLabel);
        usernameField = new JTextField();
        styleMinimalInput(usernameField);
        formPanel.add(usernameField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel passLabel = createMicroLabel("PASSWORD");
        formPanel.add(passLabel);
        passwordField = new JPasswordField();
        styleMinimalInput(passwordField);
        formPanel.add(passwordField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 35)));

        loginButton = new JButton("Sign In");
        loginButton.setBackground(BROWN);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setOpaque(true);
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        formPanel.add(loginButton);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        messageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(messageLabel);

        loginButton.addActionListener(e -> handleLogin());
        passwordField.addActionListener(e -> handleLogin());

        rightOuterPanel.add(formPanel);

        contentPane.add(brandPanel, BorderLayout.WEST);
        contentPane.add(rightOuterPanel, BorderLayout.CENTER);
        setContentPane(contentPane);
    }

    // helper styling methods
    private JLabel createMicroLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 11));
        label.setForeground(TEXT_MUTED);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private void styleMinimalInput(JTextField field) {
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        field.setFont(new Font("SansSerif", Font.PLAIN, 15));
        field.setForeground(TEXT_DARK);
        field.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, INPUT_LINE));
        field.setBackground(BEIGE);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    // backend
    private void handleLogin() {
        String enteredUsername = usernameField.getText().trim();
        String enteredPassword = new String(passwordField.getPassword());
        Role selectedRole = (Role) roleComboBox.getSelectedItem();

        if (enteredUsername.isEmpty() || enteredPassword.isEmpty()) {
            showMessage("Please fill in all fields.", new Color(200, 50, 50));
            return;
        }

        User user = userRepo.findById(enteredUsername);
        if (user == null) {
            showMessage("Username not found.", new Color(200, 50, 50));
            return;
        }
        if (!user.verifyPassword(enteredPassword)) {
            showMessage("Incorrect password.", new Color(200, 50, 50));
            return;
        }
        if (user.getUserRole() != selectedRole) {
            showMessage("Role mismatch for this account.", new Color(200, 50, 50));
            return;
        }
        showMessage("Welcome, " + user.getName() + "!", new Color(34, 139, 34));

        // navigate based on role
        if (selectedRole == Role.CASHIER) {
            dispose(); // close login window
            new POSScreen(user).setVisible(true);
        } else if (selectedRole == Role.MANAGER) {
            dispose(); // close login window
            new MenuManagement(user).setVisible(true);
        }
    }

    private void showMessage(String text, Color color) {
        messageLabel.setText(text);
        messageLabel.setForeground(color);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new LoginScreen().setVisible(true));
    }
}