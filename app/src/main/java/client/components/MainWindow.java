package client.components;

import javax.swing.*;
import javax.swing.border.*;

import client.ActionCommand;
import client.AppContext;
import client.AppLocale;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.util.*;

final public class MainWindow extends JFrame implements AppLocale.Localizable {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentCards = new JPanel(cardLayout);

    private AppButtons.TabToggleButton tableTab;
    private AppButtons.TabToggleButton graphTab;

    private HintTextField searchField;
    private JLabel userLabel;
    private AppButtons.RoundLocaleButton localeButton;

    private GraphPanel graphCanvas;
    private TablePanel tablePanel;

    private final AppContext context;

    private final Map<String, String> uiText = new HashMap<>();
    private final List<AppButtons.RoundedButton> actionButtons = new ArrayList<>();

    public MainWindow(AppContext context) {
        super("StudyBuddy");
        this.context = context;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(980, 620));
        setSize(980, 620);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());
        add(buildTopBar(), BorderLayout.NORTH);
        add(buildContentArea(), BorderLayout.CENTER);
        add(buildActionsSidebar(), BorderLayout.EAST);

        applyLocale(context.getLocale());

    }

    private JComponent buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        bar.setBackground(new Color(246, 248, 252));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setOpaque(false);

        tableTab = new AppButtons.TabToggleButton(uiText.get("tab_table"));
        graphTab = new AppButtons.TabToggleButton(uiText.get("tab_graph"));

        ButtonGroup group = new ButtonGroup();
        group.add(tableTab);
        group.add(graphTab);
        tableTab.setSelected(true);

        tableTab.addActionListener(e -> {
            cardLayout.show(contentCards, "TABLE");
        });
        graphTab.addActionListener(e -> {
            cardLayout.show(contentCards, "GRAPH");
        });

        left.add(tableTab);
        left.add(graphTab);

        JPanel center = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        center.setOpaque(false);

        searchField = new HintTextField(22);
        searchField.setPreferredSize(new Dimension(230, 32));
        searchField.addActionListener(e -> {
            context.setSearchQuery(searchField.getText());
            context.notifyUpdate();
        });
        searchField.setBorder(new CompoundBorder(
                new LineBorder(new Color(200, 208, 220), 1, true),
                new EmptyBorder(6, 10, 6, 10)));

        center.add(searchField);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);

        localeButton = new AppButtons.RoundLocaleButton();
        localeButton.addActionListener(e -> AppLocale.cycleLocale(this, context.getLocale()));
        right.add(localeButton);

        userLabel = new JLabel(context.getUsername());
        userLabel.setBorder(new CompoundBorder(
                new LineBorder(new Color(218, 223, 232), 1, true),
                new EmptyBorder(6, 12, 6, 12)));
        userLabel.setOpaque(true);
        userLabel.setBackground(Color.WHITE);
        right.add(userLabel);

        JButton logout = new JButton("↪");
        styleSmallRoundButton(logout);
        logout.setToolTipText("Exit");
        right.add(logout);

        bar.add(left, BorderLayout.WEST);
        bar.add(center, BorderLayout.CENTER);
        bar.add(right, BorderLayout.EAST);

        return bar;
    }

    private JComponent buildContentArea() {
        contentCards.setBackground(new Color(246, 248, 252));
        contentCards.add(buildTableTab(), "TABLE");
        contentCards.add(buildGraphTab(), "GRAPH");
        return contentCards;
    }

    private JComponent buildTableTab() {
        tablePanel = new TablePanel(context);

        SwingUtilities.invokeLater(() -> {
            try {
                tablePanel.refreshFromServer();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        this,
                        "Failed to load data from server: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        return tablePanel;
    }

    private JComponent buildGraphTab() {
        this.graphCanvas = new GraphPanel(context);

        JPanel root = new JPanel(new BorderLayout(10, 0));
        root.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        root.setBackground(new Color(246, 248, 252));

        JScrollPane graphScroll = new JScrollPane(graphCanvas);
        graphScroll.setBorder(new LineBorder(new Color(214, 221, 233), 1, true));
        graphScroll.getViewport().setBackground(Color.WHITE);
        graphScroll.getHorizontalScrollBar().setUnitIncrement(16);
        graphScroll.getVerticalScrollBar().setUnitIncrement(16);

        root.add(graphScroll, BorderLayout.CENTER);

        return root;
    }

    private JComponent buildActionsSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(260, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        sidebar.setBackground(new Color(246, 248, 252));

        for (ActionCommand command : ActionCommand.values()) {
            sidebar.add(actionButton(command));
            sidebar.add(Box.createVerticalStrut(8));
        }

        sidebar.add(Box.createVerticalGlue());
        return sidebar;
    }

    private JComponent actionButton(ActionCommand command) {
        AppButtons.RoundedButton button = new AppButtons.RoundedButton();
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setPreferredSize(new Dimension(240, 42));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        button.addActionListener(e -> command.getHandler(context).run());
        button.setKeySupplier(() -> uiText.getOrDefault(command.getCommand(), command.getCommand()));
        actionButtons.add(button);
        return button;
    }

    private void styleSmallRoundButton(AbstractButton button) {
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setPreferredSize(new Dimension(32, 32));
        button.setMinimumSize(new Dimension(32, 32));
        button.setMaximumSize(new Dimension(32, 32));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFont(button.getFont().deriveFont(Font.BOLD, 14f));
    }

    @Override
    public void applyLocale(AppLocale locale) {
        if (!locale.equals(context.getLocale()))
            context.setLocale(locale);
        Locale.setDefault(locale.locale);

        uiText.clear();
        uiText.putAll(locale.labels);

        tableTab.setText(uiText.get("tab_table"));
        graphTab.setText(uiText.get("tab_graph"));
        searchField.setHint(uiText.get("search"));

        repaintActionButtons();

        localeButton.setLocaleOption(locale);

        graphCanvas.repaint();
        if (tablePanel != null) {
            tablePanel.applyLocale(locale);
        }
    }

    private void repaintActionButtons() {
        for (AppButtons.RoundedButton button : actionButtons) {
            button.updateText();
        }

        revalidate();
        repaint();
    }

    /**
     * Refreshes the table data from the server.
     * Can be called from action handlers or menu items.
     */
    public void refreshTableData() {
        if (tablePanel != null) {
            try {
                tablePanel.refreshFromServer();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        this,
                        "Failed to refresh data: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    static class HintTextField extends JTextField {
        private String hint = "";

        HintTextField(int columns) {
            super(columns);
            setOpaque(true);
            setBackground(Color.WHITE);
            setForeground(new Color(30, 35, 40));
        }

        void setHint(String hint) {
            this.hint = hint;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if ((getText() == null || getText().isEmpty()) && !isFocusOwner() && hint != null && !hint.isEmpty()) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setColor(new Color(140, 148, 160));
                g2.setFont(getFont().deriveFont(Font.PLAIN, getFont().getSize2D()));
                Insets insets = getInsets();
                FontMetrics fm = g2.getFontMetrics();
                int x = insets.left + 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2 - 1;
                g2.drawString(hint, x, y);
                g2.dispose();
            }
        }
    }

    static class JTableHeaderStyler {
        static void style(JTable table) {
            table.getTableHeader().setReorderingAllowed(false);
            table.getTableHeader().setResizingAllowed(true);
            table.getTableHeader().setPreferredSize(new Dimension(0, 32));
            table.getTableHeader().setBackground(new Color(243, 246, 250));
            table.getTableHeader().setForeground(new Color(40, 45, 55));
            table.getTableHeader().setBorder(new MatteBorder(0, 0, 1, 0, new Color(220, 226, 235)));
            table.setFont(table.getFont().deriveFont(Font.PLAIN, 13f));
            table.getTableHeader().setFont(table.getTableHeader().getFont().deriveFont(Font.BOLD, 13f));
        }
    }
}
