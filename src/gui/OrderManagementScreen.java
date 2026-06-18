package gui;

import enums.DateRange;
import enums.OrderStatus;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import models.Order;
import models.OrderItem;
import models.User;
import repository.OrderRepository;

public class OrderManagementScreen extends JFrame {

    // ── Palette (shared design system) ────────────────────────────────────────
    private static final Color SIDEBAR_BG    = new Color(26, 20, 16);
    private static final Color SIDEBAR_HOVER = new Color(44, 34, 26);
    private static final Color SIDEBAR_ACTIVE= new Color(55, 42, 32);
    private static final Color CANVAS_BG     = new Color(247, 245, 241);
    private static final Color CARD_BG       = new Color(255, 255, 255);
    private static final Color AMBER         = new Color(212, 135, 90);
    private static final Color AMBER_DARK    = new Color(185, 108, 62);
    private static final Color TEXT_DARK     = new Color(28, 22, 16);
    private static final Color TEXT_MID      = new Color(90, 75, 65);
    private static final Color TEXT_MUTED    = new Color(155, 142, 132);
    private static final Color BORDER_COLOR  = new Color(234, 230, 224);
    private static final Color ROW_HOVER     = new Color(250, 248, 244);

    // Status colours
    private static final Color SAGE  = new Color(72, 158, 100);
    private static final Color ROSE  = new Color(196, 64, 64);
    private static final Color STEEL = new Color(80, 110, 160);
    private static final Color TEAL  = new Color(42, 148, 140);
    private static final Color OCHRE = new Color(190, 138, 42);

    // ── State ─────────────────────────────────────────────────────────────────
    private final User    user;
    private DateRange     currentRange  = DateRange.TODAY;
    private OrderStatus   currentStatus = OrderStatus.PENDING;
    private JPanel        ordersPanel;
    private JLabel        countLabel;
    private final JFrame  parentPOS;
    private final OrderRepository orderRepo = new OrderRepository();

    // ══════════════════════════════════════════════════════════════════════════
    public OrderManagementScreen(User user, JFrame parentPOS) {
        this.user      = user;
        this.parentPOS = parentPOS;
        setTitle("CafeSync — Order Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1260, 760);
        setMinimumSize(new Dimension(1000, 600));
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(true);
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        buildUI();
    }

    // ── Root ──────────────────────────────────────────────────────────────────
    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(CANVAS_BG);
        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildMainArea(), BorderLayout.CENTER);
        setContentPane(root);
    }

    // ── Sidebar (identical structure to POS) ──────────────────────────────────
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(170, 0));
        sidebar.setMaximumSize(new Dimension(220, Integer.MAX_VALUE));

        // Logo
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

        JLabel logoSub = new JLabel("Order Management");
        logoSub.setFont(new Font("SansSerif", Font.PLAIN, 11));
        logoSub.setForeground(new Color(155, 135, 115));
        logoSub.setAlignmentX(LEFT_ALIGNMENT);

        logoArea.add(logoText);
        logoArea.add(Box.createRigidArea(new Dimension(0, 2)));
        logoArea.add(logoSub);

        JPanel accentLine = new JPanel();
        accentLine.setBackground(AMBER);
        accentLine.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        accentLine.setPreferredSize(new Dimension(0, 2));
        accentLine.setAlignmentX(LEFT_ALIGNMENT);

        sidebar.add(logoArea);
        sidebar.add(accentLine);
        sidebar.add(Box.createRigidArea(new Dimension(0, 16)));

        // Navigation
        sidebar.add(sidebarSectionLabel("NAVIGATION"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 4)));
        // "Point of Sale" nav item goes back to POS
        sidebar.add(sidebarNavItemClickable("Point of Sale", "●", false, e -> goBack()));
        sidebar.add(sidebarNavItem("Orders", "☰", true));

        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));

        // Session
        sidebar.add(sidebarSectionLabel("SESSION"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 4)));

        // User identity pill
        JPanel userPill = new JPanel();
        userPill.setLayout(new BoxLayout(userPill, BoxLayout.X_AXIS));
        userPill.setBackground(SIDEBAR_ACTIVE);
        userPill.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        userPill.setAlignmentX(LEFT_ALIGNMENT);
        userPill.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 16));

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
                String initial = user.getName().substring(0, 1).toUpperCase();
                g2.drawString(initial,
                        (26 - fm.stringWidth(initial)) / 2,
                        (26 - fm.getHeight()) / 2 + fm.getAscent());
            }
        };
        initCircle.setPreferredSize(new Dimension(26, 26));
        initCircle.setMinimumSize(new Dimension(26, 26));
        initCircle.setMaximumSize(new Dimension(26, 26));
        initCircle.setOpaque(false);

        JLabel nameLabel = new JLabel(user.getName());
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        nameLabel.setForeground(Color.WHITE);

        userPill.add(initCircle);
        userPill.add(Box.createRigidArea(new Dimension(10, 0)));
        userPill.add(nameLabel);
        userPill.add(Box.createHorizontalGlue());

        sidebar.add(userPill);
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));

        sidebar.add(sidebarNavItemClickable("← Back to POS", "⎋", false, e -> goBack()));

        sidebar.add(Box.createVerticalGlue());

        // Version footer
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
        item.setCursor(action != null ? new Cursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());

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
        item.add(Box.createHorizontalGlue());

        if (action != null) {
            item.addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { item.setBackground(SIDEBAR_HOVER); }
                @Override public void mouseExited(MouseEvent e)  { item.setBackground(active ? SIDEBAR_ACTIVE : SIDEBAR_BG); }
                @Override public void mouseClicked(MouseEvent e) { action.actionPerformed(new ActionEvent(item, 0, "")); }
            });
        }
        return item;
    }

    // ── Main content area ─────────────────────────────────────────────────────
    private JPanel buildMainArea() {
        JPanel area = new JPanel(new BorderLayout(0, 0));
        area.setBackground(CANVAS_BG);
        area.setBorder(BorderFactory.createEmptyBorder(28, 28, 20, 28));

        // Page header
        JPanel pageHeader = new JPanel(new BorderLayout());
        pageHeader.setBackground(CANVAS_BG);
        pageHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JPanel titleStack = new JPanel();
        titleStack.setLayout(new BoxLayout(titleStack, BoxLayout.Y_AXIS));
        titleStack.setBackground(CANVAS_BG);

        JLabel pageTitle = new JLabel("Orders");
        pageTitle.setFont(new Font("Serif", Font.BOLD, 28));
        pageTitle.setForeground(TEXT_DARK);
        pageTitle.setAlignmentX(LEFT_ALIGNMENT);

        JLabel pageSub = new JLabel("Review, accept and manage incoming orders");
        pageSub.setFont(new Font("SansSerif", Font.PLAIN, 13));
        pageSub.setForeground(TEXT_MUTED);
        pageSub.setAlignmentX(LEFT_ALIGNMENT);

        titleStack.add(pageTitle);
        titleStack.add(Box.createRigidArea(new Dimension(0, 3)));
        titleStack.add(pageSub);

        countLabel = new JLabel("");
        countLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        countLabel.setForeground(TEXT_MUTED);

        pageHeader.add(titleStack,  BorderLayout.WEST);
        pageHeader.add(countLabel,  BorderLayout.EAST);

        area.add(pageHeader,         BorderLayout.NORTH);
        area.add(buildControlBar(),  BorderLayout.CENTER);

        return area;
    }

    // ── Filter / control bar ──────────────────────────────────────────────────
    private JPanel buildControlBar() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 14));
        wrapper.setBackground(CANVAS_BG);

        // ── Filter strip (date + status) ──────────────────────────────────
        JPanel filterStrip = new JPanel();
        filterStrip.setLayout(new BoxLayout(filterStrip, BoxLayout.Y_AXIS));
        filterStrip.setBackground(CANVAS_BG);

        // Row 1 – date range
        JPanel rangeRow = new JPanel();
        rangeRow.setLayout(new BoxLayout(rangeRow, BoxLayout.X_AXIS));
        rangeRow.setBackground(CANVAS_BG);
        rangeRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        rangeRow.setAlignmentX(LEFT_ALIGNMENT);

        JLabel rangeLabel = new JLabel("Date Range");
        rangeLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        rangeLabel.setForeground(TEXT_MID);
        rangeRow.add(rangeLabel);
        rangeRow.add(Box.createRigidArea(new Dimension(14, 0)));

        ButtonGroup rangeGroup = new ButtonGroup();
        DateRange[] ranges = { DateRange.TODAY, DateRange.THIS_WEEK, DateRange.THIS_MONTH };
        for (DateRange r : ranges) {
            JToggleButton tb = filterToggle(r.toString(), r.equals(currentRange));
            rangeGroup.add(tb);
            tb.addActionListener(e -> { currentRange = r; refreshOrders(); });
            rangeRow.add(tb);
            rangeRow.add(Box.createRigidArea(new Dimension(6, 0)));
        }
        rangeRow.add(Box.createHorizontalGlue());

        // Row 2 – status
        JPanel statusRow = new JPanel();
        statusRow.setLayout(new BoxLayout(statusRow, BoxLayout.X_AXIS));
        statusRow.setBackground(CANVAS_BG);
        statusRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        statusRow.setAlignmentX(LEFT_ALIGNMENT);

        JLabel statusLabel = new JLabel("Status");
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        statusLabel.setForeground(TEXT_MID);
        statusRow.add(statusLabel);
        statusRow.add(Box.createRigidArea(new Dimension(14, 0)));

        ButtonGroup statusGroup = new ButtonGroup();
        OrderStatus[] statuses = {
            OrderStatus.PENDING, OrderStatus.PREPARING,
            OrderStatus.OUT_FOR_DELIVERY, OrderStatus.COMPLETED, OrderStatus.CANCELLED
        };
        String[] statusLabels = { "Pending", "Preparing", "Out for Delivery", "Completed", "Cancelled" };
        for (int i = 0; i < statuses.length; i++) {
            final OrderStatus s = statuses[i];
            JToggleButton tb = filterToggle(statusLabels[i], s.equals(currentStatus));
            statusGroup.add(tb);
            tb.addActionListener(e -> { currentStatus = s; refreshOrders(); });
            statusRow.add(tb);
            statusRow.add(Box.createRigidArea(new Dimension(6, 0)));
        }
        statusRow.add(Box.createHorizontalGlue());

        filterStrip.add(rangeRow);
        filterStrip.add(Box.createRigidArea(new Dimension(0, 10)));
        filterStrip.add(statusRow);

        // ── Orders table ──────────────────────────────────────────────────
        JPanel tableSection = new JPanel(new BorderLayout(0, 0));
        tableSection.setBackground(CANVAS_BG);

        // Table header bar
        String[] cols      = { "Order ID", "Date", "Time", "Customer", "Contact", "Type", "Items", "Total", "Status", "Actions" };
        int[]    colWidths = {  120,         90,     70,     110,        105,       80,     160,     80,      110,      200 };

        JPanel headerBar = new JPanel(new GridBagLayout());
        headerBar.setBackground(SIDEBAR_BG);
        headerBar.setBorder(new EmptyBorder(10, 16, 10, 16));
        // top rounded corners only — use a custom panel
        GridBagConstraints hgbc = new GridBagConstraints();
        hgbc.fill = GridBagConstraints.HORIZONTAL;
        hgbc.gridy = 0;
        for (int i = 0; i < cols.length; i++) {
            hgbc.gridx    = i;
            hgbc.weightx  = colWidths[i];
            JLabel lbl = new JLabel(cols[i]);
            lbl.setFont(new Font("SansSerif", Font.BOLD, 11));
            lbl.setForeground(new Color(180, 160, 140));
            headerBar.add(lbl, hgbc);
        }

        ordersPanel = new JPanel();
        ordersPanel.setLayout(new BoxLayout(ordersPanel, BoxLayout.Y_AXIS));
        ordersPanel.setBackground(CARD_BG);

        JScrollPane scroll = new JScrollPane(ordersPanel);
        scroll.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, BORDER_COLOR));
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        scroll.setBackground(CARD_BG);
        scroll.getViewport().setBackground(CARD_BG);

        tableSection.add(headerBar, BorderLayout.NORTH);
        tableSection.add(scroll,    BorderLayout.CENTER);

        // Assemble
        JPanel body = new JPanel(new BorderLayout(0, 16));
        body.setBackground(CANVAS_BG);
        body.add(filterStrip,  BorderLayout.NORTH);
        body.add(tableSection, BorderLayout.CENTER);

        refreshOrders();
        return body;
    }

    // ── Order list ────────────────────────────────────────────────────────────
    private void refreshOrders() {
        ordersPanel.removeAll();
        List<Order> filtered = getFilteredOrders();

        if (countLabel != null) {
            countLabel.setText(filtered.size() + " order" + (filtered.size() != 1 ? "s" : "") + " found");
        }

        if (filtered.isEmpty()) {
            JPanel empty = new JPanel(new BorderLayout());
            empty.setBackground(CARD_BG);
            empty.setPreferredSize(new Dimension(0, 140));

            JPanel inner = new JPanel();
            inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
            inner.setBackground(CARD_BG);

            JLabel icon = new JLabel("⊘");
            icon.setFont(new Font("SansSerif", Font.PLAIN, 30));
            icon.setForeground(new Color(220, 215, 208));
            icon.setAlignmentX(CENTER_ALIGNMENT);

            JLabel msg = new JLabel("No orders found for this filter");
            msg.setFont(new Font("SansSerif", Font.PLAIN, 13));
            msg.setForeground(TEXT_MUTED);
            msg.setAlignmentX(CENTER_ALIGNMENT);

            inner.add(Box.createVerticalGlue());
            inner.add(icon);
            inner.add(Box.createRigidArea(new Dimension(0, 8)));
            inner.add(msg);
            inner.add(Box.createVerticalGlue());

            empty.add(inner, BorderLayout.CENTER);
            ordersPanel.add(empty);
        } else {
            DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("MMM dd, yyyy");
            DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("hh:mm a");
            for (int i = 0; i < filtered.size(); i++) {
                Order o = filtered.get(i);
                ordersPanel.add(buildOrderRow(o, dateFmt, timeFmt, i % 2 == 0));
                JPanel sep = new JPanel();
                sep.setBackground(BORDER_COLOR);
                sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
                ordersPanel.add(sep);
            }
        }

        ordersPanel.revalidate();
        ordersPanel.repaint();
    }

    private JPanel buildOrderRow(Order order, DateTimeFormatter dateFmt,
                                  DateTimeFormatter timeFmt, boolean even) {
        JPanel row = new JPanel(new GridBagLayout());
        row.setBackground(even ? CARD_BG : new Color(251, 249, 246));
        row.setBorder(new EmptyBorder(11, 16, 11, 16));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));

        row.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { row.setBackground(ROW_HOVER); }
            @Override public void mouseExited(MouseEvent e)  { row.setBackground(even ? CARD_BG : new Color(251, 249, 246)); }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill  = GridBagConstraints.HORIZONTAL;
        gbc.gridy = 0;
        int[] colWidths = { 120, 90, 70, 110, 105, 80, 160, 80, 110, 200 };

        String itemsSummary = buildItemsSummary(order.getOrderItems());
        Color  statusColor  = statusColor(order.getOrderStatus());
        String statusStr    = order.getOrderStatus().toString().replace("_", " ");

        Object[] cells = {
            order.getOrderID(),
            order.getOrderDate().format(dateFmt),
            order.getOrderTime().format(timeFmt),
            order.getCustomerName(),
            order.getCustomerContact(),
            order.getOrderType().toString(),
            itemsSummary,
            String.format("PKR %.0f", order.getTotalPrice()),
            ""   // status badge placeholder
        };

        for (int i = 0; i < cells.length; i++) {
            gbc.gridx   = i;
            gbc.weightx = colWidths[i];
            if (i == 8) {
                row.add(makeStatusBadge(statusStr, statusColor), gbc);
            } else {
                JLabel lbl = new JLabel(cells[i].toString());
                lbl.setFont(new Font("SansSerif", i == 0 ? Font.BOLD : Font.PLAIN, 12));
                lbl.setForeground(i == 0 ? TEXT_DARK : TEXT_MUTED);
                if (i == 6) lbl.setToolTipText(fullItemsList(order.getOrderItems()));
                row.add(lbl, gbc);
            }
        }

        gbc.gridx   = 9;
        gbc.weightx = colWidths[9];
        row.add(buildActionPanel(order), gbc);

        return row;
    }

    private JLabel makeStatusBadge(String status, Color color) {
        JLabel lbl = new JLabel(status) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 22));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.setColor(color);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth()  - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        lbl.setFont(new Font("SansSerif", Font.BOLD, 10));
        lbl.setForeground(color);
        lbl.setPreferredSize(new Dimension(108, 22));
        lbl.setMaximumSize(new Dimension(108, 22));
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
            accept.addActionListener(e -> { order.prepareOrder();   orderRepo.update(order); refreshOrders(); });
            cancel.addActionListener(e -> { order.cancelOrder();    orderRepo.update(order); refreshOrders(); });
            p.add(accept); p.add(cancel);

        } else if (status == OrderStatus.PREPARING) {
            JButton deliver  = makeActionButton("Send Out", STEEL);
            JButton complete = makeActionButton("Complete", SAGE);
            JButton cancel   = makeActionButton("Cancel",   ROSE);
            deliver.addActionListener(e ->  { order.outForDelivery(); orderRepo.update(order); refreshOrders(); });
            complete.addActionListener(e -> { order.completeOrder();  orderRepo.update(order); refreshOrders(); });
            cancel.addActionListener(e ->   { order.cancelOrder();    orderRepo.update(order); refreshOrders(); });
            p.add(deliver); p.add(complete); p.add(cancel);

        } else if (status == OrderStatus.OUT_FOR_DELIVERY) {
            JButton complete = makeActionButton("Complete", SAGE);
            JButton cancel   = makeActionButton("Cancel",   ROSE);
            complete.addActionListener(e -> { order.completeOrder(); orderRepo.update(order); refreshOrders(); });
            cancel.addActionListener(e ->   { order.cancelOrder();   orderRepo.update(order); refreshOrders(); });
            p.add(complete); p.add(cancel);

        } else {
            JLabel done = new JLabel(status == OrderStatus.COMPLETED ? "✓ Completed" : "✗ Cancelled");
            done.setFont(new Font("SansSerif", Font.PLAIN, 11));
            done.setForeground(TEXT_MUTED);
            p.add(done);
        }
        return p;
    }

    // ── Unchanged business logic ───────────────────────────────────────────────
    public List<Order> getFilteredOrders() {
        LocalDate startDate = null;
        LocalDate endDate   = null;

        switch (currentRange) {
            case DateRange.TODAY:
                startDate = LocalDate.now();
                endDate   = LocalDate.now();
                break;
            case DateRange.THIS_WEEK:
                startDate = LocalDate.now().with(DayOfWeek.MONDAY);
                endDate   = startDate.plusDays(6);
                break;
            case DateRange.THIS_MONTH:
                startDate = LocalDate.now().withDayOfMonth(1);
                endDate   = startDate.plusMonths(1).minusDays(1);
                break;
        }

        List<Order> currentOrders = orderRepo.findOrdersInRange(startDate, endDate);
        List<Order> filtered      = new ArrayList<>();
        for (Order o : currentOrders) {
            if (o.getOrderStatus() == currentStatus) filtered.add(o);
        }
        return filtered;
    }

    private String buildItemsSummary(List<OrderItem> items) {
        if (items == null || items.isEmpty()) return "—";
        String first = items.get(0).getMenuItem().getItemName();
        if (first.length() > 22) first = first.substring(0, 20) + "…";
        return items.size() == 1 ? first : first + " +" + (items.size() - 1) + " more";
    }

    private String fullItemsList(List<OrderItem> items) {
        if (items == null || items.isEmpty()) return "No items";
        StringBuilder sb = new StringBuilder("<html>");
        for (OrderItem item : items)
            sb.append(item.getMenuItem().getItemName()).append(" x ").append(item.getQuantity()).append("<br>");
        sb.append("</html>");
        return sb.toString();
    }

    private Color statusColor(OrderStatus status) {
        return switch (status) {
            case COMPLETED        -> SAGE;
            case CANCELLED        -> ROSE;
            case PREPARING        -> OCHRE;
            case OUT_FOR_DELIVERY -> STEEL;
            default               -> new Color(160, 130, 80);
        };
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.charAt(0) + s.substring(1).toLowerCase();
    }

    // ── Widget helpers ────────────────────────────────────────────────────────
    private JToggleButton filterToggle(String text, boolean selected) {
        JToggleButton btn = new JToggleButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isSelected() ? SIDEBAR_BG : CARD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                if (!isSelected()) {
                    g2.setColor(BORDER_COLOR);
                    g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 8, 8));
                }
                g2.setColor(isSelected() ? Color.WHITE : TEXT_MID);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth()  - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        btn.setSelected(selected);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        int w = text.length() * 8 + 24;
        btn.setPreferredSize(new Dimension(Math.max(80, w), 30));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton makeActionButton(String text, Color color) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean hovered = getModel().isRollover();
                g2.setColor(hovered
                        ? color
                        : new Color(color.getRed(), color.getGreen(), color.getBlue(), 28));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.setColor(hovered ? Color.WHITE : color);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth()  - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        btn.setFont(new Font("SansSerif", Font.BOLD, 11));
        btn.setPreferredSize(new Dimension(76, 27));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void goBack() {
        dispose();
        parentPOS.setVisible(true);
    }
}