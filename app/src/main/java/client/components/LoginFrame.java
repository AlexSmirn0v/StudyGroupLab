package client.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import client.AppContext;
import client.AppLocale;
import client.components.AppButtons.RoundLocaleButton;
import client.components.AppButtons.RoundedButton;
import model.CommandFormat;
import model.CommandMessage;

public class LoginFrame extends AppFrame {
    private JLabel titleLabel;
    private JTextField usernameField, passwordField;
    private RoundLocaleButton localeButton;
    private RoundedButton enterButton;

    private String username;
    private String password;

    public LoginFrame(AppContext context) {
        super(context);

        setMinimumSize(new Dimension(440, 400));
        setSize(520, 400);

        initComponents();
    }

    @Override
    JComponent buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(true);
        header.setBorder(BorderFactory.createEmptyBorder(14, 16, 6, 16));

        JPanel left = new JPanel(new BorderLayout(0, 2));
        left.setOpaque(false);

        titleLabel = new JLabel();
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 16f));
        titleLabel.setForeground(new Color(28, 35, 44));
        left.add(titleLabel, BorderLayout.NORTH);

        header.add(left, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        right.setOpaque(false);
        localeButton = new RoundLocaleButton();
        localeButton.addActionListener(e -> AppLocale.cycleLocale(this, context.getLocale()));
        right.add(localeButton);
        header.add(right, BorderLayout.EAST);

        return header;
    }

    @Override
    JComponent buildForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(SCROLL_BORDER, 1, true),
                new EmptyBorder(12, 14, 12, 14)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.insets = new Insets(0, 0, 10, 0);

        int row = 0;
        row = addSectionTitle(form, gbc, row, "login_hello");
        usernameField = addFieldRow(form, gbc, row++, "login_username", true);
        passwordField = addPasswordRow(form, gbc, row++, "login_password", true);

        gbc.weighty = 1;
        form.add(Box.createVerticalGlue(), gbc);

        return form;
    }

    @Override
    JComponent buildFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(true);
        footer.setBorder(BorderFactory.createEmptyBorder(4, 12, 12, 12));

        statusLabel = new JLabel(" ");
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.PLAIN, 12f));
        statusLabel.setBorder(new EmptyBorder(0, 4, 8, 4));
        footer.add(statusLabel, BorderLayout.NORTH);

        enterButton = new RoundedButton();
        enterButton.setPreferredSize(new Dimension(140, 40));
        enterButton.setMinimumSize(new Dimension(120, 40));
        enterButton.addActionListener(e -> authUser());
        footer.add(enterButton, BorderLayout.SOUTH);

        return footer;
    }

    private void authUser() {
        try {
            username = usernameField.getText();
            password = passwordField.getText();
            CommandMessage message = new CommandMessage(CommandFormat.INFO, username, password);
            String response = context.getConnectFacade().askServer(message);
            if (response == null || response.isBlank()) {
                setStatus(context.getLocalText("table_err_admin_empty"), ERROR);
                return;
            } else if (response.toLowerCase().contains("ошибка")) {
                setStatus(context.getLocalText("login_error"), ERROR);
                return;
            }
            context.setCredentials(username, password);
            SwingUtilities.invokeLater(this::dispose);
            SwingUtilities.invokeLater(() -> new MainWindow(context).setVisible(true));
        } catch (IllegalArgumentException ex) {
            setStatus(ex.getMessage(), ERROR);
        }
    }

    @Override
    public void applyLocale(AppLocale locale) {
        if (!locale.equals(context.getLocale()))
            context.setLocale(locale);
        Locale.setDefault(locale.locale);

        setTitle(context.getLocalText("login_title"));
        if (titleLabel != null)
            titleLabel.setText(context.getLocalText("form_heading"));

        for (SectionTitle section : sectionTitles) {
            section.label.setText(context.getLocalText(section.key));
        }
        for (FieldRow row : fieldRows) {
            updateFieldLabel(row);
            row.field.setToolTipText(context.getLocalText("form_input") + context.getLocalText(row.hintKey));
        }
        if (enterButton != null)
            enterButton.setText(context.getLocalText("login_button"));

        revalidate();
        repaint();
    }

    JPasswordField addPasswordRow(JPanel form, GridBagConstraints gbc, int row, String hintKey, boolean required) {
        gbc.gridy = row;
        JPanel rowPanel = new JPanel(new BorderLayout(0, 4));
        rowPanel.setOpaque(false);

        JLabel label = new JLabel();
        rowPanel.add(label, BorderLayout.NORTH);

        JPasswordField field = new JPasswordField();
        styleField(field);
        rowPanel.add(field, BorderLayout.CENTER);

        form.add(rowPanel, gbc);
        fieldRows.add(new FieldRow(label, field, hintKey, required));
        return field;
    }

}
