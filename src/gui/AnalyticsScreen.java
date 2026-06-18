package gui;

import analytics.AnalyticsManager;
import enums.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import models.User;

public class AnalyticsScreen extends JFrame {

    // ── Palette ───────────────────────────────────────────────────────────────
    private static final Color SIDEBAR_BG    = new Color(26, 20, 16);
    private static final Color SIDEBAR_HOVER = new Color(44, 34, 26);
    private static final Color SIDEBAR_ACTIVE= new Color(55, 42, 32);
    private static final Color CANVAS_BG     = new Color(247, 245, 241);
    private static final Color CARD_BG       = new Color(255, 255, 255);
    private static final Color AMBER         = new Color(212, 135, 90);
    private static final Color TEAL          = new Color(42, 148, 140);
    private static final Color TEXT_DARK     = new Color(28, 22, 16);
    private static final Color TEXT_MID      = new Color(90, 75, 65);
    private static final Color TEXT_MUTED    = new Color(155, 142, 132);
    private static final Color BORDER_COLOR  = new Color(234, 230, 224);

    private static final Color C_TEAL  = new Color(42, 148, 140);
    private static final Color C_AMBER = new Color(210, 155, 55);
    private static final Color C_ROSE  = new Color(196, 80, 80);
    private static final Color C_SAGE  = new Color(72, 158, 100);
    private static final Color C_JADE  = new Color(20, 130, 108);

    // ── State ─────────────────────────────────────────────────────────────────
    private final User             manager;
    private final AnalyticsManager analytics;
    private DateRange              currentRange = DateRange.THIS_WEEK;

    // Live content panel (swapped on filter change)
    private JPanel contentPanel;

    // ══════════════════════════════════════════════════════════════════════════
    public AnalyticsScreen(User manager) {
        this.manager   = manager;
        this.analytics = new AnalyticsManager();
        setTitle("CafeSync — Analytics");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1260, 760);
        setMinimumSize(new Dimension(1060, 640));
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

    // ── Main area: header + live content in a BorderLayout ────────────────────
    private JPanel buildMainArea() {
        JPanel area = new JPanel(new BorderLayout(0, 0));
        area.setBackground(CANVAS_BG);
        area.setBorder(new EmptyBorder(18, 20, 14, 20));

        area.add(buildPageHeader(), BorderLayout.NORTH);

        contentPanel = buildContentPanel();
        area.add(contentPanel, BorderLayout.CENTER);
        return area;
    }

    // ── Page header ───────────────────────────────────────────────────────────
    private JPanel buildPageHeader() {
        JPanel row = new JPanel(new BorderLayout(0, 0));
        row.setBackground(CANVAS_BG);
        row.setBorder(new EmptyBorder(0, 0, 14, 0));

        JPanel titleStack = new JPanel();
        titleStack.setLayout(new BoxLayout(titleStack, BoxLayout.Y_AXIS));
        titleStack.setBackground(CANVAS_BG);

        JLabel pageTitle = new JLabel("Analytics");
        pageTitle.setFont(new Font("Serif", Font.BOLD, 24));
        pageTitle.setForeground(TEXT_DARK);
        pageTitle.setAlignmentX(LEFT_ALIGNMENT);

        JLabel pageSub = new JLabel("Performance overview for your cafe");
        pageSub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        pageSub.setForeground(TEXT_MUTED);
        pageSub.setAlignmentX(LEFT_ALIGNMENT);

        titleStack.add(pageTitle);
        titleStack.add(Box.createRigidArea(new Dimension(0, 2)));
        titleStack.add(pageSub);

        JPanel filterGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 4));
        filterGroup.setBackground(CANVAS_BG);

        ButtonGroup group = new ButtonGroup();
        DateRange[] ranges = { DateRange.TODAY, DateRange.THIS_WEEK, DateRange.THIS_MONTH };
        for (DateRange r : ranges) {
            JToggleButton tb = filterToggle(r.toString(), r == currentRange);
            group.add(tb);
            tb.addActionListener(e -> {
                currentRange = r;
                refreshContent();
            });
            filterGroup.add(tb);
        }

        row.add(titleStack,  BorderLayout.WEST);
        row.add(filterGroup, BorderLayout.EAST);
        return row;
    }

    // ── Refresh: swap only the content panel ──────────────────────────────────
    private void refreshContent() {
        Container parent = contentPanel.getParent();
        parent.remove(contentPanel);
        contentPanel = buildContentPanel();
        parent.add(contentPanel, BorderLayout.CENTER);
        parent.revalidate();
        parent.repaint();
    }

    // ── Content panel: KPI row + charts row + bottom row via GridBagLayout ────
    private JPanel buildContentPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(CANVAS_BG);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill    = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.gridx   = 0;
        gbc.insets  = new Insets(0, 0, 10, 0);

        // Row 1 — KPI cards (fixed height proportion)
        gbc.gridy   = 0;
        gbc.weighty = 0.16;
        p.add(buildKpiRow(), gbc);

        // Row 2 — Charts
        gbc.gridy   = 1;
        gbc.weighty = 0.46;
        gbc.insets  = new Insets(0, 0, 10, 0);
        p.add(buildChartsRow(), gbc);

        // Row 3 — Bottom tables
        gbc.gridy   = 2;
        gbc.weighty = 0.38;
        gbc.insets  = new Insets(0, 0, 0, 0);
        p.add(buildBottomRow(), gbc);

        return p;
    }

    // ── KPI cards ─────────────────────────────────────────────────────────────
    private JPanel buildKpiRow() {
        JPanel row = new JPanel(new GridLayout(1, 5, 10, 0));
        row.setOpaque(false);

        double totalRevenue  = analytics.getTotalRevenue(currentRange);
        int    totalOrders   = analytics.getTotalOrders(currentRange);
        double avgOrder      = analytics.getAverageOrderValue(currentRange);
        String topItem       = analytics.getTopSellingItemName(currentRange);
        int    topItemUnits  = analytics.getTopSellingItemUnits(currentRange);
        String peakHour      = analytics.getPeakHourLabel(currentRange);

        double revenueChange = analytics.getRevenueChangePercent(currentRange);
        int    ordersChange  = analytics.getOrdersCountChange(currentRange);
        double avgChange     = analytics.getAvgOrderChangePercent(currentRange);

        String rSign = revenueChange >= 0 ? "+" : "";
        String aSign = avgChange     >= 0 ? "+" : "";
        String oSign = ordersChange  >= 0 ? "+" : "";

        row.add(kpiCard("Total Revenue",   String.format("PKR %.0f", totalRevenue),
                rSign + String.format("%.0f%%", revenueChange) + " vs last period", C_TEAL));
        row.add(kpiCard("Total Orders",    String.valueOf(totalOrders),
                oSign + ordersChange + " vs last period", C_AMBER));
        row.add(kpiCard("Avg Order Value", String.format("PKR %.0f", avgOrder),
                aSign + String.format("%.0f%%", avgChange) + " vs last period", C_ROSE));
        row.add(kpiCard("Top Item",        topItem,
                topItemUnits + " units sold", C_SAGE));
        row.add(kpiCard("Peak Hour",       peakHour,
                "busiest window", C_TEAL));
        return row;
    }

    private JPanel kpiCard(String title, String value, String sub, Color accent) {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.setColor(accent);
                g2.fill(new RoundRectangle2D.Float(0, 0, 4, getHeight(), 4, 4));
                g2.dispose();
            }
        };
        p.setLayout(new GridBagLayout());
        p.setOpaque(false);

        JPanel inner = new JPanel();
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setOpaque(false);
        inner.setBorder(new EmptyBorder(10, 16, 10, 10));

        JLabel t = new JLabel(title.toUpperCase());
        t.setFont(new Font("SansSerif", Font.BOLD, 9));
        t.setForeground(TEXT_MUTED);
        t.setAlignmentX(LEFT_ALIGNMENT);

        JLabel v = new JLabel(value);
        v.setFont(new Font("SansSerif", Font.BOLD, 18));
        v.setForeground(TEXT_DARK);
        v.setAlignmentX(LEFT_ALIGNMENT);

        JLabel s = new JLabel(sub);
        s.setFont(new Font("SansSerif", Font.PLAIN, 10));
        s.setForeground(TEXT_MUTED);
        s.setAlignmentX(LEFT_ALIGNMENT);

        inner.add(t);
        inner.add(Box.createRigidArea(new Dimension(0, 5)));
        inner.add(v);
        inner.add(Box.createRigidArea(new Dimension(0, 2)));
        inner.add(s);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill    = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        p.add(inner, gbc);
        return p;
    }

    // ── Charts row ────────────────────────────────────────────────────────────
    private JPanel buildChartsRow() {
        JPanel row = new JPanel(new GridLayout(1, 2, 12, 0));
        row.setOpaque(false);

        int[]    revenueData = analytics.getRevenueByDay(currentRange);
        String[] revLabels   = analytics.getRevenueByDayLabels(currentRange);
        row.add(buildBarChart("Revenue by Day (PKR)", revenueData, revLabels, C_TEAL));

        int[]    hourData    = analytics.getOrdersByHour(currentRange);
        String[] hourLabels  = analytics.getOrdersByHourLabels();
        row.add(buildBarChart("Orders by Hour", hourData, hourLabels, C_AMBER));

        return row;
    }

    private JPanel buildBarChart(String title, int[] data, String[] labels, Color barColor) {
        JPanel wrapper = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.dispose();
            }
        };
        wrapper.setOpaque(false);

        JPanel titleRow = new JPanel();
        titleRow.setLayout(new BoxLayout(titleRow, BoxLayout.X_AXIS));
        titleRow.setOpaque(false);
        titleRow.setBorder(new EmptyBorder(12, 14, 6, 14));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        titleLbl.setForeground(TEXT_DARK);
        titleRow.add(titleLbl);
        titleRow.add(Box.createHorizontalGlue());
        wrapper.add(titleRow, BorderLayout.NORTH);

        JPanel chart = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth(), h = getHeight();
                int padL = 38, padR = 10, padT = 6, padB = 24;
                int chartW = w - padL - padR;
                int chartH = h - padT - padB;

                int max = 0;
                for (int d : data) if (d > max) max = d;
                if (max == 0) max = 1;

                g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
                g2.setColor(BORDER_COLOR);
                for (int i = 0; i <= 4; i++) {
                    int y = padT + chartH - (i * chartH / 4);
                    g2.drawLine(padL, y, padL + chartW, y);
                    g2.setColor(TEXT_MUTED);
                    g2.drawString(String.valueOf(max * i / 4), 2, y + 4);
                    g2.setColor(BORDER_COLOR);
                }

                int barCount = data.length;
                int gap      = Math.max(2, 4 - barCount / 8);
                int barW     = barCount != 0 ? Math.max(1, (chartW - gap * (barCount + 1)) / barCount) : 1;

                g2.setFont(new Font("SansSerif", Font.PLAIN, 8));
                for (int i = 0; i < barCount; i++) {
                    int barH = (int) ((double) data[i] / max * chartH);
                    int x    = padL + gap + i * (barW + gap);
                    int y    = padT + chartH - barH;

                    g2.setColor(new Color(barColor.getRed(), barColor.getGreen(), barColor.getBlue(), 40));
                    g2.fillRoundRect(x, padT, barW, chartH, 4, 4);
                    g2.setColor(barColor);
                    g2.fillRoundRect(x, y, barW, barH, 4, 4);

                    g2.setColor(TEXT_MUTED);
                    if (i < labels.length) {
                        FontMetrics fm = g2.getFontMetrics();
                        int lx = x + (barW - fm.stringWidth(labels[i])) / 2;
                        g2.drawString(labels[i], lx, padT + chartH + 14);
                    }
                }
            }
        };
        chart.setOpaque(false);
        chart.setBorder(new EmptyBorder(0, 6, 8, 8));
        wrapper.add(chart, BorderLayout.CENTER);
        return wrapper;
    }

    // ── Bottom row ────────────────────────────────────────────────────────────
    private JPanel buildBottomRow() {
        JPanel row = new JPanel(new GridLayout(1, 2, 12, 0));
        row.setOpaque(false);
        row.add(buildTopItemsTable());
        row.add(buildOrderStatusPanel());
        return row;
    }

    // ── Top items table ───────────────────────────────────────────────────────
    private JPanel buildTopItemsTable() {
        String[][] rawItems = analytics.getTopSellingItems(currentRange, 4);
        Object[][] rows = new Object[rawItems.length][5];
        for (int i = 0; i < rawItems.length; i++) {
            rows[i][0] = String.valueOf(i + 1);
            rows[i][1] = rawItems[i][0];
            rows[i][2] = rawItems[i][1];
            rows[i][3] = rawItems[i][2];
            rows[i][4] = rawItems[i][3];
        }
        return buildTable("Top Selling Items", rows,
                new String[]{ "#", "Item", "Category", "Units", "Revenue" });
    }

    // ── Order status breakdown ────────────────────────────────────────────────
    private JPanel buildOrderStatusPanel() {
        JPanel wrapper = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.dispose();
            }
        };
        wrapper.setOpaque(false);

        JPanel titleRow = new JPanel();
        titleRow.setLayout(new BoxLayout(titleRow, BoxLayout.X_AXIS));
        titleRow.setOpaque(false);
        titleRow.setBorder(new EmptyBorder(12, 14, 8, 14));
        JLabel titleLbl = new JLabel("Order Status Breakdown");
        titleLbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        titleLbl.setForeground(TEXT_DARK);
        titleRow.add(titleLbl);
        titleRow.add(Box.createHorizontalGlue());
        wrapper.add(titleRow, BorderLayout.NORTH);

        String[] statuses = { "PENDING", "PREPARING", "OUT_FOR_DELIVERY", "COMPLETED", "CANCELLED" };
        int[]    counts   = analytics.getOrderCountByStatus(currentRange, statuses);
        Color[]  colors   = { C_SAGE, C_AMBER, new Color(80, 110, 160), C_TEAL, C_JADE };

        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(0, 14, 10, 14));

        int total = 0;
        for (int c : counts) total += c;
        if (total == 0) total = 1;

        for (int i = 0; i < statuses.length; i++) {
            body.add(buildStatusBar(statuses[i], counts[i], total, colors[i]));
            if (i < statuses.length - 1)
                body.add(Box.createRigidArea(new Dimension(0, 8)));
        }

        wrapper.add(body, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel buildStatusBar(String label, int count, int total, Color color) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setAlignmentX(LEFT_ALIGNMENT);

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);

        JLabel lbl = new JLabel(label.replace("_", " "));
        lbl.setFont(new Font("SansSerif", Font.BOLD, 10));
        lbl.setForeground(TEXT_DARK);

        JLabel cnt = new JLabel(count + "  (" + (count * 100 / total) + "%)");
        cnt.setFont(new Font("SansSerif", Font.PLAIN, 10));
        cnt.setForeground(TEXT_MUTED);

        topRow.add(lbl, BorderLayout.WEST);
        topRow.add(cnt, BorderLayout.EAST);
        p.add(topRow);
        p.add(Box.createRigidArea(new Dimension(0, 4)));

        final int fCount = count, fTotal = total;
        JPanel bar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BORDER_COLOR);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                int filled = (int) ((double) fCount / fTotal * getWidth());
                g2.setColor(color);
                g2.fillRoundRect(0, 0, filled, getHeight(), 6, 6);
            }
        };
        bar.setPreferredSize(new Dimension(0, 7));
        bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 7));
        bar.setAlignmentX(LEFT_ALIGNMENT);
        p.add(bar);
        return p;
    }

    // ── Data table ────────────────────────────────────────────────────────────
    private JPanel buildTable(String heading, Object[][] rows, String[] cols) {
        JPanel outer = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.dispose();
            }
        };
        outer.setOpaque(false);

        JPanel headingRow = new JPanel();
        headingRow.setLayout(new BoxLayout(headingRow, BoxLayout.X_AXIS));
        headingRow.setOpaque(false);
        headingRow.setBorder(new EmptyBorder(12, 14, 0, 14));
        JLabel headLbl = new JLabel(heading);
        headLbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        headLbl.setForeground(TEXT_DARK);
        headingRow.add(headLbl);
        headingRow.add(Box.createHorizontalGlue());
        outer.add(headingRow, BorderLayout.NORTH);

        JPanel header = new JPanel(new GridLayout(1, cols.length));
        header.setBackground(new Color(245, 242, 238));
        header.setBorder(new EmptyBorder(6, 14, 6, 14));
        for (String c : cols) {
            JLabel lbl = new JLabel(c.toUpperCase());
            lbl.setFont(new Font("SansSerif", Font.BOLD, 9));
            lbl.setForeground(TEXT_MUTED);
            header.add(lbl);
        }

        JPanel dataPanel = new JPanel();
        dataPanel.setLayout(new BoxLayout(dataPanel, BoxLayout.Y_AXIS));
        dataPanel.setOpaque(false);

        for (int r = 0; r < rows.length; r++) {
            JPanel rowPanel = new JPanel(new GridLayout(1, cols.length));
            rowPanel.setOpaque(false);
            rowPanel.setBorder(new EmptyBorder(7, 14, 7, 14));
            rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
            for (int c = 0; c < cols.length; c++) {
                JLabel cell = new JLabel(rows[r][c].toString());
                cell.setFont(new Font("SansSerif", c <= 1 ? Font.BOLD : Font.PLAIN, 11));
                cell.setForeground(c <= 1 ? TEXT_DARK : TEXT_MUTED);
                rowPanel.add(cell);
            }
            dataPanel.add(rowPanel);
            if (r < rows.length - 1) {
                JPanel sep = new JPanel();
                sep.setBackground(BORDER_COLOR);
                sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
                dataPanel.add(sep);
            }
        }

        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);
        content.add(header,    BorderLayout.NORTH);
        content.add(dataPanel, BorderLayout.CENTER);
        outer.add(content, BorderLayout.CENTER);
        return outer;
    }

    // ── Filter toggle ─────────────────────────────────────────────────────────
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
        btn.setPreferredSize(new Dimension(Math.max(80, w), 28));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ── Sidebar ───────────────────────────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(170, 0));
        sidebar.setMaximumSize(new Dimension(220, Integer.MAX_VALUE));

        JPanel logoArea = new JPanel();
        logoArea.setLayout(new BoxLayout(logoArea, BoxLayout.Y_AXIS));
        logoArea.setBackground(SIDEBAR_BG);
        logoArea.setAlignmentX(LEFT_ALIGNMENT);
        logoArea.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));
        logoArea.setMinimumSize(new Dimension(0, 72));
        logoArea.setBorder(BorderFactory.createEmptyBorder(20, 20, 14, 20));

        JLabel logoText = new JLabel("CafeSync");
        logoText.setFont(new Font("Serif", Font.BOLD, 20));
        logoText.setForeground(Color.WHITE);
        logoText.setAlignmentX(LEFT_ALIGNMENT);

        JLabel logoSub = new JLabel("Analytics");
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
        sidebar.add(Box.createRigidArea(new Dimension(0, 14)));

        sidebar.add(sidebarSectionLabel("NAVIGATION"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 4)));
        sidebar.add(sidebarNavItemClickable("Menu Items", "▤", false, e -> openMenuManagementScreen()));
        sidebar.add(sidebarNavItem("Analytics", "↗", true));

        sidebar.add(Box.createRigidArea(new Dimension(0, 18)));

        sidebar.add(sidebarSectionLabel("SESSION"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 4)));

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
        sidebar.add(sidebarNavItemClickable("← Back to Menu", "⎋", false, e -> openMenuManagementScreen()));
        sidebar.add(Box.createVerticalGlue());

        JPanel versionRow = new JPanel();
        versionRow.setLayout(new BoxLayout(versionRow, BoxLayout.X_AXIS));
        versionRow.setBackground(SIDEBAR_BG);
        versionRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        versionRow.setAlignmentX(LEFT_ALIGNMENT);
        versionRow.setBorder(BorderFactory.createEmptyBorder(0, 20, 12, 20));
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

    private void openMenuManagementScreen() {
        new MenuManagement(manager).setVisible(true);
        setVisible(false);
    }
}