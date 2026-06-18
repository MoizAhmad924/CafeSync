package gui;

import enums.Category;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import models.MenuItem;
import models.User;
import repository.MenuRepository;

public class MenuManagement extends JFrame {

    // ── Palette (matches POS) ──────────────────────────────────────────────────
    private static final Color SIDEBAR_BG    = new Color(26, 20, 16);
    private static final Color SIDEBAR_HOVER = new Color(44, 34, 26);
    private static final Color SIDEBAR_ACTIVE= new Color(55, 42, 32);
    private static final Color CANVAS_BG     = new Color(247, 245, 241);
    private static final Color CARD_BG       = new Color(255, 255, 255);
    private static final Color AMBER         = new Color(212, 135, 90);
    private static final Color AMBER_DARK    = new Color(185, 108, 62);
    private static final Color TEAL          = new Color(52, 152, 140);
    private static final Color TEXT_DARK     = new Color(28, 22, 16);
    private static final Color TEXT_MID      = new Color(90, 75, 65);
    private static final Color TEXT_MUTED    = new Color(155, 142, 132);
    private static final Color BORDER_COLOR  = new Color(234, 230, 224);
    private static final Color RED_SOFT      = new Color(210, 70, 70);
    private static final Color FIELD_BG      = new Color(250, 248, 245);

    private static final int RADIUS = 14;

    // ── State ──────────────────────────────────────────────────────────────────
    private final User manager;
    private final MenuRepository menuRepo  = new MenuRepository();
    private final List<MenuItem> menuItems = new ArrayList<>();
    private boolean editMode      = false;
    private String  thisEditingId = null;

    // ── Form widgets ───────────────────────────────────────────────────────────
    private JPanel     menuGrid;
    private JTextField fldName;
    private JTextField fldPrice;
    private JComboBox<Category> fldCategory;
    private JTextArea  fldDesc;
    private JTextField fldImage;
    private JTextField fldPrepTime;
    private JTextField fldServing;
    private JLabel     formHeading;
    private JButton    saveBtn;

    // ══════════════════════════════════════════════════════════════════════════
    public MenuManagement(User manager) {
        this.manager = manager;
        loadFromRepository();
        setTitle("CafeSync — Menu Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1260, 760);
        setMinimumSize(new Dimension(1000, 600));
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(true);
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        buildUI();
    }

    private void loadFromRepository() {
        menuItems.clear();
        List<MenuItem> loaded = menuRepo.findAll();
        if (loaded != null) menuItems.addAll(loaded);
    }

    // ── Root layout ────────────────────────────────────────────────────────────
    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(CANVAS_BG);
        root.add(buildSidebar(),   BorderLayout.WEST);
        root.add(buildMenuArea(),  BorderLayout.CENTER);
        root.add(buildFormPanel(), BorderLayout.EAST);
        setContentPane(root);
    }

    // ── Left Sidebar ───────────────────────────────────────────────────────────
// ═══════════════════════════════════════════════════════════════════════════
// REPLACEMENT: buildSidebar(), sidebarSectionLabel(), sidebarNavItem(),
//              sidebarNavItemClickable()  —  for MenuManagement.java
// ═══════════════════════════════════════════════════════════════════════════

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(170, 0));
        sidebar.setMaximumSize(new Dimension(220, Integer.MAX_VALUE));

        // ── Logo block ────────────────────────────────────────────────────
        JPanel logoArea = new JPanel();
        logoArea.setLayout(new BoxLayout(logoArea, BoxLayout.Y_AXIS));
        logoArea.setBackground(SIDEBAR_BG);
        logoArea.setAlignmentX(LEFT_ALIGNMENT);
        logoArea.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        logoArea.setMinimumSize(new Dimension(0, 80));
        logoArea.setBorder(BorderFactory.createEmptyBorder(22, 20, 18, 20));

        JLabel logoText = new JLabel("CafeSync");
        logoText.setFont(new Font("Serif", Font.BOLD, 20));
        logoText.setForeground(Color.WHITE);
        logoText.setAlignmentX(LEFT_ALIGNMENT);

        JLabel logoSub = new JLabel("Menu Management");
        logoSub.setFont(new Font("SansSerif", Font.PLAIN, 11));
        logoSub.setForeground(new Color(155, 135, 115));
        logoSub.setAlignmentX(LEFT_ALIGNMENT);

        logoArea.add(logoText);
        logoArea.add(Box.createRigidArea(new Dimension(0, 2)));
        logoArea.add(logoSub);

        // amber accent line
        JPanel accentLine = new JPanel();
        accentLine.setBackground(AMBER);
        accentLine.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        accentLine.setPreferredSize(new Dimension(0, 2));
        accentLine.setAlignmentX(LEFT_ALIGNMENT);

        sidebar.add(logoArea);
        sidebar.add(accentLine);
        sidebar.add(Box.createRigidArea(new Dimension(0, 16)));

        // ── Navigation section ────────────────────────────────────────────
        sidebar.add(sidebarSectionLabel("NAVIGATION"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 4)));
        sidebar.add(sidebarNavItem("Menu Items", "▤", true));
        sidebar.add(sidebarNavItemClickable("Analytics", "↗", false,
                e -> openAnalyticsScreen()));

        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));

        // ── Session section ───────────────────────────────────────────────
        sidebar.add(sidebarSectionLabel("SESSION"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 4)));

        // Manager identity row
        JPanel managerPill = new JPanel();
        managerPill.setLayout(new BoxLayout(managerPill, BoxLayout.X_AXIS));
        managerPill.setBackground(SIDEBAR_ACTIVE);
        managerPill.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        managerPill.setAlignmentX(LEFT_ALIGNMENT);
        managerPill.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 16));

        JPanel initCircle = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(TEAL);
                g2.fillOval(0, 0, 26, 26);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 12));
                FontMetrics fm = g2.getFontMetrics();
                String initial = manager.getName().substring(0, 1).toUpperCase();
                g2.drawString(initial,
                        (26 - fm.stringWidth(initial)) / 2,
                        (26 - fm.getHeight()) / 2 + fm.getAscent());
            }
        };
        initCircle.setPreferredSize(new Dimension(26, 26));
        initCircle.setMinimumSize(new Dimension(26, 26));
        initCircle.setMaximumSize(new Dimension(26, 26));
        initCircle.setOpaque(false);

        JPanel nameStack = new JPanel();
        nameStack.setLayout(new BoxLayout(nameStack, BoxLayout.Y_AXIS));
        nameStack.setBackground(SIDEBAR_ACTIVE);
        nameStack.setAlignmentX(LEFT_ALIGNMENT);

        JLabel nameLabel = new JLabel(manager.getName());
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setAlignmentX(LEFT_ALIGNMENT);

        JLabel roleLabel = new JLabel("Manager");
        roleLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));
        roleLabel.setForeground(new Color(155, 135, 115));
        roleLabel.setAlignmentX(LEFT_ALIGNMENT);

        nameStack.add(nameLabel);
        nameStack.add(roleLabel);

        managerPill.add(initCircle);
        managerPill.add(Box.createRigidArea(new Dimension(10, 0)));
        managerPill.add(nameStack);
        managerPill.add(Box.createHorizontalGlue());

        sidebar.add(managerPill);
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));

        sidebar.add(sidebarNavItemClickable("Logout", "⎋", false, e -> {
            dispose();
            new LoginScreen().setVisible(true);
        }));

        // ── Footer ────────────────────────────────────────────────────────
        sidebar.add(Box.createVerticalGlue());

        JPanel versionRow = new JPanel();
        versionRow.setLayout(new BoxLayout(versionRow, BoxLayout.X_AXIS));
        versionRow.setBackground(SIDEBAR_BG);
        versionRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        versionRow.setAlignmentX(LEFT_ALIGNMENT);
        versionRow.setBorder(BorderFactory.createEmptyBorder(0, 20, 14, 20));

        JLabel version = new JLabel("v2.0  ·  CafeSync");
        version.setFont(new Font("SansSerif", Font.PLAIN, 10));
        version.setForeground(new Color(80, 65, 55));
        versionRow.add(version);
        versionRow.add(Box.createHorizontalGlue());

        sidebar.add(versionRow);
        return sidebar;
    }

    private JLabel sidebarSectionLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 9));
        lbl.setForeground(new Color(100, 85, 75));
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        lbl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 18));
        return lbl;
    }

    private JPanel sidebarNavItem(String label, String icon, boolean active) {
        return sidebarNavItemClickable(label, icon, active, null);
    }

    private JPanel sidebarNavItemClickable(String label, String icon,
                                           boolean active, ActionListener action) {
        JPanel item = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (active) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setColor(AMBER);
                    g2.fillRect(0, 8, 3, getHeight() - 16);
                }
            }
        };
        item.setLayout(new BoxLayout(item, BoxLayout.X_AXIS));
        item.setBackground(active ? SIDEBAR_ACTIVE : SIDEBAR_BG);
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        item.setPreferredSize(new Dimension(220, 42));
        item.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 16));
        item.setAlignmentX(LEFT_ALIGNMENT);
        item.setCursor(action != null
                ? new Cursor(Cursor.HAND_CURSOR)
                : Cursor.getDefaultCursor());

        JLabel iconLbl = new JLabel(icon);
        iconLbl.setFont(new Font("SansSerif", Font.PLAIN, 14));
        iconLbl.setForeground(active ? AMBER : new Color(155, 135, 115));
        iconLbl.setPreferredSize(new Dimension(22, 20));
        iconLbl.setMinimumSize(new Dimension(22, 20));
        iconLbl.setMaximumSize(new Dimension(22, 20));

        JLabel nameLbl = new JLabel(label);
        nameLbl.setFont(new Font("SansSerif", active ? Font.BOLD : Font.PLAIN, 13));
        nameLbl.setForeground(active ? Color.WHITE : new Color(180, 165, 150));

        item.add(iconLbl);
        item.add(Box.createRigidArea(new Dimension(10, 0)));
        item.add(nameLbl);
        item.add(Box.createHorizontalGlue());   // pushes content LEFT

        if (action != null) {
            item.addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) {
                    item.setBackground(SIDEBAR_HOVER);
                }
                @Override public void mouseExited(MouseEvent e) {
                    item.setBackground(active ? SIDEBAR_ACTIVE : SIDEBAR_BG);
                }
                @Override public void mouseClicked(MouseEvent e) {
                    action.actionPerformed(new ActionEvent(item, 0, ""));
                }
            });
        }
        return item;
    }

    // ── Menu Grid Area ─────────────────────────────────────────────────────────
    private JScrollPane buildMenuArea() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(CANVAS_BG);
        wrapper.setBorder(BorderFactory.createEmptyBorder(28, 28, 20, 20));

        // Page header
        JPanel pageHeader = new JPanel(new BorderLayout());
        pageHeader.setBackground(CANVAS_BG);
        pageHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 22, 0));

        JLabel pageTitle = new JLabel("Menu Items");
        pageTitle.setFont(new Font("Serif", Font.BOLD, 28));
        pageTitle.setForeground(TEXT_DARK);

        JLabel pageSub = new JLabel("Add, edit, or remove items from your menu");
        pageSub.setFont(new Font("SansSerif", Font.PLAIN, 13));
        pageSub.setForeground(TEXT_MUTED);

        JPanel titleStack = new JPanel();
        titleStack.setLayout(new BoxLayout(titleStack, BoxLayout.Y_AXIS));
        titleStack.setBackground(CANVAS_BG);
        titleStack.add(pageTitle);
        titleStack.add(Box.createRigidArea(new Dimension(0, 3)));
        titleStack.add(pageSub);
        pageHeader.add(titleStack, BorderLayout.WEST);

        // Item count badge
        JPanel countPill = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(245, 238, 228));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        countPill.setOpaque(false);
        countPill.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        JLabel countLbl = new JLabel(menuItems.size() + " items");
        countLbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        countLbl.setForeground(AMBER_DARK);
        countPill.add(countLbl);
        pageHeader.add(countPill, BorderLayout.EAST);

        menuGrid = new JPanel(new GridLayout(0, 3, 16, 16));
        menuGrid.setBackground(CANVAS_BG);

        wrapper.add(pageHeader, BorderLayout.NORTH);
        wrapper.add(menuGrid,   BorderLayout.CENTER);

        JScrollPane scroll = new JScrollPane(wrapper);
        scroll.setBorder(null);
        scroll.setBackground(CANVAS_BG);
        scroll.getViewport().setBackground(CANVAS_BG);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        loadMenuGrid();
        return scroll;
    }

    private void loadMenuGrid() {
        menuGrid.removeAll();
        for (MenuItem item : menuItems) {
            menuGrid.add(buildMenuCard(item));
        }
        menuGrid.revalidate();
        menuGrid.repaint();
    }

    private JPanel buildMenuCard(MenuItem item) {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), RADIUS * 2, RADIUS * 2));
                g2.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));

        // Image banner
        JPanel banner = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(230, 222, 212));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight() + RADIUS, RADIUS * 2, RADIUS * 2));
                g2.dispose();
            }
        };
        banner.setOpaque(false);
        banner.setPreferredSize(new Dimension(0, 100));
        banner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        banner.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel imgLabel = new JLabel(item.getCategory().name().substring(0, 1), SwingConstants.CENTER);
        imgLabel.setFont(new Font("Serif", Font.BOLD, 40));
        imgLabel.setForeground(new Color(255, 255, 255, 100));
        banner.add(imgLabel, BorderLayout.CENTER);

    if (item.getImageUrl() != null && !item.getImageUrl().trim().isEmpty()) {
        try {
            ImageIcon orig = new ImageIcon(item.getImageUrl());
            Image scaled = orig.getImage().getScaledInstance(300, 140, Image.SCALE_SMOOTH);
            imgLabel.setIcon(new ImageIcon(scaled));
            imgLabel.setText("");
        } catch (Exception ignored) {}
    }

        // Content
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(12, 14, 2, 14));
        content.setAlignmentX(LEFT_ALIGNMENT);

        // Category chip
        JLabel catChip = new JLabel(item.getCategory().name()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(245, 238, 228));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        catChip.setFont(new Font("SansSerif", Font.BOLD, 9));
        catChip.setForeground(AMBER_DARK);
        catChip.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        catChip.setOpaque(false);
        catChip.setAlignmentX(LEFT_ALIGNMENT);

        JLabel nameLbl = new JLabel(item.getItemName());
        nameLbl.setFont(new Font("SansSerif", Font.BOLD, 14));
        nameLbl.setForeground(TEXT_DARK);
        nameLbl.setAlignmentX(LEFT_ALIGNMENT);

        JLabel metaLbl = new JLabel("Prep: " + item.getPreparationTime() + " min  ·  Serves " + item.getServingSize());
        metaLbl.setFont(new Font("SansSerif", Font.ITALIC, 11));
        metaLbl.setForeground(TEXT_MUTED);
        metaLbl.setAlignmentX(LEFT_ALIGNMENT);

        JLabel priceLbl = new JLabel(String.format("PKR %.0f", item.getPrice()));
        priceLbl.setFont(new Font("SansSerif", Font.BOLD, 16));
        priceLbl.setForeground(TEXT_DARK);
        priceLbl.setAlignmentX(LEFT_ALIGNMENT);

        // Button row
        JPanel btnRow = new JPanel(new GridLayout(1, 2, 8, 0));
        btnRow.setOpaque(false);
        btnRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        btnRow.setAlignmentX(LEFT_ALIGNMENT);

        JButton editBtn = roundedButton("Edit", AMBER, Color.WHITE);
        editBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        editBtn.addActionListener(e -> populateFormForEdit(item));

        JButton delBtn = roundedButton("Delete", new Color(250, 245, 242), RED_SOFT);
        delBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        delBtn.addActionListener(e -> deleteItem(item));

        btnRow.add(editBtn);
        btnRow.add(delBtn);

        content.add(catChip);
        content.add(Box.createRigidArea(new Dimension(0, 6)));
        content.add(nameLbl);
        content.add(Box.createRigidArea(new Dimension(0, 3)));
        content.add(metaLbl);
        content.add(Box.createRigidArea(new Dimension(0, 8)));
        content.add(priceLbl);
        content.add(Box.createRigidArea(new Dimension(0, 10)));
        content.add(btnRow);

        card.add(banner);
        card.add(content);
        return card;
    }

    // ── Form Panel ─────────────────────────────────────────────────────────────
    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_BG);
        panel.setPreferredSize(new Dimension(310, 0));
        panel.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, BORDER_COLOR));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(CARD_BG);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
            BorderFactory.createEmptyBorder(20, 20, 18, 20)));

        formHeading = new JLabel("Add New Item");
        formHeading.setFont(new Font("Serif", Font.BOLD, 20));
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

        // Fields
        JPanel fields = new JPanel();
        fields.setLayout(new BoxLayout(fields, BoxLayout.Y_AXIS));
        fields.setBackground(CARD_BG);
        fields.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

        fldName     = addStyledField(fields, "Item Name");
        fldPrice    = addStyledField(fields, "Price (PKR)");

        addFieldLabel(fields, "Category");
        fields.add(Box.createRigidArea(new Dimension(0, 4)));
        fldCategory = new JComboBox<>(Category.values());
        fldCategory.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        fldCategory.setAlignmentX(Component.LEFT_ALIGNMENT);
        fldCategory.setBackground(FIELD_BG);
        fldCategory.setFont(new Font("SansSerif", Font.PLAIN, 13));
        fldCategory.setForeground(TEXT_DARK);
        fldCategory.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        fields.add(fldCategory);
        fields.add(Box.createRigidArea(new Dimension(0, 14)));

        addFieldLabel(fields, "Description");
        fields.add(Box.createRigidArea(new Dimension(0, 4)));
        fldDesc = new JTextArea(3, 1);
        fldDesc.setLineWrap(true);
        fldDesc.setWrapStyleWord(true);
        fldDesc.setFont(new Font("SansSerif", Font.PLAIN, 13));
        fldDesc.setForeground(TEXT_DARK);
        fldDesc.setBackground(FIELD_BG);
        JScrollPane descScroll = new JScrollPane(fldDesc);
        descScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        descScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        descScroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        fields.add(descScroll);
        fields.add(Box.createRigidArea(new Dimension(0, 14)));

        fldImage    = addStyledField(fields, "Image URL");
        fldPrepTime = addStyledField(fields, "Prep Time (mins)");
        fldServing  = addStyledField(fields, "Serving Size");

        JScrollPane fieldScroll = new JScrollPane(fields);
        fieldScroll.setBorder(null);
        fieldScroll.setBackground(CARD_BG);
        fieldScroll.getViewport().setBackground(CARD_BG);

        // Footer
        JPanel footer = new JPanel();
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        footer.setBackground(CARD_BG);
        footer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR),
            BorderFactory.createEmptyBorder(16, 20, 22, 20)));

        saveBtn = roundedButton("Add Item", AMBER, Color.WHITE);
        saveBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        saveBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        saveBtn.setAlignmentX(LEFT_ALIGNMENT);
        saveBtn.addActionListener(e -> saveItem());

        footer.add(saveBtn);

        panel.add(header,     BorderLayout.NORTH);
        panel.add(fieldScroll,BorderLayout.CENTER);
        panel.add(footer,     BorderLayout.SOUTH);
        return panel;
    }

    // ── Form helpers ───────────────────────────────────────────────────────────
    private JTextField addStyledField(JPanel container, String labelText) {
        addFieldLabel(container, labelText);
        container.add(Box.createRigidArea(new Dimension(0, 4)));
        JTextField tf = new JTextField();
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        tf.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tf.setForeground(TEXT_DARK);
        tf.setBackground(FIELD_BG);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(tf);
        container.add(Box.createRigidArea(new Dimension(0, 14)));
        return tf;
    }

    private void addFieldLabel(JPanel container, String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 11));
        lbl.setForeground(TEXT_MID);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(lbl);
    }

    // ── Form actions (unchanged logic) ────────────────────────────────────────
    private void populateFormForEdit(MenuItem item) {
        editMode = true;
        thisEditingId = item.getID();
        formHeading.setText("Edit Item");
        saveBtn.setText("Update Item");
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
            String name     = fldName.getText().trim();
            String priceStr = fldPrice.getText().trim();
            String desc     = fldDesc.getText().trim();
            String imageUrl = fldImage.getText().trim();
            String prepStr  = fldPrepTime.getText().trim();
            String servStr  = fldServing.getText().trim();

            if (name.isEmpty() || priceStr.isEmpty() || desc.isEmpty()
                    || imageUrl.isEmpty() || prepStr.isEmpty() || servStr.isEmpty()) {
                showError("Please fill in all fields.");
                return;
            }

            double price   = Double.parseDouble(priceStr);
            int    prep    = Integer.parseInt(prepStr);
            int    serving = Integer.parseInt(servStr);
            Category cat   = (Category) fldCategory.getSelectedItem();

            if (editMode) {
                MenuItem item = new MenuItem(name, thisEditingId, price, desc, cat, imageUrl, prep, serving);
                boolean ok = menuRepo.update(item);
                if (ok) {
                    for (int i = 0; i < menuItems.size(); i++) {
                        if (menuItems.get(i).getID().equals(thisEditingId)) {
                            menuItems.set(i, item);
                            break;
                        }
                    }
                    info("Item \"" + name + "\" updated successfully.");
                } else {
                    showError("Could not update \"" + name + "\". Item not found in database.");
                    return;
                }
            } else {
                MenuItem item = new MenuItem(name, price, desc, cat, imageUrl, prep, serving);
                boolean ok = menuRepo.save(item);
                if (ok) {
                    menuItems.add(item);
                    info("Item \"" + name + "\" added successfully.");
                } else {
                    showError("Could not save \"" + name + "\" to the database.");
                    return;
                }
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
            boolean ok = menuRepo.delete(item.getID());
            if (ok) {
                menuItems.removeIf(m -> m.getID().equals(item.getID()));
                loadMenuGrid();
            } else {
                showError("Could not delete \"" + item.getItemName() + "\" from the database.");
            }
        }
    }

    // ── Reusable rounded button ────────────────────────────────────────────────
    private JButton roundedButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isEnabled() ? bg : new Color(200, 195, 190));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(fg);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void info(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    private void openAnalyticsScreen() {
        new AnalyticsScreen(manager).setVisible(true);
        setVisible(false);
    }
}