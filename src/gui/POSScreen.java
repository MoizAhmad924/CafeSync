package gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import models.MenuItem;
import models.OrderItem;
import models.User;
import repository.MenuRepository;

public class POSScreen extends JFrame {

    // ── Palette ────────────────────────────────────────────────────────────────
    private static final Color SIDEBAR_BG   = new Color(26, 20, 16);
    private static final Color SIDEBAR_HOVER= new Color(44, 34, 26);
    private static final Color SIDEBAR_ACTIVE= new Color(55, 42, 32);
    private static final Color CANVAS_BG    = new Color(247, 245, 241);
    private static final Color CARD_BG      = new Color(255, 255, 255);
    private static final Color AMBER        = new Color(212, 135, 90);
    private static final Color AMBER_DARK   = new Color(185, 108, 62);
    private static final Color TEXT_DARK    = new Color(28, 22, 16);
    private static final Color TEXT_MID     = new Color(90, 75, 65);
    private static final Color TEXT_MUTED   = new Color(155, 142, 132);
    private static final Color BORDER_COLOR = new Color(234, 230, 224);
    private static final Color RED_SOFT     = new Color(210, 70, 70);
    private static final Color SUCCESS      = new Color(52, 168, 100);

    private static final int   RADIUS       = 14;

    // ── State ──────────────────────────────────────────────────────────────────
    private final User cashier;
    private List<OrderItem> orderItems = new ArrayList<>();

    // ── Widgets ────────────────────────────────────────────────────────────────
    private JPanel cartListPanel;
    private JLabel totalLabel;
    private JButton confirmButton;
    private JLabel cartCountBadge;

    private final MenuRepository menuRepo  = new MenuRepository();
    private final List<MenuItem> menuItems = menuRepo.findAll();

    // ══════════════════════════════════════════════════════════════════════════
    public POSScreen(User cashier) {
        this.cashier = cashier;
        setTitle("CafeSync — POS");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1260, 760);
        setMinimumSize(new Dimension(1000, 600));
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(true);
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        buildUI();
    }

    public User getCashierUser() { return cashier; }

    // ── Root layout ────────────────────────────────────────────────────────────
    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(CANVAS_BG);
        root.add(buildSidebar(),   BorderLayout.WEST);
        root.add(buildMenuArea(),  BorderLayout.CENTER);
        root.add(buildCartPanel(), BorderLayout.EAST);
        setContentPane(root);
    }

    // ── Left Sidebar ───────────────────────────────────────────────────────────
// ═══════════════════════════════════════════════════════════════════════════
// REPLACEMENT: buildSidebar(), sidebarSectionLabel(), sidebarNavItem(),
//              sidebarNavItemClickable()  —  for POSScreen.java
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

        JLabel logoSub = new JLabel("Point of Sale");
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
        sidebar.add(sidebarNavItem("Point of Sale", "●", true));
        sidebar.add(sidebarNavItemClickable("Orders", "☰", false,
                e -> openOrderManagementScreen()));

        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));

        // ── Session section ───────────────────────────────────────────────
        sidebar.add(sidebarSectionLabel("SESSION"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 4)));

        // Cashier identity row
        JPanel cashierPill = new JPanel();
        cashierPill.setLayout(new BoxLayout(cashierPill, BoxLayout.X_AXIS));
        cashierPill.setBackground(SIDEBAR_ACTIVE);
        cashierPill.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        cashierPill.setAlignmentX(LEFT_ALIGNMENT);
        cashierPill.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 16));

        JPanel initCircle = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AMBER);
                g2.fillOval(0, 0, 26, 26);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 12));
                FontMetrics fm = g2.getFontMetrics();
                String initial = cashier.getName().substring(0, 1).toUpperCase();
                g2.drawString(initial,
                        (26 - fm.stringWidth(initial)) / 2,
                        (26 - fm.getHeight()) / 2 + fm.getAscent());
            }
        };
        initCircle.setPreferredSize(new Dimension(26, 26));
        initCircle.setMinimumSize(new Dimension(26, 26));
        initCircle.setMaximumSize(new Dimension(26, 26));
        initCircle.setOpaque(false);

        JLabel nameLabel = new JLabel(cashier.getName());
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        nameLabel.setForeground(Color.WHITE);

        cashierPill.add(initCircle);
        cashierPill.add(Box.createRigidArea(new Dimension(10, 0)));
        cashierPill.add(nameLabel);
        cashierPill.add(Box.createHorizontalGlue());

        sidebar.add(cashierPill);
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

        JLabel version = new JLabel("v2.0  ·  CafeSync POS");
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

    // ── Menu Area ──────────────────────────────────────────────────────────────
    private JScrollPane buildMenuArea() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(CANVAS_BG);
        wrapper.setBorder(BorderFactory.createEmptyBorder(28, 28, 20, 20));

        // Page header
        JPanel pageHeader = new JPanel(new BorderLayout());
        pageHeader.setBackground(CANVAS_BG);
        pageHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 22, 0));

        JLabel pageTitle = new JLabel("Menu");
        pageTitle.setFont(new Font("Serif", Font.BOLD, 28));
        pageTitle.setForeground(TEXT_DARK);

        JLabel pageSub = new JLabel("Select items to add to the current order");
        pageSub.setFont(new Font("SansSerif", Font.PLAIN, 13));
        pageSub.setForeground(TEXT_MUTED);

        JPanel titleStack = new JPanel();
        titleStack.setLayout(new BoxLayout(titleStack, BoxLayout.Y_AXIS));
        titleStack.setBackground(CANVAS_BG);
        titleStack.add(pageTitle);
        titleStack.add(Box.createRigidArea(new Dimension(0, 3)));
        titleStack.add(pageSub);
        pageHeader.add(titleStack, BorderLayout.WEST);

        // grid
        JPanel grid = new JPanel(new GridLayout(0, 3, 16, 16));
        grid.setBackground(CANVAS_BG);
        for (MenuItem item : menuItems) {
            grid.add(buildMenuCard(item));
        }

        wrapper.add(pageHeader, BorderLayout.NORTH);
        wrapper.add(grid, BorderLayout.CENTER);

        JScrollPane scroll = new JScrollPane(wrapper);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        scroll.setBackground(CANVAS_BG);
        scroll.getViewport().setBackground(CANVAS_BG);
        return scroll;
    }

    private JPanel buildMenuCard(MenuItem menuItem) {
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
        banner.setPreferredSize(new Dimension(0, 140));
        banner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        banner.setAlignmentX(LEFT_ALIGNMENT);

        JLabel imgLabel = new JLabel(menuItem.getCategory().name().substring(0, 1), SwingConstants.CENTER);
        imgLabel.setFont(new Font("Serif", Font.BOLD, 40));
        imgLabel.setForeground(new Color(255, 255, 255, 100));
        banner.add(imgLabel, BorderLayout.CENTER);

    if (menuItem.getImageUrl() != null && !menuItem.getImageUrl().trim().isEmpty()) {
        try {
            ImageIcon orig = new ImageIcon(menuItem.getImageUrl());
            Image scaled = orig.getImage().getScaledInstance(300, 140, Image.SCALE_SMOOTH);
            imgLabel.setIcon(new ImageIcon(scaled));
            imgLabel.setText("");
        } catch (Exception ignored) {}
    }        
        
        // Content padding wrapper
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(12, 14, 2, 14));
        content.setAlignmentX(LEFT_ALIGNMENT);

        // Category chip
        JLabel catChip = new JLabel(menuItem.getCategory().name()) {
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

        JLabel nameLbl = new JLabel(menuItem.getItemName());
        nameLbl.setFont(new Font("SansSerif", Font.BOLD, 14));
        nameLbl.setForeground(TEXT_DARK);
        nameLbl.setAlignmentX(LEFT_ALIGNMENT);

        JLabel descLbl = new JLabel("<html><body style='width:155px'>" + menuItem.getDescription() + "</body></html>");
        descLbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
        descLbl.setForeground(TEXT_MUTED);
        descLbl.setAlignmentX(LEFT_ALIGNMENT);

        JPanel priceRow = new JPanel(new BorderLayout(0, 0));
        priceRow.setOpaque(false);
        priceRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        priceRow.setAlignmentX(LEFT_ALIGNMENT);

        JLabel priceLbl = new JLabel(String.format("PKR %.0f", menuItem.getPrice()));
        priceLbl.setFont(new Font("SansSerif", Font.BOLD, 16));
        priceLbl.setForeground(TEXT_DARK);

        JLabel metaLbl = new JLabel(menuItem.getPreparationTime() + " min");
        metaLbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
        metaLbl.setForeground(TEXT_MUTED);

        priceRow.add(priceLbl, BorderLayout.WEST);
        priceRow.add(metaLbl, BorderLayout.EAST);

        // Add button
        JButton addBtn = roundedButton("+ Add to Order", AMBER, Color.WHITE);
        addBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        addBtn.setAlignmentX(LEFT_ALIGNMENT);
        addBtn.addActionListener(e -> addToCart(menuItem));

        content.add(catChip);
        content.add(Box.createRigidArea(new Dimension(0, 6)));
        content.add(nameLbl);
        content.add(Box.createRigidArea(new Dimension(0, 4)));
        content.add(descLbl);
        content.add(Box.createRigidArea(new Dimension(0, 10)));
        content.add(priceRow);
        content.add(Box.createRigidArea(new Dimension(0, 10)));
        content.add(addBtn);

        card.add(banner);
        card.add(content);
        return card;
    }

    // ── Cart Panel ─────────────────────────────────────────────────────────────
    private JPanel buildCartPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_BG);
        panel.setPreferredSize(new Dimension(310, 0));
        panel.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, BORDER_COLOR));

        // Header
        JPanel header = new JPanel(new BorderLayout(0, 0));
        header.setBackground(CARD_BG);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
            BorderFactory.createEmptyBorder(20, 20, 18, 20)));

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        titleRow.setBackground(CARD_BG);

        JLabel cartTitle = new JLabel("Order");
        cartTitle.setFont(new Font("Serif", Font.BOLD, 20));
        cartTitle.setForeground(TEXT_DARK);

        cartCountBadge = new JLabel("0") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AMBER);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        cartCountBadge.setFont(new Font("SansSerif", Font.BOLD, 11));
        cartCountBadge.setForeground(Color.WHITE);
        cartCountBadge.setPreferredSize(new Dimension(22, 22));
        cartCountBadge.setHorizontalAlignment(SwingConstants.CENTER);
        cartCountBadge.setOpaque(false);

        titleRow.add(cartTitle);
        titleRow.add(cartCountBadge);

        JButton clearBtn = new JButton("Clear all");
        clearBtn.setFont(new Font("SansSerif", Font.PLAIN, 11));
        clearBtn.setForeground(TEXT_MUTED);
        clearBtn.setBackground(CARD_BG);
        clearBtn.setBorderPainted(false);
        clearBtn.setFocusPainted(false);
        clearBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearBtn.addActionListener(e -> clearCart());

        header.add(titleRow, BorderLayout.WEST);
        header.add(clearBtn, BorderLayout.EAST);

        // Cart list
        cartListPanel = new JPanel();
        cartListPanel.setLayout(new BoxLayout(cartListPanel, BoxLayout.Y_AXIS));
        cartListPanel.setBackground(CARD_BG);
        cartListPanel.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));

        JScrollPane cartScroll = new JScrollPane(cartListPanel);
        cartScroll.setBorder(null);
        cartScroll.getVerticalScrollBar().setUnitIncrement(10);
        cartScroll.setBackground(CARD_BG);
        cartScroll.getViewport().setBackground(CARD_BG);

        // Footer
        JPanel footer = new JPanel();
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        footer.setBackground(CARD_BG);
        footer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR),
            BorderFactory.createEmptyBorder(16, 20, 22, 20)));

        // Subtotal row
        JPanel subtotalRow = new JPanel(new BorderLayout());
        subtotalRow.setBackground(CARD_BG);
        subtotalRow.setAlignmentX(LEFT_ALIGNMENT);
        subtotalRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));

        JLabel subtotalLbl = new JLabel("Subtotal");
        subtotalLbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtotalLbl.setForeground(TEXT_MUTED);

        totalLabel = new JLabel("PKR 0");
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        totalLabel.setForeground(TEXT_DARK);
        totalLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        subtotalRow.add(subtotalLbl, BorderLayout.WEST);
        subtotalRow.add(totalLabel, BorderLayout.EAST);

        confirmButton = roundedButton("Confirm Payment", SUCCESS, Color.WHITE);
        confirmButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        confirmButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        confirmButton.setAlignmentX(LEFT_ALIGNMENT);
        confirmButton.setEnabled(false);
        confirmButton.addActionListener(e -> openPaymentScreen());

        footer.add(subtotalRow);
        footer.add(Box.createRigidArea(new Dimension(0, 14)));
        footer.add(confirmButton);

        panel.add(header,     BorderLayout.NORTH);
        panel.add(cartScroll, BorderLayout.CENTER);
        panel.add(footer,     BorderLayout.SOUTH);
        return panel;
    }

    // ── Cart logic ─────────────────────────────────────────────────────────────
    private void addToCart(MenuItem menuItem) {
        for (int i = 0; i < orderItems.size(); i++) {
            if (orderItems.get(i).getMenuItem().getID().equals(menuItem.getID())) {
                orderItems.get(i).incrementQuantity();
                refreshCart();
                return;
            }
        }
        orderItems.add(new OrderItem(menuItem));
        refreshCart();
    }

    private void removeFromCart(int index) {
        orderItems.remove(index);
        refreshCart();
    }

    private void changeQty(int index, int delta) {
        int newQty = orderItems.get(index).getQuantity() + delta;
        if (newQty <= 0) removeFromCart(index);
        else { orderItems.get(index).setQuantity(newQty); refreshCart(); }
    }

    private void clearCart() {
        orderItems.clear();
        refreshCart();
    }

    private void refreshCart() {
        cartListPanel.removeAll();
        double total = 0;

        for (int i = 0; i < orderItems.size(); i++) {
            MenuItem item = orderItems.get(i).getMenuItem();
            int qty = orderItems.get(i).getQuantity();
            total += orderItems.get(i).getSubTotal();
            cartListPanel.add(buildCartRow(i, item, qty));
            if (i < orderItems.size() - 1) cartListPanel.add(buildDividerLine());
        }

        if (orderItems.isEmpty()) {
            JPanel emptyState = new JPanel();
            emptyState.setLayout(new BoxLayout(emptyState, BoxLayout.Y_AXIS));
            emptyState.setBackground(CARD_BG);

            JLabel icon = new JLabel("⊘");
            icon.setFont(new Font("SansSerif", Font.PLAIN, 32));
            icon.setForeground(new Color(220, 215, 208));
            icon.setAlignmentX(CENTER_ALIGNMENT);

            JLabel msg = new JLabel("No items added yet");
            msg.setFont(new Font("SansSerif", Font.PLAIN, 13));
            msg.setForeground(TEXT_MUTED);
            msg.setAlignmentX(CENTER_ALIGNMENT);

            emptyState.add(Box.createVerticalGlue());
            emptyState.add(Box.createRigidArea(new Dimension(0, 60)));
            emptyState.add(icon);
            emptyState.add(Box.createRigidArea(new Dimension(0, 8)));
            emptyState.add(msg);
            emptyState.add(Box.createVerticalGlue());
            cartListPanel.add(emptyState);
        }

        totalLabel.setText(String.format("PKR %.0f", total));
        cartCountBadge.setText(String.valueOf(orderItems.size()));
        confirmButton.setEnabled(!orderItems.isEmpty());
        cartListPanel.revalidate();
        cartListPanel.repaint();
    }

    private JPanel buildCartRow(int index, MenuItem item, int qty) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBackground(CARD_BG);
        row.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 68));

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBackground(CARD_BG);

        JLabel nameLbl = new JLabel(item.getItemName());
        nameLbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        nameLbl.setForeground(TEXT_DARK);

        JLabel priceLbl = new JLabel(String.format("PKR %.0f each", item.getPrice()));
        priceLbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
        priceLbl.setForeground(TEXT_MUTED);

        info.add(nameLbl);
        info.add(Box.createRigidArea(new Dimension(0, 2)));
        info.add(priceLbl);

        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBackground(CARD_BG);

        JLabel subtotal = new JLabel(String.format("PKR %.0f", item.getPrice() * qty));
        subtotal.setFont(new Font("SansSerif", Font.BOLD, 12));
        subtotal.setForeground(AMBER_DARK);
        subtotal.setAlignmentX(RIGHT_ALIGNMENT);

        JPanel qtyCtrl = new JPanel(new FlowLayout(FlowLayout.RIGHT, 3, 0));
        qtyCtrl.setBackground(CARD_BG);

        JLabel minus = qtyControlBtn("−");
        JLabel qtyLbl = new JLabel(String.valueOf(qty));
        qtyLbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        qtyLbl.setPreferredSize(new Dimension(20, 26));
        qtyLbl.setHorizontalAlignment(SwingConstants.CENTER);
        qtyLbl.setForeground(TEXT_DARK);
        JLabel plus = qtyControlBtn("+");
        JLabel del = qtyControlBtn("×");
        del.setForeground(RED_SOFT);

        minus.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { changeQty(index, -1); }
        });
        plus.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { changeQty(index, +1); }
        });
        del.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { removeFromCart(index); }
        });

        qtyCtrl.add(minus);
        qtyCtrl.add(qtyLbl);
        qtyCtrl.add(plus);
        qtyCtrl.add(Box.createHorizontalStrut(4));
        qtyCtrl.add(del);

        right.add(subtotal);
        right.add(qtyCtrl);

        row.add(info,  BorderLayout.WEST);
        row.add(right, BorderLayout.EAST);
        return row;
    }

    private JPanel buildDividerLine() {
        JPanel line = new JPanel();
        line.setBackground(BORDER_COLOR);
        line.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        line.setPreferredSize(new Dimension(0, 1));
        return line;
    }

    // ── Helpers ────────────────────────────────────────────────────────────────
    private JLabel qtyControlBtn(String text) {
        JLabel b = new JLabel(text, SwingConstants.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(245, 242, 237));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setPreferredSize(new Dimension(26, 26));
        b.setForeground(TEXT_DARK);
        b.setOpaque(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

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

    private void openPaymentScreen() {
        new PaymentScreen(orderItems, this).setVisible(true);
        setVisible(false);
    }

    private void openOrderManagementScreen() {
        new OrderManagementScreen(cashier, this).setVisible(true);
        setVisible(false);
    }
}