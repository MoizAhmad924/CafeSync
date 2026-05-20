package gui;

import analytics.AnalyticsManager;
import enums.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import models.User;

public class AnalyticsScreen extends JFrame {

    private static final Color BROWN      = new Color(44, 30, 22);
    private static final Color BEIGE      = new Color(245, 242, 235);
    private static final Color CARD_BG    = new Color(255, 255, 255);
    private static final Color TEXT_DARK  = new Color(60, 45, 35);
    private static final Color TEXT_MUTED = new Color(130, 115, 105);
    private static final Color DIVIDER    = new Color(230, 225, 218);
    private static final Color TEAL       = new Color(52, 152, 152);
    private static final Color AMBER      = new Color(210, 140, 50);
    private static final Color ROSE       = new Color(185, 70, 70);
    private static final Color SAGE       = new Color(85, 140, 100);
    private static final Color JADE       = new Color(20, 120, 100);

    private final User manager;
    private final AnalyticsManager analytics;
    private DateRange currentRange = DateRange.THIS_WEEK;

    public AnalyticsScreen(User manager) {
        this.manager = manager;
        this.analytics = new AnalyticsManager();
        setTitle("CafeSync — Analytics  |  " + manager.getName());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1180, 780);
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

        JLabel logo = new JLabel("CafeSync  ·  Analytics Dashboard");
        logo.setFont(new Font("Serif", Font.BOLD, 22));
        logo.setForeground(Color.WHITE);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 12));
        right.setOpaque(false);

        JLabel managerLabel = new JLabel("Manager: " + manager.getName());
        managerLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        managerLabel.setForeground(new Color(200, 180, 160));

        JButton backBtn = makeHeaderButton("← Back to Menu", TEAL);
        backBtn.addActionListener(e -> dispose());

        right.add(managerLabel);
        right.add(backBtn);
        bar.add(logo, BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    private JPanel buildBody() {
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(BEIGE);
        body.setBorder(new EmptyBorder(16, 22, 16, 22));

        body.add(buildFilterBar(body));
        body.add(Box.createRigidArea(new Dimension(0, 14)));

        body.add(sectionLabel("Overview"));
        body.add(Box.createRigidArea(new Dimension(0, 8)));
        body.add(buildKpiRow());
        body.add(Box.createRigidArea(new Dimension(0, 16)));

        body.add(buildChartsRow());
        body.add(Box.createRigidArea(new Dimension(0, 16)));

        JPanel bottomRow = new JPanel(new GridLayout(1, 2, 16, 0));
        bottomRow.setOpaque(false);
        bottomRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));
        bottomRow.setAlignmentX(LEFT_ALIGNMENT);
        bottomRow.add(buildTopItemsTable());
        bottomRow.add(buildOrderStatusPanel());
        body.add(bottomRow);

        return body;
    }

    private JPanel buildFilterBar(JPanel body) {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);
        bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        bar.setAlignmentX(LEFT_ALIGNMENT);

        JLabel title = new JLabel("Date Range:");
        title.setFont(new Font("SansSerif", Font.BOLD, 13));
        title.setForeground(TEXT_DARK);

        JPanel btnGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        btnGroup.setOpaque(false);

        ButtonGroup group = new ButtonGroup();
        DateRange[] ranges = { DateRange.TODAY, DateRange.THIS_WEEK, DateRange.THIS_MONTH };
        for (DateRange r : ranges) {
            JToggleButton tb = makeRangeButton(r.toString());
            if (r == currentRange) tb.setSelected(true);
            group.add(tb);
            tb.addActionListener(e -> {
                currentRange = r;
                refreshBody(body);
            });
            btnGroup.add(tb);
        }

        bar.add(title, BorderLayout.WEST);
        bar.add(btnGroup, BorderLayout.CENTER);
        return bar;
    }

    private void refreshBody(JPanel body) {
        Component[] comps = body.getComponents();
        for (int i = comps.length - 1; i >= 2; i--) {
            body.remove(comps[i]);
        }

        body.add(sectionLabel("Overview"));
        body.add(Box.createRigidArea(new Dimension(0, 8)));
        body.add(buildKpiRow());
        body.add(Box.createRigidArea(new Dimension(0, 16)));
        body.add(buildChartsRow());
        body.add(Box.createRigidArea(new Dimension(0, 16)));

        JPanel bottomRow = new JPanel(new GridLayout(1, 2, 16, 0));
        bottomRow.setOpaque(false);
        bottomRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));
        bottomRow.setAlignmentX(LEFT_ALIGNMENT);
        bottomRow.add(buildTopItemsTable());
        bottomRow.add(buildOrderStatusPanel());
        body.add(bottomRow);

        body.revalidate();
        body.repaint();
    }

    private JPanel buildKpiRow() {
        JPanel row = new JPanel(new GridLayout(1, 5, 12, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 88));
        row.setAlignmentX(LEFT_ALIGNMENT);

        double totalRevenue = analytics.getTotalRevenue(currentRange);
        int totalOrders     = analytics.getTotalOrders(currentRange);
        double avgOrder     = analytics.getAverageOrderValue(currentRange);
        String topItem      = analytics.getTopSellingItemName(currentRange);
        int topItemUnits    = analytics.getTopSellingItemUnits(currentRange);
        String peakHour     = analytics.getPeakHourLabel(currentRange);

        double revenueChange = analytics.getRevenueChangePercent(currentRange);
        int ordersChange     = analytics.getOrdersCountChange(currentRange);
        double avgChange     = analytics.getAvgOrderChangePercent(currentRange);

        String revenueSign = revenueChange >= 0 ? "+" : "";
        String avgSign     = avgChange >= 0 ? "+" : "";
        String ordSign     = ordersChange >= 0 ? "+" : "";

        row.add(kpiCard("Total Revenue",   String.format("$%.2f", totalRevenue),
                revenueSign + String.format("%.0f%%", revenueChange) + " vs last period", TEAL));
        row.add(kpiCard("Total Orders",    String.valueOf(totalOrders),
                ordSign + ordersChange + " vs last period", AMBER));
        row.add(kpiCard("Avg Order Value", String.format("$%.2f", avgOrder),
                avgSign + String.format("%.0f%%", avgChange) + " vs last period", ROSE));
        row.add(kpiCard("Top Item",        topItem,
                topItemUnits + " units sold", SAGE));
        row.add(kpiCard("Peak Hour",       peakHour,
                "busiest window", TEAL));
        return row;
    }

    private JPanel kpiCard(String title, String value, String sub, Color accent) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(CARD_BG);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, accent),
                new EmptyBorder(10, 14, 10, 10)));

        JLabel t = new JLabel(title);
        t.setFont(new Font("SansSerif", Font.BOLD, 10));
        t.setForeground(TEXT_MUTED);
        t.setAlignmentX(LEFT_ALIGNMENT);

        JLabel v = new JLabel(value);
        v.setFont(new Font("SansSerif", Font.BOLD, 20));
        v.setForeground(TEXT_DARK);
        v.setAlignmentX(LEFT_ALIGNMENT);

        JLabel s = new JLabel(sub);
        s.setFont(new Font("SansSerif", Font.PLAIN, 10));
        s.setForeground(TEXT_MUTED);
        s.setAlignmentX(LEFT_ALIGNMENT);

        p.add(t);
        p.add(Box.createRigidArea(new Dimension(0, 6)));
        p.add(v);
        p.add(Box.createRigidArea(new Dimension(0, 3)));
        p.add(s);
        return p;
    }

    private JPanel buildChartsRow() {
        JPanel row = new JPanel(new GridLayout(1, 2, 16, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 210));
        row.setAlignmentX(LEFT_ALIGNMENT);

        int[] revenueData   = analytics.getRevenueByDay(currentRange);
        String[] revLabels  = analytics.getRevenueByDayLabels(currentRange);
        row.add(buildBarChart("Revenue by Day ($)", revenueData, revLabels, TEAL));

        int[] hourData      = analytics.getOrdersByHour(currentRange);
        String[] hourLabels = analytics.getOrdersByHourLabels();
        row.add(buildBarChart("Orders by Hour", hourData, hourLabels, AMBER));

        return row;
    }

    private JPanel buildBarChart(String title, int[] data, String[] labels, Color barColor) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(CARD_BG);
        wrapper.setBorder(BorderFactory.createLineBorder(DIVIDER, 1));

        JLabel titleLbl = new JLabel("  " + title);
        titleLbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        titleLbl.setForeground(TEXT_DARK);
        titleLbl.setPreferredSize(new Dimension(0, 30));
        titleLbl.setBorder(new EmptyBorder(0, 12, 0, 0));
        wrapper.add(titleLbl, BorderLayout.NORTH);

        JPanel chart = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                int padL = 36, padR = 10, padT = 10, padB = 28;
                int chartW = w - padL - padR;
                int chartH = h - padT - padB;

                int max = 0;
                for (int d : data) if (d > max) max = d;
                if (max == 0) max = 1;

                g2.setColor(DIVIDER);
                for (int i = 0; i <= 4; i++) {
                    int y = padT + chartH - (i * chartH / 4);
                    g2.drawLine(padL, y, padL + chartW, y);
                    g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
                    g2.setColor(TEXT_MUTED);
                    String lbl = String.valueOf(max * i / 4);
                    g2.drawString(lbl, 2, y + 4);
                    g2.setColor(DIVIDER);
                }

                int barCount = data.length;
                int gap = 4;
                
                int barW = 1;
                barW = barCount != 0? Math.max(1, (chartW - gap * (barCount + 1)) / barCount):1;

                g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
                for (int i = 0; i < barCount; i++) {
                    int barH = (int) ((double) data[i] / max * chartH);
                    int x = padL + gap + i * (barW + gap);
                    int y = padT + chartH - barH;

                    g2.setColor(new Color(barColor.getRed(), barColor.getGreen(), barColor.getBlue(), 60));
                    g2.fillRoundRect(x, padT, barW, chartH, 4, 4);

                    g2.setColor(barColor);
                    g2.fillRoundRect(x, y, barW, barH, 4, 4);

                    g2.setColor(TEXT_MUTED);
                    FontMetrics fm = g2.getFontMetrics();
                    if (i < labels.length) {
                        int lx = x + (barW - fm.stringWidth(labels[i])) / 2;
                        g2.drawString(labels[i], lx, padT + chartH + 16);
                    }
                }
            }
        };
        chart.setBackground(CARD_BG);
        wrapper.add(chart, BorderLayout.CENTER);
        return wrapper;
    }

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
        String[] cols = { "#", "Item", "Category", "Units", "Revenue" };
        return buildTable("Top Selling Items", rows, cols);
    }

    private JPanel buildOrderStatusPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(CARD_BG);
        wrapper.setBorder(BorderFactory.createLineBorder(DIVIDER, 1));

        JLabel title = new JLabel("  Order Status Breakdown");
        title.setFont(new Font("SansSerif", Font.BOLD, 12));
        title.setForeground(TEXT_DARK);
        title.setPreferredSize(new Dimension(0, 30));
        title.setBorder(new EmptyBorder(0, 12, 0, 0));
        wrapper.add(title, BorderLayout.NORTH);

        String[] statuses = {"PENDING", "PREPARING", "OUT_FOR_DELIVERY", "COMPLETED", "CANCELLED"};
        int[] counts      = analytics.getOrderCountByStatus(currentRange, statuses);
        Color[] colors    = { SAGE, AMBER, ROSE, TEAL, JADE};

        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(CARD_BG);
        body.setBorder(new EmptyBorder(10, 16, 10, 16));

        int total = 0;
        for (int c : counts) total += c;
        if (total == 0) total = 1;

        for (int i = 0; i < statuses.length; i++) {
            body.add(buildStatusBar(statuses[i], counts[i], total, colors[i]));
            body.add(Box.createRigidArea(new Dimension(0, 10)));
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
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 11));
        lbl.setForeground(TEXT_DARK);
        JLabel cnt = new JLabel(count + " orders  (" + (count * 100 / total) + "%)");
        cnt.setFont(new Font("SansSerif", Font.PLAIN, 11));
        cnt.setForeground(TEXT_MUTED);
        topRow.add(lbl, BorderLayout.WEST);
        topRow.add(cnt, BorderLayout.EAST);
        p.add(topRow);
        p.add(Box.createRigidArea(new Dimension(0, 4)));

        final int fCount = count, fTotal = total;
        final Color fColor = color;
        JPanel bar = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(DIVIDER);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                int filled = (int) ((double) fCount / fTotal * getWidth());
                g2.setColor(fColor);
                g2.fillRoundRect(0, 0, filled, getHeight(), 6, 6);
            }
        };
        bar.setPreferredSize(new Dimension(0, 10));
        bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 10));
        bar.setAlignmentX(LEFT_ALIGNMENT);
        p.add(bar);
        return p;
    }

    private JPanel buildTable(String heading, Object[][] rows, String[] cols) {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(CARD_BG);
        outer.setBorder(BorderFactory.createLineBorder(DIVIDER, 1));

        JLabel titleLbl = new JLabel("  " + heading);
        titleLbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        titleLbl.setForeground(TEXT_DARK);
        titleLbl.setPreferredSize(new Dimension(0, 30));
        titleLbl.setBorder(new EmptyBorder(0, 12, 0, 0));
        outer.add(titleLbl, BorderLayout.NORTH);

        JPanel header = new JPanel(new GridLayout(1, cols.length));
        header.setBackground(BROWN);
        header.setBorder(new EmptyBorder(7, 14, 7, 14));
        for (String c : cols) {
            JLabel lbl = new JLabel(c);
            lbl.setFont(new Font("SansSerif", Font.BOLD, 10));
            lbl.setForeground(new Color(220, 200, 180));
            header.add(lbl);
        }

        JPanel dataPanel = new JPanel();
        dataPanel.setLayout(new BoxLayout(dataPanel, BoxLayout.Y_AXIS));
        dataPanel.setBackground(CARD_BG);

        for (int r = 0; r < rows.length; r++) {
            JPanel rowPanel = new JPanel(new GridLayout(1, cols.length));
            rowPanel.setBackground(CARD_BG);
            rowPanel.setBorder(new EmptyBorder(7, 14, 7, 14));
            rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
            for (int c = 0; c < cols.length; c++) {
                JLabel cell = new JLabel(rows[r][c].toString());
                cell.setFont(new Font("SansSerif", c <= 1 ? Font.BOLD : Font.PLAIN, 12));
                cell.setForeground(c <= 1 ? TEXT_DARK : TEXT_MUTED);
                rowPanel.add(cell);
            }
            dataPanel.add(rowPanel);
            if (r < rows.length - 1) {
                JSeparator sep = new JSeparator();
                sep.setForeground(DIVIDER);
                sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
                dataPanel.add(sep);
            }
        }

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(CARD_BG);
        content.add(header, BorderLayout.NORTH);
        content.add(dataPanel, BorderLayout.CENTER);
        outer.add(content, BorderLayout.CENTER);
        return outer;
    }

    private JLabel sectionLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 14));
        lbl.setForeground(TEXT_DARK);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        return lbl;
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

    private JToggleButton makeRangeButton(String text) {
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
        btn.setPreferredSize(new Dimension(88, 28));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}