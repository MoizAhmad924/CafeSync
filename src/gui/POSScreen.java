package gui;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import models.MenuItem;
import models.OrderItem;
import models.Order;
import models.User;
import repository.MenuRepository;

public class POSScreen extends JFrame {
    private static final Color BROWN = new Color(44, 30, 22);
    private static final Color BEIGE = new Color(245, 242, 235);
    private static final Color CARD_BG = new Color(255, 255, 255);
    private static final Color TEXT_DARK = new Color(60, 45, 35);
    private static final Color TEXT_MUTED = new Color(130, 115, 105);
    private static final Color ACCENT = new Color(196, 123, 87);
    private static final Color DIVIDER = new Color(230, 225, 218);
    private static final Color REMOVE_RED = new Color(200, 60, 60);

    private final User cashier;

    private List<OrderItem> orderItems = new ArrayList<>();

    private JPanel cartListPanel;
    private JLabel totalLabel;
    private JButton confirmButton;

    private final MenuRepository menuRepo = new MenuRepository();
    private final List<MenuItem> menuItems = menuRepo.findAll();

    public POSScreen(User cashier) {
        this.cashier = cashier;
        setTitle("CafeSync — POS Screen  |  " + cashier.getName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 680);
        setLocationRelativeTo(null);
        setResizable(true);
        buildUI();
    }

    public User getCashierUser() {
        return cashier;
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(BEIGE);
        root.add(buildTopBar(), BorderLayout.NORTH);
        root.add(buildMenuArea(), BorderLayout.CENTER);
        root.add(buildCartPanel(), BorderLayout.EAST);
        setContentPane(root);
    }

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(BROWN);
        bar.setPreferredSize(new Dimension(0, 60));
        bar.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 24));

        JLabel logo = new JLabel("CafeSync  ·  Point of Sale");
        logo.setFont(new Font("Serif", Font.BOLD, 22));
        logo.setForeground(Color.WHITE);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        rightPanel.setOpaque(false);

        JLabel cashierLabel = new JLabel("Cashier: " + cashier.getName());
        cashierLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        cashierLabel.setForeground(new Color(200, 180, 160));

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(new Font("SansSerif", Font.BOLD, 11));
        logoutBtn.setBackground(REMOVE_RED);
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setOpaque(true);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginScreen().setVisible(true);
        });

        JButton orderMgmtBtn = new JButton("Orders");
        orderMgmtBtn.setFont(new Font("SansSerif", Font.BOLD, 11));
        orderMgmtBtn.setBackground(ACCENT);
        orderMgmtBtn.setForeground(Color.WHITE);
        orderMgmtBtn.setFocusPainted(false);
        orderMgmtBtn.setBorderPainted(false);
        orderMgmtBtn.setOpaque(true);
        orderMgmtBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        orderMgmtBtn.addActionListener(e -> openOrderManagementScreen());

        rightPanel.add(cashierLabel);
        rightPanel.add(logoutBtn);
        rightPanel.add(orderMgmtBtn);

        bar.add(logo, BorderLayout.WEST);
        bar.add(rightPanel, BorderLayout.EAST);
        return bar;
    }

    private JScrollPane buildMenuArea() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BEIGE);
        wrapper.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 10));

        // grid: 3 columns, auto rows
        JPanel grid = new JPanel(new GridLayout(0, 3, 14, 14));
        grid.setBackground(BEIGE);

        for (MenuItem item : menuItems) {
            grid.add(buildMenuCard(item));
        }

        wrapper.add(grid, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(wrapper);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setBackground(BEIGE);
        return scroll;
    }

    private JPanel buildMenuCard(MenuItem menuItem) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DIVIDER, 1),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)));

        // imaage placeholder
        JPanel banner = new JPanel(new BorderLayout());
        banner.setAlignmentX(Component.LEFT_ALIGNMENT);
        banner.setPreferredSize(new Dimension(0, 90));
        banner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        banner.setBackground(new Color(220, 215, 205));
        banner.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        JLabel imgLabel = new JLabel(menuItem.getCategory().name().substring(0, 1), SwingConstants.CENTER);
        imgLabel.setFont(new Font("Serif", Font.BOLD, 36));
        imgLabel.setForeground(new Color(255, 255, 255, 140));
        banner.add(imgLabel, BorderLayout.CENTER);

        if (menuItem.getImageUrl() != null && !menuItem.getImageUrl().trim().isEmpty()) {
            try {
                ImageIcon OriginalImg = new ImageIcon(menuItem.getImageUrl());
                Image img = OriginalImg.getImage();
                if (img != null) {
                    Image scaled = img.getScaledInstance(200, 90, Image.SCALE_SMOOTH);
                    imgLabel.setIcon(new ImageIcon(scaled));
                    imgLabel.setText("");
                }
            } catch (Exception e) {
                System.out.println("Failed to load image for " + menuItem.getItemName());
            }
        }

        JLabel nameLbl = new JLabel(menuItem.getItemName());
        nameLbl.setFont(new Font("SansSerif", Font.BOLD, 14));
        nameLbl.setForeground(TEXT_DARK);
        nameLbl.setAlignmentX(LEFT_ALIGNMENT);

        JLabel descLbl = new JLabel(
                "<html><body style='width:160px;color:#828373'>"
                        + menuItem.getDescription() + "</body></html>");
        descLbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
        descLbl.setAlignmentX(LEFT_ALIGNMENT);

        JLabel metaLbl = new JLabel(
                String.format("Prep: %d min | Serves: %d", menuItem.getPreparationTime(), menuItem.getServingSize()));
        metaLbl.setFont(new Font("SansSerif", Font.ITALIC, 11));
        metaLbl.setForeground(TEXT_MUTED);
        metaLbl.setAlignmentX(LEFT_ALIGNMENT);

        JLabel priceLbl = new JLabel(String.format("PKR %.2f", menuItem.getPrice()));
        priceLbl.setFont(new Font("SansSerif", Font.BOLD, 15));
        priceLbl.setForeground(ACCENT);
        priceLbl.setAlignmentX(LEFT_ALIGNMENT);

        JButton addBtn = new JButton("+ Add to Cart");
        addBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        addBtn.setBackground(BROWN);
        addBtn.setForeground(Color.WHITE);
        addBtn.setFocusPainted(false);
        addBtn.setBorderPainted(false);
        addBtn.setOpaque(true);
        addBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        addBtn.setAlignmentX(LEFT_ALIGNMENT);
        addBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addBtn.addActionListener(e -> addToCart(menuItem));

        card.add(banner);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(nameLbl);
        card.add(Box.createRigidArea(new Dimension(0, 4)));
        card.add(descLbl);
        card.add(Box.createRigidArea(new Dimension(0, 4)));
        card.add(metaLbl);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(priceLbl);
        card.add(Box.createRigidArea(new Dimension(0, 12)));
        card.add(addBtn);

        return card;
    }

    private JPanel buildCartPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_BG);
        panel.setPreferredSize(new Dimension(300, 0));
        panel.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, DIVIDER));

        // header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(CARD_BG);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, DIVIDER),
                BorderFactory.createEmptyBorder(16, 18, 16, 18)));

        JLabel cartTitle = new JLabel("Cart");
        cartTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        cartTitle.setForeground(TEXT_DARK);

        JButton clearBtn = new JButton("Clear");
        clearBtn.setFont(new Font("SansSerif", Font.PLAIN, 11));
        clearBtn.setForeground(REMOVE_RED);
        clearBtn.setBackground(CARD_BG);
        clearBtn.setBorderPainted(false);
        clearBtn.setFocusPainted(false);
        clearBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearBtn.addActionListener(e -> clearCart());

        header.add(cartTitle, BorderLayout.WEST);
        header.add(clearBtn, BorderLayout.EAST);

        // scrollable list of cart rows
        cartListPanel = new JPanel();
        cartListPanel.setLayout(new BoxLayout(cartListPanel, BoxLayout.Y_AXIS));
        cartListPanel.setBackground(CARD_BG);
        cartListPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        JScrollPane cartScroll = new JScrollPane(cartListPanel);
        cartScroll.setBorder(null);
        cartScroll.getVerticalScrollBar().setUnitIncrement(10);
        cartScroll.setBackground(CARD_BG);

        // footer
        JPanel footer = new JPanel();
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        footer.setBackground(CARD_BG);
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, DIVIDER),
                BorderFactory.createEmptyBorder(14, 18, 18, 18)));

        totalLabel = new JLabel("Total:  PKR 0.00");
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        totalLabel.setForeground(TEXT_DARK);
        totalLabel.setAlignmentX(LEFT_ALIGNMENT);

        confirmButton = new JButton("Confirm Payment →");
        confirmButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        confirmButton.setBackground(BROWN);
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setFocusPainted(false);
        confirmButton.setBorderPainted(false);
        confirmButton.setOpaque(true);
        confirmButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        confirmButton.setAlignmentX(LEFT_ALIGNMENT);
        confirmButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        confirmButton.setEnabled(false);
        confirmButton.addActionListener(e -> openPaymentScreen());

        footer.add(totalLabel);
        footer.add(Box.createRigidArea(new Dimension(0, 12)));
        footer.add(confirmButton);

        panel.add(header, BorderLayout.NORTH);
        panel.add(cartScroll, BorderLayout.CENTER);
        panel.add(footer, BorderLayout.SOUTH);

        return panel;
    }

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
        if (newQty <= 0) {
            removeFromCart(index);
        } else {
            orderItems.get(index).setQuantity(newQty);
        }
        refreshCart();
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
            cartListPanel.add(buildDividerLine());
        }

        if (orderItems.isEmpty()) {
            JLabel empty = new JLabel("Your cart is empty");
            empty.setFont(new Font("SansSerif", Font.ITALIC, 13));
            empty.setForeground(TEXT_MUTED);
            empty.setAlignmentX(CENTER_ALIGNMENT);
            cartListPanel.add(Box.createRigidArea(new Dimension(0, 20)));
            cartListPanel.add(empty);
        }

        totalLabel.setText(String.format("Total:  PKR %.2f", total));
        confirmButton.setEnabled(!orderItems.isEmpty());

        cartListPanel.revalidate();
        cartListPanel.repaint();
    }

    private JPanel buildCartRow(int index, MenuItem item, int qty) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setBackground(CARD_BG);
        row.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBackground(CARD_BG);

        JLabel nameLbl = new JLabel(item.getItemName());
        nameLbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        nameLbl.setForeground(TEXT_DARK);

        JLabel priceLbl = new JLabel(String.format("PKR %.2f", item.getPrice()));
        priceLbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
        priceLbl.setForeground(TEXT_MUTED);

        info.add(nameLbl);
        info.add(priceLbl);

        JPanel controls = new JPanel();
        controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));
        controls.setBackground(CARD_BG);

        JLabel subtotal = new JLabel(String.format("PKR %.2f", item.getPrice() * qty));
        subtotal.setFont(new Font("SansSerif", Font.BOLD, 12));
        subtotal.setForeground(ACCENT);
        subtotal.setAlignmentX(RIGHT_ALIGNMENT);

        JPanel qtyRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 0));
        qtyRow.setBackground(CARD_BG);

        JLabel minus = tinyBtn("-");
        JLabel qtyLbl = new JLabel(String.valueOf(qty));
        qtyLbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        qtyLbl.setPreferredSize(new Dimension(22, 28));
        qtyLbl.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel plus = tinyBtn("+");
        JLabel del = tinyBtn("X");
        del.setForeground(REMOVE_RED);

        minus.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                changeQty(index, -1);
            }
        });
        plus.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                changeQty(index, +1);
            }
        });
        del.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                removeFromCart(index);
            }
        });

        qtyRow.add(minus);
        qtyRow.add(qtyLbl);
        qtyRow.add(plus);
        qtyRow.add(Box.createHorizontalStrut(4));
        qtyRow.add(del);

        controls.add(subtotal);
        controls.add(qtyRow);

        row.add(info, BorderLayout.WEST);
        row.add(controls, BorderLayout.EAST);
        return row;
    }

    private JPanel buildDividerLine() {
        JPanel line = new JPanel();
        line.setBackground(DIVIDER);
        line.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return line;
    }

    private JLabel tinyBtn(String text) {
        JLabel b = new JLabel(text, SwingConstants.CENTER);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setPreferredSize(new Dimension(28, 28));
        b.setMinimumSize(new Dimension(28, 28));
        b.setMaximumSize(new Dimension(28, 28));
        b.setBackground(BEIGE);
        b.setForeground(TEXT_DARK);
        b.setOpaque(true);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return b;
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
