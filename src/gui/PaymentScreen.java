package gui;

import models.Order;
import models.OrderItem;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import enums.OrderType;
import enums.OrderStatus;

public class PaymentScreen extends JFrame {
    private static final Color BROWN = new Color(44, 30, 22);
    private static final Color BEIGE = new Color(245, 242, 235);
    private static final Color CARD_BG = new Color(255, 255, 255);
    private static final Color TEXT_DARK = new Color(60, 45, 35);
    private static final Color TEXT_MUTED = new Color(130, 115, 105);
    private static final Color ACCENT = new Color(196, 123, 87);
    private static final Color DIVIDER = new Color(230, 225, 218);

    private final Order order;
    private final JFrame parentPOS;

    private ButtonGroup methodGroup;
    private JRadioButton cashBtn, cardBtn;
    private JLabel statusLabel;

    public PaymentScreen(List<OrderItem> orderItems, JFrame parentPOS) {
        String username = ((POSScreen) parentPOS).getCashierUser().getUsername();
        double total = 0;
        for (OrderItem item : orderItems) {
            total += item.getSubTotal();
        }
        String orderID = "ORD" + System.currentTimeMillis();
        String nowStr = java.time.LocalDateTime.now().toString();

        this.order = new Order(
                orderID,
                nowStr,
                username,
                enums.OrderType.TAKEAWAY,
                "Walk-in Customer",
                "N/A",
                "",
                orderItems,
                total,
                enums.OrderStatus.PENDING);
        this.parentPOS = parentPOS;
        setTitle("CafeSync — Payment  |  Order #" + order.getOrderID());
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1100, 680);
        setLocationRelativeTo(null);
        setResizable(false);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                goBack();
            }
        });
        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BEIGE);

        root.add(buildTopBar(), BorderLayout.NORTH);
        root.add(buildBodyPanel(), BorderLayout.CENTER);
        root.add(buildFooter(), BorderLayout.SOUTH);

        setContentPane(root);
    }

    // Top bar
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(BROWN);
        bar.setPreferredSize(new Dimension(0, 56));
        bar.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 24));

        JLabel title = new JLabel("Payment");
        title.setFont(new Font("Serif", Font.BOLD, 20));
        title.setForeground(Color.WHITE);

        JLabel orderId = new JLabel("Order #" + order.getOrderID());
        orderId.setFont(new Font("SansSerif", Font.PLAIN, 13));
        orderId.setForeground(new Color(200, 180, 160));

        bar.add(title, BorderLayout.WEST);
        bar.add(orderId, BorderLayout.EAST);
        return bar;
    }

    // Body: order summary + payment method
    private JPanel buildBodyPanel() {
        JPanel wrapper = new JPanel(new GridLayout(1, 2, 24, 0));
        wrapper.setBackground(BEIGE);
        wrapper.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));

        // LEFT COLUMN
        JPanel leftCol = new JPanel();
        leftCol.setLayout(new BoxLayout(leftCol, BoxLayout.Y_AXIS));
        leftCol.setBackground(BEIGE);

        // Customer Info Card
        JPanel customerCard = new JPanel();
        customerCard.setLayout(new BoxLayout(customerCard, BoxLayout.Y_AXIS));
        customerCard.setBackground(CARD_BG);
        customerCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DIVIDER),
                BorderFactory.createEmptyBorder(24, 28, 24, 28)));
        customerCard.setAlignmentX(LEFT_ALIGNMENT);

        JLabel customerTitle = new JLabel("Customer Information");
        customerTitle.setFont(new Font("Serif", Font.BOLD, 20));
        customerTitle.setForeground(TEXT_DARK);
        customerTitle.setAlignmentX(LEFT_ALIGNMENT);
        customerCard.add(customerTitle);
        customerCard.add(Box.createRigidArea(new Dimension(0, 16)));

        customerCard.add(buildInfoRow("CUSTOMER NAME", "-"));
        customerCard.add(buildDivider());
        customerCard.add(buildInfoRow("CONTACT", "-"));
        customerCard.add(buildDivider());
        customerCard.add(buildInfoRow("ORDER TYPE", "-"));
        customerCard.add(buildDivider());
        customerCard.add(buildInfoRow("DELIVERY ADDRESS", "-"));

        // Limit the vertical stretch
        customerCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));

        // Payment method card
        JPanel methodCard = new JPanel();
        methodCard.setLayout(new BoxLayout(methodCard, BoxLayout.Y_AXIS));
        methodCard.setBackground(CARD_BG);
        methodCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DIVIDER),
                BorderFactory.createEmptyBorder(24, 28, 24, 28)));
        methodCard.setAlignmentX(LEFT_ALIGNMENT);

        JLabel methodTitle = new JLabel("Payment Method");
        methodTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        methodTitle.setForeground(TEXT_DARK);
        methodTitle.setAlignmentX(LEFT_ALIGNMENT);
        methodCard.add(methodTitle);
        methodCard.add(Box.createRigidArea(new Dimension(0, 16)));

        methodGroup = new ButtonGroup();
        cashBtn = styledRadio(" Cash");
        cardBtn = styledRadio(" Card / Debit");
        cashBtn.setSelected(true);

        methodGroup.add(cashBtn);
        methodGroup.add(cardBtn);

        methodCard.add(cashBtn);
        methodCard.add(Box.createRigidArea(new Dimension(0, 8)));
        methodCard.add(cardBtn);

        methodCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

        leftCol.add(customerCard);
        leftCol.add(Box.createRigidArea(new Dimension(0, 20)));
        leftCol.add(methodCard);
        leftCol.add(Box.createVerticalGlue());

        // RIGHT COLUMN
        JPanel rightCol = new JPanel();
        rightCol.setLayout(new BoxLayout(rightCol, BoxLayout.Y_AXIS));
        rightCol.setBackground(BEIGE);

        JPanel summaryCard = new JPanel();
        summaryCard.setLayout(new BoxLayout(summaryCard, BoxLayout.Y_AXIS));
        summaryCard.setBackground(CARD_BG);
        summaryCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DIVIDER),
                BorderFactory.createEmptyBorder(24, 28, 24, 28)));
        summaryCard.setAlignmentX(LEFT_ALIGNMENT);

        JLabel summaryTitle = new JLabel("Order Summary");
        summaryTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
        summaryTitle.setForeground(TEXT_DARK);
        summaryTitle.setAlignmentX(LEFT_ALIGNMENT);
        summaryCard.add(summaryTitle);
        summaryCard.add(Box.createRigidArea(new Dimension(0, 16)));

        for (OrderItem oi : order.getOrderItems()) {
            JPanel itemRow = new JPanel(new BorderLayout());
            itemRow.setBackground(CARD_BG);
            itemRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));

            JLabel itemName = new JLabel(oi.getMenuItem().getItemName()
                    + "  x" + oi.getQuantity());
            itemName.setFont(new Font("SansSerif", Font.PLAIN, 16));
            itemName.setForeground(TEXT_DARK);

            JLabel itemPrice = new JLabel(String.format("PKR %.2f", oi.getSubTotal()));
            itemPrice.setFont(new Font("SansSerif", Font.PLAIN, 16));
            itemPrice.setForeground(TEXT_MUTED);

            itemRow.add(itemName, BorderLayout.WEST);
            itemRow.add(itemPrice, BorderLayout.EAST);

            summaryCard.add(itemRow);
            summaryCard.add(Box.createRigidArea(new Dimension(0, 6)));
        }

        // Divider + total
        summaryCard.add(Box.createRigidArea(new Dimension(0, 12)));
        JPanel divLine = new JPanel();
        divLine.setBackground(DIVIDER);
        divLine.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        summaryCard.add(divLine);
        summaryCard.add(Box.createRigidArea(new Dimension(0, 12)));

        JPanel totalRow = new JPanel(new BorderLayout());
        totalRow.setBackground(CARD_BG);
        totalRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel totalLbl = new JLabel("Total");
        totalLbl.setFont(new Font("SansSerif", Font.BOLD, 18));
        totalLbl.setForeground(TEXT_DARK);

        JLabel totalAmt = new JLabel(String.format("PKR %.2f", order.getTotalPrice()));
        totalAmt.setFont(new Font("SansSerif", Font.BOLD, 20));
        totalAmt.setForeground(ACCENT);

        totalRow.add(totalLbl, BorderLayout.WEST);
        totalRow.add(totalAmt, BorderLayout.EAST);
        summaryCard.add(totalRow);

        summaryCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));

        // Status label
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        statusLabel.setAlignmentX(CENTER_ALIGNMENT);

        rightCol.add(summaryCard);
        rightCol.add(Box.createRigidArea(new Dimension(0, 16)));
        rightCol.add(statusLabel);
        rightCol.add(Box.createVerticalGlue());

        wrapper.add(leftCol);
        wrapper.add(rightCol);

        return wrapper;
    }

    // Footer buttons
    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout(12, 0));
        footer.setBackground(BEIGE);
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, DIVIDER),
                BorderFactory.createEmptyBorder(14, 32, 18, 32)));

        JButton backBtn = new JButton("← Back to Cart");
        backBtn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        backBtn.setBackground(BEIGE);
        backBtn.setForeground(TEXT_DARK);
        backBtn.setBorderPainted(true);
        backBtn.setFocusPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> goBack());

        JButton payBtn = new JButton("Confirm & Pay");
        payBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        payBtn.setBackground(BROWN);
        payBtn.setForeground(Color.WHITE);
        payBtn.setFocusPainted(false);
        payBtn.setBorderPainted(false);
        payBtn.setOpaque(true);
        payBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        payBtn.addActionListener(e -> processPayment());

        footer.add(backBtn, BorderLayout.WEST);
        footer.add(payBtn, BorderLayout.EAST);
        return footer;
    }

    private void processPayment() {
        String method = cashBtn.isSelected() ? "Cash" : "Card/Debit";
        order.completeOrder();
        JOptionPane.showMessageDialog(
                this,
                "<html><b>Payment Successful!</b><br>"
                        + "Method: " + method + "<br>"
                        + "Order: #" + order.getOrderID() + "<br>"
                        + String.format("Total: PKR %.2f", order.getTotalPrice()) + "</html>",
                "Payment Confirmed",
                JOptionPane.INFORMATION_MESSAGE);

        models.User cashier = ((POSScreen) parentPOS).getCashierUser();
        dispose();
        parentPOS.dispose();
        new POSScreen(cashier).setVisible(true);
    }

    private void goBack() {
        dispose();
        parentPOS.setVisible(true);
    }

    // helpers
    private JPanel buildInfoRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(CARD_BG);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        row.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        lbl.setForeground(TEXT_DARK);

        JLabel val = new JLabel(value);
        val.setFont(new Font("SansSerif", Font.ITALIC, 14));
        val.setForeground(TEXT_MUTED);

        row.add(lbl, BorderLayout.WEST);
        row.add(val, BorderLayout.EAST);
        return row;
    }

    private JPanel buildDivider() {
        JPanel div = new JPanel();
        div.setBackground(DIVIDER);
        div.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return div;
    }

    private JRadioButton styledRadio(String text) {
        JRadioButton rb = new JRadioButton(text);
        rb.setFont(new Font("SansSerif", Font.PLAIN, 14));
        rb.setForeground(TEXT_DARK);
        rb.setBackground(CARD_BG);
        rb.setFocusPainted(false);
        rb.setAlignmentX(LEFT_ALIGNMENT);
        return rb;
    }
}