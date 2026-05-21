package client.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;

import client.AppContext;
import client.AppLocale;

public abstract class AppFrame extends JFrame implements AppLocale.Localizable {
    static final Color BG = new Color(246, 248, 252);
    static final Color FIELD_BORDER = new Color(200, 208, 220);
    static final Color SCROLL_BORDER = new Color(214, 221, 233);
    static final Color MUTED = new Color(100, 108, 120);
    static final Color ERROR = new Color(196, 58, 58);
    static final Color SUCCESS = new Color(36, 128, 72);

    final AppContext context;
    JLabel statusLabel = new JLabel();

    final List<FieldRow> fieldRows = new ArrayList<>();
    final List<ComboRow> comboRows = new ArrayList<>();
    final List<SectionTitle> sectionTitles = new ArrayList<>();

    AppFrame(AppContext context) {
        this.context = context;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(0, 0));
    }

    void initComponents() {
        add(buildHeader(), BorderLayout.NORTH);
        add(buildForm(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);

        applyLocale(context.getLocale());
        context.registerLocalizable(this);
    }

    abstract JComponent buildHeader();

    abstract JComponent buildForm();

    abstract JComponent buildFooter();

    JTextField addFieldRow(JPanel form, GridBagConstraints gbc, int row, String hintKey, boolean required) {
        gbc.gridy = row;
        JPanel rowPanel = new JPanel(new BorderLayout(0, 4));
        rowPanel.setOpaque(false);

        JLabel label = new JLabel();
        rowPanel.add(label, BorderLayout.NORTH);

        JTextField field = new JTextField();
        styleField(field);
        rowPanel.add(field, BorderLayout.CENTER);

        form.add(rowPanel, gbc);
        fieldRows.add(new FieldRow(label, field, hintKey, required));
        return field;
    }

    JComboBox<String> addComboRow(JPanel form, GridBagConstraints gbc, int row, String labelKey,
            String hintKey, boolean includeSemesters) {
        gbc.gridy = row;
        JPanel rowPanel = new JPanel(new BorderLayout(0, 4));
        rowPanel.setOpaque(false);

        JLabel label = new JLabel();
        rowPanel.add(label, BorderLayout.NORTH);

        JComboBox<String> combo = new JComboBox<>();
        combo.setEditable(false);
        styleCombo(combo);
        rowPanel.add(combo, BorderLayout.CENTER);

        form.add(rowPanel, gbc);
        comboRows.add(new ComboRow(label, combo, labelKey, hintKey, false, includeSemesters));
        return combo;
    }

    void updateFieldLabel(FieldRow row) {
        updateFieldLabel(row.label, row.hintKey, row.required);
    }

    void updateFieldLabel(JLabel label, String hintKey, boolean required) {
        String suffix = required ? " <font color='#c43a3a'>*</font>" : "";
        label.setText(
                "<html>" + context.getLocalText("form_input") + context.getLocalText(hintKey) + suffix + "</html>");
        label.setFont(label.getFont().deriveFont(Font.PLAIN, 12f));
        label.setForeground(new Color(55, 62, 72));
    }

    int addSectionTitle(JPanel form, GridBagConstraints gbc, int row, String key) {
        gbc.gridy = row;
        JLabel label = new JLabel();
        label.setFont(label.getFont().deriveFont(Font.BOLD, 13f));
        label.setForeground(new Color(40, 48, 58));
        label.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(row == 0 ? 0 : 8, 0, 0, 0, new Color(230, 235, 242)),
                new EmptyBorder(row == 0 ? 0 : 10, 0, 4, 0)));
        form.add(label, gbc);
        sectionTitles.add(new SectionTitle(label, key));
        return row + 1;
    }

    void styleField(JTextField field) {
        field.setOpaque(true);
        field.setBackground(Color.WHITE);
        field.setForeground(new Color(30, 35, 40));
        field.setBorder(new CompoundBorder(
                new LineBorder(FIELD_BORDER, 1, true),
                new EmptyBorder(6, 10, 6, 10)));
        field.setPreferredSize(new Dimension(0, 32));
    }

    void styleCombo(JComboBox<?> combo) {
        combo.setBackground(Color.WHITE);
        combo.setBorder(new CompoundBorder(
                new LineBorder(FIELD_BORDER, 1, true),
                new EmptyBorder(4, 8, 4, 8)));
        combo.setPreferredSize(new Dimension(0, 32));
    }

    void styleFlatButton(JButton button) {
        button.setFocusPainted(false);
        button.setBackground(Color.WHITE);
        button.setBorder(new CompoundBorder(
                new LineBorder(new Color(188, 197, 209), 1, true),
                new EmptyBorder(8, 16, 8, 16)));
        button.setPreferredSize(new Dimension(100, 36));
    }

    String requireText(JTextField field, String fieldName) throws IllegalArgumentException {
        String value = field.getText().trim();
        if (value.isBlank()) {
            throw new IllegalArgumentException(context.getLocalText("form_error_fill").formatted(fieldName));
        }
        return value;
    }

    static String selectedComboValue(JComboBox<String> combo) {
        if (combo == null || combo.getSelectedIndex() <= 0) {
            return null;
        }
        Object item = combo.getSelectedItem();
        return item == null ? null : item.toString();
    }

    void setStatus(String message, Color color) {
        statusLabel.setText(message == null ? " " : message);
        statusLabel.setForeground(color);
    }

    static final class FieldRow {
        final JLabel label;
        final JTextField field;
        final String hintKey;
        final boolean required;

        FieldRow(JLabel label, JTextField field, String hintKey, boolean required) {
            this.label = label;
            this.field = field;
            this.hintKey = hintKey;
            this.required = required;
        }
    }

    static final class ComboRow {
        final JLabel label;
        final JComboBox<String> combo;
        final String labelKey;
        final String hintKey;
        final boolean required;
        final boolean includeSemesters;

        ComboRow(JLabel label, JComboBox<String> combo, String labelKey, String hintKey, boolean required,
                boolean includeSemesters) {
            this.label = label;
            this.combo = combo;
            this.labelKey = labelKey;
            this.hintKey = hintKey;
            this.required = required;
            this.includeSemesters = includeSemesters;
        }
    }

    static final class SectionTitle {
        final JLabel label;
        final String key;

        SectionTitle(JLabel label, String key) {
            this.label = label;
            this.key = key;
        }
    }
}
