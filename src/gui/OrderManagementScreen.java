package gui;

import java.awt.*;
import java.awt.event.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import models.User;
import models.Order;
import enums.OrderStatus;
import repository.OrderRepository;
import enums.DateRange;

public class OrderManagementScreen extends JFrame {

    private static final Color BROWN        = new Color(44, 30, 22);
    private static final Color BEIGE        = new Color(245, 242, 235);
    private static final Color CARD_BG      = new Color(255, 255, 255);
    private static final Color TEXT_DARK    = new Color(60, 45, 35);
    private static final Color TEXT_MUTED   = new Color(130, 115, 105);
    private static final Color DIVIDER      = new Color(230, 225, 218);
    private static final Color TEAL         = new Color(52, 152, 152);
    private static final Color AMBER        = new Color(210, 140, 50);
    private static final Color ROSE         = new Color(185, 70, 70);
    private static final Color SAGE         = new Color(85, 140, 100);
    private static final Color STEEL        = new Color(90, 110, 140);
    private static final Color ROW_HOVER    = new Color(248, 245, 240);

    private final User user;
    private List<Order> allOrders;
    private DateRange currentRange = DateRange.TODAY;
    private String currentStatus = "All";
    private JPanel ordersPanel;
    private JLabel countLabel;
    private JFrame parentPOS;
    private final OrderRepository OrderRepo = new OrderRepository();

    public OrderManagementScreen(User user,JFrame parentPOS) {
        this.user = user;
        this.allOrders = OrderRepo.findAll();
        this.parentPOS = parentPOS;
        setTitle("CafeSync — Order Management  |  " + user.getName());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setResizable(true);
        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(BEIGE);
        root.add(buildTopBar(), BorderLayout.NORTH);
        root.add(buildBody(), BorderLayout.CENTER);
        setContentPane(root);
    }

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(BROWN);
        bar.setPreferredSize(new Dimension(0, 60));
        bar.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 24));

        JLabel logo = new JLabel("CafeSync  ·  Order Management");
        logo.setFont(new Font("Serif", Font.BOLD, 22));
        logo.setForeground(Color.WHITE);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 12));
        right.setOpaque(false);

        JLabel managerLabel = new JLabel("Manager: " + user.getName());
        managerLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        managerLabel.setForeground(new Color(200, 180, 160));

        JButton backBtn = makeHeaderButton("← Back to Menu", TEAL);
        backBtn.addActionListener(e -> goBack());

        right.add(managerLabel);
        right.add(backBtn);
        bar.add(logo, BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    private JPanel buildBody() {
        JPanel body = new JPanel(new BorderLayout(0, 12));
        body.setBackground(BEIGE);
        body.setBorder(new EmptyBorder(16, 22, 16, 22));
        body.add(buildControlBar(), BorderLayout.NORTH);
        body.add(buildOrdersSection(), BorderLayout.CENTER);
        return body;
    }

    private JPanel buildControlBar() {
        JPanel bar = new JPanel();
        bar.setLayout(new BoxLayout(bar, BoxLayout.Y_AXIS));
        bar.setOpaque(false);

        JPanel row1 = new JPanel(new BorderLayout());
        row1.setOpaque(false);

        JPanel rangeGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        rangeGroup.setOpaque(false);
        JLabel rangeLabel = new JLabel("Date Range: ");
        rangeLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        rangeLabel.setForeground(TEXT_DARK);
        rangeGroup.add(rangeLabel);

        ButtonGroup rangeButtons = new ButtonGroup();
        DateRange[] ranges = { DateRange.TODAY, DateRange.THIS_WEEK, DateRange.THIS_MONTH };
        for (DateRange r : ranges) {
            JToggleButton tb = makeToggleButton(r.toString());
            if (r.equals(currentRange)) tb.setSelected(true);
            rangeButtons.add(tb);
            tb.addActionListener(e -> { currentRange = r; refreshOrders(); });
            rangeGroup.add(tb);
        }

        countLabel = new JLabel();
        countLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        countLabel.setForeground(TEXT_MUTED);

        row1.add(rangeGroup, BorderLayout.WEST);
        row1.add(countLabel, BorderLayout.EAST);
        bar.add(row1);
        bar.add(Box.createRigidArea(new Dimension(0, 8)));

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        row2.setOpaque(false);
        JLabel statusLabel = new JLabel("Filter by Status: ");
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        statusLabel.setForeground(TEXT_DARK);
        row2.add(statusLabel);

        ButtonGroup statusButtons = new ButtonGroup();
        String[] statuses = { "All", "PENDING", "PREPARING", "OUT_FOR_DELIVERY", "COMPLETED", "CANCELLED" };
        for (String s : statuses) {
            JToggleButton tb = makeToggleButton(s.equals("OUT_FOR_DELIVERY") ? "Out for Delivery" : capitalize(s));
            tb.putClientProperty("statusKey", s);
            if (s.equals(currentStatus)) tb.setSelected(true);
            statusButtons.add(tb);
            tb.addActionListener(e -> { currentStatus = s; refreshOrders(); });
            row2.add(tb);
        }

        bar.add(row2);
        return bar;
    }

    private JPanel buildOrdersSection() {
        JPanel section = new JPanel(new BorderLayout());
        section.setOpaque(false);

        String[] cols = { "Order ID", "Date", "Time", "Customer", "Contact", "Type", "Items", "Total", "Status", "Actions" };
        int[] colWidths = { 120, 90, 70, 110, 105, 80, 160, 75, 110, 200 };

        JPanel headerPanel = new JPanel(new GridBagLayout());
        headerPanel.setBackground(BROWN);
        headerPanel.setBorder(new EmptyBorder(9, 14, 9, 14));
        GridBagConstraints hgbc = new GridBagConstraints();
        hgbc.fill = GridBagConstraints.HORIZONTAL;
        hgbc.gridy = 0;
        for (int i = 0; i < cols.length; i++) {
            hgbc.gridx = i;
            hgbc.weightx = colWidths[i];
            JLabel lbl = new JLabel(cols[i]);
            lbl.setFont(new Font("SansSerif", Font.BOLD, 11));
            lbl.setForeground(new Color(220, 200, 180));
            headerPanel.add(lbl, hgbc);
        }

        ordersPanel = new JPanel();
        ordersPanel.setLayout(new BoxLayout(ordersPanel, BoxLayout.Y_AXIS));
        ordersPanel.setBackground(CARD_BG);

        JScrollPane scroll = new JScrollPane(ordersPanel);
        scroll.setBorder(BorderFactory.createLineBorder(DIVIDER, 1));
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.getVerticalScrollBar().setUnitIncrement(14);

        section.add(headerPanel, BorderLayout.NORTH);
        section.add(scroll, BorderLayout.CENTER);

        refreshOrders();
        return section;
    }

    private void refreshOrders() {
        ordersPanel.removeAll();
        List<Order> filtered = getFilteredOrders();

        if (countLabel != null) {
            countLabel.setText(filtered.size() + " order" + (filtered.size() != 1 ? "s" : "") + " found");
        }

        if (filtered.isEmpty()) {
            JPanel empty = new JPanel(new BorderLayout());
            empty.setBackground(CARD_BG);
            empty.setPreferredSize(new Dimension(0, 120));
            JLabel msg = new JLabel("No orders found for this filter.", SwingConstants.CENTER);
            msg.setFont(new Font("SansSerif", Font.PLAIN, 14));
            msg.setForeground(TEXT_MUTED);
            empty.add(msg, BorderLayout.CENTER);
            ordersPanel.add(empty);
        } else {
            DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("MMM dd, yyyy");
            DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("hh:mm a");
            for (int i = 0; i < filtered.size(); i++) {
                Order o = filtered.get(i);
                ordersPanel.add(buildOrderRow(o, dateFmt, timeFmt, i % 2 == 0));
                JSeparator sep = new JSeparator();
                sep.setForeground(DIVIDER);
                sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
                ordersPanel.add(sep);
            }
        }

        ordersPanel.revalidate();
        ordersPanel.repaint();
    }

    private JPanel buildOrderRow(Order order, DateTimeFormatter dateFmt, DateTimeFormatter timeFmt, boolean even) {
        JPanel row = new JPanel(new GridBagLayout());
        row.setBackground(even ? CARD_BG : new Color(250, 248, 244));
        row.setBorder(new EmptyBorder(10, 14, 10, 14));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));

        row.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { row.setBackground(ROW_HOVER); }
            public void mouseExited(MouseEvent e)  { row.setBackground(even ? CARD_BG : new Color(250, 248, 244)); }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridy = 0;
        int[] colWidths = { 120, 90, 70, 110, 105, 80, 160, 75, 110, 200 };

        String itemsSummary = buildItemsSummary(order.getOrderItems());
        String statusStr = order.getOrderStatus().toString();
        Color statusColor = statusColor(order.getOrderStatus());

        Object[] cells = {
            order.getOrderID(),
            order.getOrderDate().format(dateFmt),
            order.getOrderTime().format(timeFmt),
            order.getCustomerName(),
            order.getCustomerContact(),
            order.getOrderType().toString(),
            itemsSummary,
            String.format("$%.2f", order.getTotalPrice()),
            ""
        };

        for (int i = 0; i < cells.length; i++) {
            gbc.gridx = i;
            gbc.weightx = colWidths[i];
            if (i == 8) {
                JLabel badge = makeStatusBadge(statusStr, statusColor);
                row.add(badge, gbc);
            } else {
                JLabel lbl = new JLabel(cells[i].toString());
                lbl.setFont(new Font("SansSerif", i == 0 ? Font.BOLD : Font.PLAIN, 12));
                lbl.setForeground(i == 0 ? TEXT_DARK : TEXT_MUTED);
                if (i == 6) {
                    lbl.setToolTipText(fullItemsList(order.getOrderItems()));
                }
                row.add(lbl, gbc);
            }
        }

        gbc.gridx = 9;
        gbc.weightx = colWidths[9];
        row.add(buildActionPanel(order), gbc);

        return row;
    }

    private JLabel makeStatusBadge(String status, Color color) {
        JLabel lbl = new JLabel(status.replace("_", " ")) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 28));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(color);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        lbl.setFont(new Font("SansSerif", Font.BOLD, 10));
        lbl.setForeground(color);
        lbl.setPreferredSize(new Dimension(100, 22));
        lbl.setMaximumSize(new Dimension(100, 22));
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        lbl.setOpaque(false);
        return lbl;
    }

    private JPanel buildActionPanel(Order order) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        p.setOpaque(false);
        OrderStatus status = order.getOrderStatus();

        if (status == OrderStatus.PENDING) {
            JButton accept = makeActionButton("Accept", SAGE);
            JButton cancel = makeActionButton("Cancel", ROSE);
            accept.addActionListener(e -> { order.prepareOrder(); refreshOrders(); });
            cancel.addActionListener(e -> { order.cancelOrder(); refreshOrders(); });
            p.add(accept);
            p.add(cancel);
        } else if (status == OrderStatus.PREPARING) {
            JButton deliver = makeActionButton("Send Out", STEEL);
            JButton complete = makeActionButton("Complete", SAGE);
            JButton cancel = makeActionButton("Cancel", ROSE);
            deliver.addActionListener(e -> { order.outForDelivery(); refreshOrders(); });
            complete.addActionListener(e -> { order.completeOrder(); refreshOrders(); });
            cancel.addActionListener(e -> { order.cancelOrder(); refreshOrders(); });
            p.add(deliver);
            p.add(complete);
            p.add(cancel);
        } else if (status == OrderStatus.OUT_FOR_DELIVERY) {
            JButton complete = makeActionButton("Complete", SAGE);
            JButton cancel = makeActionButton("Cancel", ROSE);
            complete.addActionListener(e -> { order.completeOrder(); refreshOrders(); });
            cancel.addActionListener(e -> { order.cancelOrder(); refreshOrders(); });
            p.add(complete);
            p.add(cancel);
        } else {
            JLabel done = new JLabel(status == OrderStatus.COMPLETED ? "✓ Completed" : "✗ Cancelled");
            done.setFont(new Font("SansSerif", Font.PLAIN, 11));
            done.setForeground(TEXT_MUTED);
            p.add(done);
        }

        return p;
    }

    public List<Order> getFilteredOrders() {
        LocalDate startDate = null;
        LocalDate endDate = null;
        
        switch (currentRange) {
            case DateRange.TODAY:
                startDate = LocalDate.now();
                endDate = LocalDate.now();
                break;
            case DateRange.THIS_WEEK:
                startDate = LocalDate.now().with(DayOfWeek.MONDAY);
                endDate = startDate.plusDays(6);
                break;
            case DateRange.THIS_MONTH:
                startDate = LocalDate.now().withDayOfMonth(1);
                endDate = startDate.plusMonths(1).minusDays(1);
                break;
        }
        List<Order> currentOrders = OrderRepo.findOrdersInRange(startDate, endDate);
        return currentOrders;
    }

    private String buildItemsSummary(List<?> items) {
        if (items == null || items.isEmpty()) return "—";
        String first = items.get(0).toString();
        if (first.length() > 22) first = first.substring(0, 20) + "…";
        return items.size() == 1 ? first : first + " +" + (items.size() - 1) + " more";
    }

    private String fullItemsList(List<?> items) {
        if (items == null || items.isEmpty()) return "No items";
        StringBuilder sb = new StringBuilder("<html>");
        for (Object item : items) sb.append(item.toString()).append("<br>");
        sb.append("</html>");
        return sb.toString();
    }

    private Color statusColor(OrderStatus status) {
        return switch (status) {
            case COMPLETED        -> SAGE;
            case CANCELLED        -> ROSE;
            case PREPARING        -> AMBER;
            case OUT_FOR_DELIVERY -> STEEL;
            default               -> new Color(160, 130, 80);
        };
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.charAt(0) + s.substring(1).toLowerCase();
    }

    private JToggleButton makeToggleButton(String text) {
        JToggleButton btn = new JToggleButton(text) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isSelected() ? BROWN : CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.setColor(isSelected() ? Color.WHITE : TEXT_MUTED);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        btn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        int w = text.length() * 8 + 20;
        btn.setPreferredSize(new Dimension(Math.max(80, w), 28));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton makeActionButton(String text, Color color) {
        JButton btn = new JButton(text) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover()
                        ? color
                        : new Color(color.getRed(), color.getGreen(), color.getBlue(), 30));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(getModel().isRollover() ? Color.WHITE : color);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        btn.setFont(new Font("SansSerif", Font.BOLD, 11));
        btn.setPreferredSize(new Dimension(74, 26));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

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
    private void goBack() {
        dispose();
        parentPOS.setVisible(true);
    }
}