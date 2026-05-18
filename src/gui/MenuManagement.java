package gui;

import enums.Category;
import models.MenuItem;
import models.User;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class MenuManagement extends JFrame {
    private static final Color BROWN = new Color(44, 30, 22);
    private static final Color BEIGE = new Color(245, 242, 235);
    private static final Color CARD_BG = new Color(255, 255, 255);
    private static final Color TEXT_DARK = new Color(60, 45, 35);
    private static final Color TEXT_MUTED = new Color(130, 115, 105);
    private static final Color ACCENT = new Color(196, 123, 87);
    private static final Color DIVIDER = new Color(230, 225, 218);
    private static final Color REMOVE_RED = new Color(200, 60, 60);
    private static final Color TEAL = new Color(52, 152, 152);

    private final User manager;
    private final List<MenuItem> menuItems = new ArrayList<>();
    private boolean editMode = false;

    private JPanel menuGrid;
    private JTextField fldId;
    private JTextField fldName;
    private JTextField fldPrice;
    private JComboBox<Category> fldCategory;
    private JTextArea fldDesc;
    private JTextField fldImage;
    private JTextField fldPrepTime;
    private JTextField fldServing;
    private JLabel formHeading;
    private JButton saveBtn;

    public MenuManagement(User manager) {
        this.manager = manager;
        seedDummyData();
        setTitle("CafeSync — Menu Management  |  " + manager.getName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 680);
        setLocationRelativeTo(null);
        setResizable(true);
        buildUI();
    }

    // ── Dummy seed data ──────────────────────────────────────────────────────
    private void seedDummyData() {
        menuItems.add(new MenuItem("Espresso", "ITEM-001", 3.50, "Rich, bold single-shot espresso.", Category.BEVERAGE,
                "https://images.unsplash.com/photo-1510591509098-f4fdc6d0ff04?w=400", 3, 1));
        menuItems.add(new MenuItem("Cappuccino", "ITEM-002", 4.75, "Espresso topped with velvety steamed milk foam.",
                Category.BEVERAGE, "https://images.unsplash.com/photo-1517256064527-09c73fc73e38?w=400", 5, 1));
        menuItems.add(new MenuItem("Avocado Toast", "ITEM-003", 8.50,
                "Sourdough toast with smashed avocado & chilli flakes.", Category.APPETIZER,
                "https://images.unsplash.com/photo-1588137378633-dea1336ce1e2?w=400", 8, 1));
        menuItems.add(new MenuItem("Grilled Chicken", "ITEM-004", 13.90, "Herb-marinated chicken with seasonal greens.",
                Category.MAIN_COURSE, "https://images.unsplash.com/photo-1604908176997-125f25cc6f3d?w=400", 18, 1));
        menuItems
                .add(new MenuItem("Garlic Hummus", "ITEM-005", 5.25, "Creamy hummus with roasted garlic and olive oil.",
                        Category.DIPS, "https://images.unsplash.com/photo-1563374756-80af9ce59f80?w=400", 5, 2));
        menuItems.add(new MenuItem("Beef Burger", "ITEM-006", 14.50,
                "Angus beef patty with lettuce, tomato & special sauce.", Category.MAIN_COURSE,
                "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=400", 15, 1));
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(BEIGE);
        root.add(buildTopBar(), BorderLayout.NORTH);
        root.add(buildMenuArea(), BorderLayout.CENTER);
        root.add(buildFormPanel(), BorderLayout.EAST);
        setContentPane(root);
    }

    // top bar
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(BROWN);
        bar.setPreferredSize(new Dimension(0, 60));
        bar.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 24));

        JLabel logo = new JLabel("CafeSync  ·  Menu Management");
        logo.setFont(new Font("Serif", Font.BOLD, 22));
        logo.setForeground(Color.WHITE);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        right.setOpaque(false);

        JLabel whoLabel = new JLabel("Manager: " + manager.getName());
        whoLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        whoLabel.setForeground(new Color(200, 180, 160));

        JButton analyticsBtn = makeHeaderButton("View Analytics", TEAL);
        analyticsBtn.addActionListener(e -> {
            new AnalyticsScreen(manager).setVisible(true);
        });

        JButton logoutBtn = makeHeaderButton("Logout", REMOVE_RED);
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginScreen().setVisible(true);
        });

        right.add(whoLabel);
        right.add(analyticsBtn);
        right.add(logoutBtn);
        bar.add(logo, BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    // centre-menu
    private JScrollPane buildMenuArea() {
        menuGrid = new JPanel(new GridLayout(0, 3, 14, 14));
        menuGrid.setBackground(BEIGE);
        menuGrid.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 10));

        JScrollPane scroll = new JScrollPane(menuGrid);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setBackground(BEIGE);
        loadMenuGrid();
        return scroll;
    }

    private void loadMenuGrid() {
        menuGrid.removeAll();
        menuGrid.setLayout(new GridLayout(0, 3, 14, 14));
        for (MenuItem item : menuItems) {
            menuGrid.add(buildMenuCard(item));
        }

        menuGrid.revalidate();
        menuGrid.repaint();
    }

    // menu card
    private JPanel buildMenuCard(MenuItem item) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DIVIDER, 1),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)));

        // image
        JPanel banner = new JPanel(new BorderLayout());
        banner.setPreferredSize(new Dimension(0, 90));
        banner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        banner.setBackground(new Color(220, 215, 205));
        banner.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel imgLabel = new JLabel(
                item.getCategory().name().substring(0, 1), SwingConstants.CENTER);
        imgLabel.setFont(new Font("Serif", Font.BOLD, 36));
        imgLabel.setForeground(new Color(255, 255, 255, 140));
        banner.add(imgLabel, BorderLayout.CENTER);

        if (item.getImageUrl() != null && item.getImageUrl().startsWith("http")) {
            new Thread(() -> {
                try {
                    URL url = java.net.URI.create(item.getImageUrl()).toURL();
                    Image img = ImageIO.read(url);
                    if (img != null) {
                        Image scaled = img.getScaledInstance(220, 90, Image.SCALE_SMOOTH);
                        SwingUtilities.invokeLater(() -> {
                            imgLabel.setIcon(new ImageIcon(scaled));
                            imgLabel.setText("");
                        });
                    }
                } catch (Exception ignored) {
                }
            }).start();
        }

        // details
        JLabel nameLbl = new JLabel(item.getItemName());
        nameLbl.setFont(new Font("SansSerif", Font.BOLD, 14));
        nameLbl.setForeground(TEXT_DARK);
        nameLbl.setAlignmentX(LEFT_ALIGNMENT);

        JLabel metaLbl = new JLabel(
                item.getCategory().name() + "  ·  Prep: " + item.getPreparationTime() + " min");
        metaLbl.setFont(new Font("SansSerif", Font.ITALIC, 11));
        metaLbl.setForeground(TEXT_MUTED);
        metaLbl.setAlignmentX(LEFT_ALIGNMENT);

        JLabel priceLbl = new JLabel(String.format("PKR %.2f", item.getPrice()));
        priceLbl.setFont(new Font("SansSerif", Font.BOLD, 15));
        priceLbl.setForeground(ACCENT);
        priceLbl.setAlignmentX(LEFT_ALIGNMENT);

        // buttons
        JPanel btnRow = new JPanel(new GridLayout(1, 2, 8, 0));
        btnRow.setBackground(CARD_BG);
        btnRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        btnRow.setAlignmentX(LEFT_ALIGNMENT);
        JButton editBtn = cardBtn("Edit", BROWN);
        editBtn.addActionListener(e -> populateFormForEdit(item));
        JButton delBtn = cardBtn("Delete", REMOVE_RED);
        delBtn.addActionListener(e -> deleteItem(item));
        btnRow.add(editBtn);
        btnRow.add(delBtn);

        card.add(banner);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(nameLbl);
        card.add(Box.createRigidArea(new Dimension(0, 3)));
        card.add(metaLbl);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(priceLbl);
        card.add(Box.createRigidArea(new Dimension(0, 12)));
        card.add(btnRow);

        return card;
    }

    // editting panel
    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_BG);
        panel.setPreferredSize(new Dimension(310, 0));
        panel.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, DIVIDER));

        // header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(CARD_BG);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, DIVIDER),
                BorderFactory.createEmptyBorder(16, 18, 16, 18)));

        formHeading = new JLabel("Add New Item");
        formHeading.setFont(new Font("SansSerif", Font.BOLD, 18));
        formHeading.setForeground(TEXT_DARK);

        JButton clearBtn = new JButton("Clear");
        clearBtn.setFont(new Font("SansSerif", Font.PLAIN, 11));
        clearBtn.setForeground(TEXT_MUTED);
        clearBtn.setBackground(CARD_BG);
        clearBtn.setBorderPainted(false);
        clearBtn.setFocusPainted(false);
        clearBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearBtn.addActionListener(e -> resetForm());
        header.add(formHeading, BorderLayout.WEST);
        header.add(clearBtn, BorderLayout.EAST);

        JPanel fields = new JPanel();
        fields.setLayout(new BoxLayout(fields, BoxLayout.Y_AXIS));
        fields.setBackground(CARD_BG);
        fields.setBorder(new EmptyBorder(10, 18, 10, 18));
        fldId = addField(fields, "ITEM ID");
        fldName = addField(fields, "NAME");
        fldPrice = addField(fields, "PRICE  (PKR)");

        addLabel(fields, "CATEGORY");
        fields.add(Box.createRigidArea(new Dimension(0, 2)));
        fldCategory = new JComboBox<>(Category.values());
        fldCategory.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        fldCategory.setAlignmentX(Component.LEFT_ALIGNMENT);
        fldCategory.setBackground(Color.WHITE);
        fields.add(fldCategory);
        fields.add(Box.createRigidArea(new Dimension(0, 10)));

        addLabel(fields, "DESCRIPTION");
        fields.add(Box.createRigidArea(new Dimension(0, 2)));
        fldDesc = new JTextArea(2, 1);
        fldDesc.setLineWrap(true);
        fldDesc.setWrapStyleWord(true);
        fldDesc.setFont(new Font("SansSerif", Font.PLAIN, 13));
        fldDesc.setForeground(TEXT_DARK);
        JScrollPane descScroll = new JScrollPane(fldDesc);
        descScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        descScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        descScroll.setBorder(BorderFactory.createLineBorder(DIVIDER));
        fields.add(descScroll);
        fields.add(Box.createRigidArea(new Dimension(0, 10)));

        fldImage = addField(fields, "IMAGE URL");
        fldPrepTime = addField(fields, "PREP TIME  (mins)");
        fldServing = addField(fields, "SERVING SIZE");

        JScrollPane fieldScroll = new JScrollPane(fields);
        fieldScroll.setBorder(null);
        fieldScroll.setBackground(CARD_BG);

        // footer
        JPanel footer = new JPanel();
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        footer.setBackground(CARD_BG);
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, DIVIDER),
                BorderFactory.createEmptyBorder(14, 18, 18, 18)));
        saveBtn = new JButton("Add Item");
        saveBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        saveBtn.setBackground(BROWN);
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);
        saveBtn.setBorderPainted(false);
        saveBtn.setOpaque(true);
        saveBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        saveBtn.setAlignmentX(LEFT_ALIGNMENT);
        saveBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveBtn.addActionListener(e -> saveItem());
        footer.add(saveBtn);
        panel.add(header, BorderLayout.NORTH);
        panel.add(fieldScroll, BorderLayout.CENTER);
        panel.add(footer, BorderLayout.SOUTH);
        return panel;
    }

    // helpers
    private JTextField addField(JPanel container, String labelText) {
        addLabel(container, labelText);
        container.add(Box.createRigidArea(new Dimension(0, 4)));
        JTextField tf = new JTextField();
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        tf.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tf.setForeground(TEXT_DARK);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DIVIDER),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(tf);
        container.add(Box.createRigidArea(new Dimension(0, 10)));
        return tf;
    }

    private void addLabel(JPanel container, String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 10));
        lbl.setForeground(TEXT_MUTED);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(lbl);
    }

    private void populateFormForEdit(MenuItem item) {
        editMode = true;
        formHeading.setText("Edit Item");
        saveBtn.setText("Update Item");
        fldId.setText(item.getID());
        fldName.setText(item.getItemName());
        fldPrice.setText(String.valueOf(item.getPrice()));
        fldCategory.setSelectedItem(item.getCategory());
        fldDesc.setText(item.getDescription());
        fldImage.setText(item.getImageUrl() != null ? item.getImageUrl() : "");
        fldPrepTime.setText(String.valueOf(item.getPreparationTime()));
        fldServing.setText(String.valueOf(item.getServingSize()));
    }

    private void resetForm() {
        editMode = false;
        formHeading.setText("Add New Item");
        saveBtn.setText("Add Item");
        fldId.setText("");
        fldName.setText("");
        fldPrice.setText("");
        fldCategory.setSelectedIndex(0);
        fldDesc.setText("");
        fldImage.setText("");
        fldPrepTime.setText("");
        fldServing.setText("");
    }

    private void saveItem() {
        try {
            String id = fldId.getText().trim();
            String name = fldName.getText().trim();
            String priceStr = fldPrice.getText().trim();
            String desc = fldDesc.getText().trim();
            String imageUrl = fldImage.getText().trim();
            String prepStr = fldPrepTime.getText().trim();
            String servStr = fldServing.getText().trim();

            if (id.isEmpty() || name.isEmpty() || priceStr.isEmpty() || desc.isEmpty()
                    || imageUrl.isEmpty() || prepStr.isEmpty() || servStr.isEmpty()) {
                showError("Please fill in all fields.");
                return;
            }
            double price = Double.parseDouble(priceStr);
            int prep = Integer.parseInt(prepStr);
            int serving = Integer.parseInt(servStr);
            Category cat = (Category) fldCategory.getSelectedItem();

            MenuItem item = new MenuItem(name, id, price, desc, cat, imageUrl, prep, serving);

            if (editMode) {
                for (int i = 0; i < menuItems.size(); i++) {
                    if (menuItems.get(i).getID().equals(item.getID())) {
                        menuItems.set(i, item);
                        break;
                    }
                }
                info("Item \"" + name + "\" updated successfully.");
            } else {
                menuItems.add(item);
                info("Item \"" + name + "\" added successfully.");
            }

            resetForm();
            loadMenuGrid();

        } catch (NumberFormatException ex) {
            showError("Price, Prep Time, and Serving Size must be valid numbers.");
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    private void deleteItem(MenuItem item) {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Delete \"" + item.getItemName() + "\"?  This cannot be undone.",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            menuItems.removeIf(m -> m.getID().equals(item.getID()));
            loadMenuGrid();
        }
    }

    // helpers
    private JButton makeHeaderButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 11));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton cardBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void info(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}
