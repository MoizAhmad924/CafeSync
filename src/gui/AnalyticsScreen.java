package gui;

import models.User;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AnalyticsScreen extends JFrame {
    private static final Color BROWN = new Color(44, 30, 22);
    private static final Color BEIGE = new Color(245, 242, 235);
    private static final Color CARD_BG = new Color(255, 255, 255);
    private static final Color TEXT_DARK = new Color(60, 45, 35);
    private static final Color TEXT_MUTED = new Color(130, 115, 105);
    private static final Color DIVIDER = new Color(230, 225, 218);
    private static final Color TEAL = new Color(52, 152, 152);

    private final User manager;

    public AnalyticsScreen(User manager) {
        this.manager = manager;
        setTitle("CafeSync — Analytics  |  " + manager.getName());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1100, 680);
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
        body.setBorder(new EmptyBorder(14, 20, 14, 20));

        // cards
        body.add(sectionLabel("Overview Of This Week:"));
        body.add(Box.createRigidArea(new Dimension(0, 8)));
        body.add(buildCardRow());
        body.add(Box.createRigidArea(new Dimension(0, 14)));

        // charts(empty for now)
        body.add(sectionLabel("Charts:"));
        body.add(buildChartsRow());
        body.add(Box.createRigidArea(new Dimension(0, 14)));

        // table
        body.add(sectionLabel("Top Selling Items This Week:"));
        body.add(Box.createRigidArea(new Dimension(0, 8)));
        body.add(buildTopItemsTable());
        body.add(Box.createRigidArea(new Dimension(0, 8)));

        return body;
    }

    private JPanel buildCardRow() {
        JPanel row = new JPanel(new GridLayout(1, 4, 16, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.add(card("Top Selling Item", "Beef Burger", "× 142 units sold", TEAL));
        row.add(card("Today's Revenue", "$910.00", "revenue earned today", TEAL));
        row.add(card("Orders Today", "42", "orders placed today", TEAL));
        row.add(card("Highest Revenue", "$910.00", "on saturday", TEAL));
        return row;
    }

    private JPanel card(String title, String value, String sub, Color accent) {
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBackground(CARD_BG);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 5, 0, 0, accent),
                new EmptyBorder(10, 16, 10, 12)));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("SansSerif", Font.BOLD, 11));
        titleLbl.setForeground(TEXT_MUTED);
        titleLbl.setAlignmentX(LEFT_ALIGNMENT);

        JLabel valLbl = new JLabel(value);
        valLbl.setFont(new Font("SansSerif", Font.BOLD, 22));
        valLbl.setForeground(TEXT_DARK);
        valLbl.setAlignmentX(LEFT_ALIGNMENT);

        JLabel subLbl = new JLabel(sub);
        subLbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
        subLbl.setForeground(TEXT_MUTED);
        subLbl.setAlignmentX(LEFT_ALIGNMENT);

        cardPanel.add(titleLbl);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        cardPanel.add(valLbl);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        cardPanel.add(subLbl);
        return cardPanel;
    }

    // reserved area for charts
    private JPanel buildChartsRow() {
        JPanel row = new JPanel();
        row.setOpaque(false);
        row.setMinimumSize(new Dimension(0, 200));
        row.setPreferredSize(new Dimension(0, 200));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        row.setAlignmentX(LEFT_ALIGNMENT);
        return row;
    }

    private JPanel buildTopItemsTable() {
        Object[][] rows = {
                { "1", "Beef Burger", "Main Course", "142", "$2,009.00" },
                { "2", "Cappuccino", "Beverage", "128", "$608.00" },
                { "3", "Avocado Toast", "Appetizer", "97", "$824.50" }
        };
        String[] cols = { "#", "Item", "Category", "Units Sold", "Revenue" };

        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(CARD_BG);
        outer.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        outer.setAlignmentX(LEFT_ALIGNMENT);
        outer.setBorder(BorderFactory.createLineBorder(DIVIDER, 1));

        JPanel header = new JPanel(new GridLayout(1, 5));
        header.setBackground(BROWN);
        header.setBorder(new EmptyBorder(10, 18, 10, 18));
        for (String c : cols) {
            JLabel lbl = new JLabel(c);
            lbl.setFont(new Font("SansSerif", Font.BOLD, 11));
            lbl.setForeground(new Color(220, 200, 180));
            header.add(lbl);
        }
        outer.add(header, BorderLayout.NORTH);

        JPanel dataPanel = new JPanel();
        dataPanel.setLayout(new BoxLayout(dataPanel, BoxLayout.Y_AXIS));
        dataPanel.setBackground(CARD_BG);
        for (int r = 0; r < rows.length; r++) {
            JPanel rowPanel = new JPanel(new GridLayout(1, 5));
            rowPanel.setBackground(CARD_BG);
            rowPanel.setBorder(new EmptyBorder(10, 18, 10, 18));
            rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
            for (int c = 0; c < cols.length; c++) {
                JLabel cell = new JLabel(rows[r][c].toString());
                if (c == 0) {
                    cell.setFont(new Font("SansSerif", Font.BOLD, 13));
                    cell.setForeground(TEXT_DARK);
                } else if (c == 1) {
                    cell.setFont(new Font("SansSerif", Font.BOLD, 13));
                    cell.setForeground(TEXT_DARK);
                } else if (c == 4) {
                    cell.setFont(new Font("SansSerif", Font.PLAIN, 13));
                    cell.setForeground(TEXT_MUTED);
                } else {
                    cell.setFont(new Font("SansSerif", Font.PLAIN, 13));
                    cell.setForeground(TEXT_MUTED);
                }
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
        outer.add(dataPanel, BorderLayout.CENTER);
        return outer;
    }

    // helpers
    private JLabel sectionLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 15));
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
}
