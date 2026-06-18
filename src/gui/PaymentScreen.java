package gui;

import enums.OrderType;
import enums.PaymentMethod;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import models.Order;
import models.OrderItem;
import repository.OrderRepository;

public class PaymentScreen extends JFrame {
    private static final Color BROWN = new Color(44, 30, 22);
    private static final Color BEIGE = new Color(245, 242, 235);
    private static final Color CARD_BG = new Color(255, 255, 255);
    private static final Color TEXT_DARK = new Color(60, 45, 35);
    private static final Color TEXT_MUTED = new Color(130, 115, 105);
    private static final Color ACCENT = new Color(196, 123, 87);
    private static final Color DIVIDER = new Color(230, 225, 218);

    private final JFrame parentPOS;
    private final OrderRepository orderRepo = new OrderRepository();
    private List<OrderItem> orderItems;
    private double totalPrice;
    private String username;

    JComboBox<enums.OrderType> orderTypeDropdown;
    JComboBox<enums.PaymentMethod> paymentMethodDropdown;
    private JTextField customerNameField;
    private JTextField contactField;
    private JTextField deliveryAddressField;
    private JLabel deliveryAddressLabel;
    private JPanel deliveryAddressPanel;
    private JLabel statusLabel;

    public PaymentScreen(List<OrderItem> orderItems, JFrame parentPOS) {
        this.username = ((POSScreen) parentPOS).getCashierUser().getUsername();
        this.parentPOS = parentPOS;
        this.orderItems = orderItems;
        for (OrderItem oi : orderItems) {
            totalPrice += oi.getSubTotal();
        }
        setTitle("CafeSync — Payment");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1100, 680);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
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

        bar.add(title, BorderLayout.WEST);
        return bar;
    }

    // Body: order summary + payment method
    private JPanel buildBodyPanel() {
        JPanel wrapper = new JPanel(new GridLayout(1, 2, 24, 0));
        wrapper.setBackground(BEIGE);
        wrapper.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));

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

        // Customer Name
        customerCard.add(buildFieldLabel("CUSTOMER NAME"));
        customerNameField = buildStyledTextField("Enter customer name");
        customerCard.add(customerNameField);
        customerCard.add(buildDivider());

        // Contact
        customerCard.add(buildFieldLabel("CONTACT"));
        contactField = buildStyledTextField("Enter contact number");
        customerCard.add(contactField);
        customerCard.add(buildDivider());

        // Order Type dropdown
        customerCard.add(buildFieldLabel("ORDER TYPE"));
        orderTypeDropdown = buildOrderTypeDropdown(enums.OrderType.values());
        orderTypeDropdown.setAlignmentX(Component.LEFT_ALIGNMENT);
        customerCard.add(orderTypeDropdown);

        // Delivery address when delivery is selected
        customerCard.add(Box.createRigidArea(new Dimension(0, 10)));
        deliveryAddressPanel = new JPanel();
        deliveryAddressPanel.setLayout(new BoxLayout(deliveryAddressPanel, BoxLayout.Y_AXIS));
        deliveryAddressPanel.setBackground(CARD_BG);
        deliveryAddressPanel.setAlignmentX(LEFT_ALIGNMENT);

        deliveryAddressLabel = buildFieldLabel("DELIVERY ADDRESS");
        deliveryAddressField = buildStyledTextField("Enter delivery address");
        deliveryAddressPanel.add(buildDivider());
        deliveryAddressPanel.add(deliveryAddressLabel);
        deliveryAddressPanel.add(deliveryAddressField);

        deliveryAddressPanel.setVisible(false);   // hidden by default
        customerCard.add(deliveryAddressPanel);

        orderTypeDropdown.addActionListener(e -> {
            boolean isOption2 = orderTypeDropdown.getSelectedIndex() == 1;
            deliveryAddressPanel.setVisible(isOption2);
            customerCard.revalidate();
            customerCard.repaint();
        });

        customerCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 380));

        // Payment Method 
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
        methodCard.add(Box.createRigidArea(new Dimension(0, 14)));

        paymentMethodDropdown = buildPaymentMethodDropdown(enums.PaymentMethod.values());
        paymentMethodDropdown.setAlignmentX(Component.LEFT_ALIGNMENT);
        methodCard.add(paymentMethodDropdown);

        methodCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

        leftCol.add(customerCard);
        leftCol.add(Box.createRigidArea(new Dimension(0, 20)));
        leftCol.add(methodCard);
        leftCol.add(Box.createVerticalGlue());

        //order summary
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

        for (OrderItem oi : orderItems) {
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

        JLabel totalAmt = new JLabel(String.format("PKR %.2f", totalPrice));
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

        JButton backBtn = new JButton("Back to Cart");
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
        payBtn.addActionListener(e -> {
            processPayment();
        });

        footer.add(backBtn, BorderLayout.WEST);
        footer.add(payBtn, BorderLayout.EAST);
        return footer;
    }

    private void processPayment() {
        String customerName = customerNameField.getText().trim();
        String contact = contactField.getText().trim();
        OrderType orderType = (OrderType) orderTypeDropdown.getSelectedItem();
        PaymentMethod paymentMethod = (PaymentMethod) paymentMethodDropdown.getSelectedItem();
        boolean isOption2 = orderTypeDropdown.getSelectedIndex() == 1;
        String deliveryAddress = isOption2 ? deliveryAddressField.getText().trim() : "N/A";

        // Validate delivery address when delivery is selected
        if (orderType == OrderType.DELIVERY) {
            if (deliveryAddress == null || deliveryAddress.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a delivery address for delivery orders.", "Missing Delivery Address", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        Order newOrder = new Order(this.username, orderType, customerName.isEmpty() ? "Walk-in Customer" : customerName, contact.isEmpty() ? "N/A" : contact, deliveryAddress, orderItems);
        orderRepo.save(newOrder);

        String addressLine = isOption2
                ? "<tr><td><b>Delivery Address</b></td><td>" + deliveryAddress + "</td></tr>"
                : "";

        String message =
                "<html>"
                + "<body style='font-family:SansSerif; font-size:13px; padding:10px;'>"
                + "<h2 style='color:#2c1e16; margin-bottom:12px;'>✔ Payment Successful!</h2>"
                + "<table cellpadding='6' cellspacing='0' style='width:380px;'>"
                + "<tr><td><b>Order #</b></td><td>" + newOrder.getOrderID() + "</td></tr>"
                + "<tr><td><b>Customer Name</b></td><td>" + (customerName.isEmpty() ? "—" : customerName) + "</td></tr>"
                + "<tr><td><b>Contact</b></td><td>" + (contact.isEmpty() ? "—" : contact) + "</td></tr>"
                + "<tr><td><b>Order Type</b></td><td>" + orderType + "</td></tr>"
                + addressLine
                + "<tr><td><b>Payment Method</b></td><td>" + paymentMethod + "</td></tr>"
                + "<tr><td><b>Total</b></td><td><span style='color:#c47b57; font-size:15px;'>"
                +     String.format("PKR %.2f", totalPrice)
                + "</span></td></tr>"
                + "</table>"
                + "</body></html>";

        JLabel msgLabel = new JLabel(message);
        JOptionPane pane = new JOptionPane(msgLabel,
                JOptionPane.INFORMATION_MESSAGE,
                JOptionPane.DEFAULT_OPTION);
        JDialog dialog = pane.createDialog(this, "Payment Confirmed");
        dialog.setPreferredSize(new Dimension(500, 400));
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        models.User cashier = ((POSScreen) parentPOS).getCashierUser();
        dispose();
        parentPOS.dispose();
        new POSScreen(cashier).setVisible(true);
    }

    private void goBack() {
        dispose();
        parentPOS.setVisible(true);
    }


    private JLabel buildFieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        lbl.setForeground(TEXT_DARK);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        lbl.setBorder(BorderFactory.createEmptyBorder(6, 0, 4, 0));
        return lbl;
    }

    private JTextField buildStyledTextField(String placeholder) {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setColor(new Color(180, 165, 155));
                    g2.setFont(getFont().deriveFont(Font.ITALIC));
                    Insets ins = getInsets();
                    g2.drawString(placeholder, ins.left + 2, getHeight() / 2 + g2.getFontMetrics().getAscent() / 2 - 2);
                }
            }
        };
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setForeground(TEXT_DARK);
        field.setBackground(CARD_BG);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DIVIDER),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        field.setAlignmentX(LEFT_ALIGNMENT);
        return field;
    }

    private JComboBox<enums.OrderType> buildOrderTypeDropdown(enums.OrderType[] options) {
    JComboBox<enums.OrderType> cb = new JComboBox<>(options); 
    cb.setFont(new Font("SansSerif", Font.PLAIN, 14));
    cb.setForeground(TEXT_DARK);
    cb.setBackground(CARD_BG);
    cb.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
    cb.setFocusable(false);
    cb.setBorder(BorderFactory.createLineBorder(DIVIDER));
    return cb;
}

private JComboBox<enums.PaymentMethod> buildPaymentMethodDropdown(enums.PaymentMethod[] options) {
    JComboBox<enums.PaymentMethod> cb = new JComboBox<>(options);
    cb.setFont(new Font("SansSerif", Font.PLAIN, 14));
    cb.setForeground(TEXT_DARK);
    cb.setBackground(CARD_BG);
    cb.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
    cb.setFocusable(false);
    cb.setBorder(BorderFactory.createLineBorder(DIVIDER));
    return cb;
}

    private JPanel buildDivider() {
        JPanel div = new JPanel();
        div.setBackground(DIVIDER);
        div.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        div.setAlignmentX(LEFT_ALIGNMENT);
        return div;
    }
}