package client.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;

import client.AppLocale;
import client.AppContext;
import client.components.AppButtons.RoundLocaleButton;
import client.components.AppButtons.RoundedButton;
import model.GroupBuilder;
import model.Semester;
import model.StudyGroup;

public class GroupAddFrame extends JFrame implements AppLocale.Localizable {
    private static final Color BG = new Color(246, 248, 252);
    private static final Color FIELD_BORDER = new Color(200, 208, 220);
    private static final Color SCROLL_BORDER = new Color(214, 221, 233);
    private static final Color MUTED = new Color(100, 108, 120);
    private static final Color ERROR = new Color(196, 58, 58);
    private static final Color SUCCESS = new Color(36, 128, 72);

    private final String authorName;
    private final Function<StudyGroup, String> onSubmit;
    private final AppContext context;

    private final Map<String, String> uiText = new HashMap<>();

    private JLabel titleLabel;
    private JLabel authorLabel;
    private RoundLocaleButton localeButton;
    private JButton cancelButton;
    private RoundedButton submitButton;
    private final List<SectionTitle> sectionTitles = new ArrayList<>();
    private final List<FieldRow> fieldRows = new ArrayList<>();
    private final List<ComboRow> comboRows = new ArrayList<>();

    private JTextField nameField;
    private JTextField coordXField;
    private JTextField coordYField;
    private JTextField studentsCountField;
    private JTextField transferredField;
    private JTextField averageField;
    private JComboBox<String> semesterCombo;
    private JTextField adminNameField;
    private JTextField adminHeightField;
    private JTextField adminPassportField;
    private JComboBox<String> adminHairCombo;

    private JLabel statusLabel;

    public GroupAddFrame(AppContext context, Function<StudyGroup, String> onSubmit) {
        super();
        this.authorName = context.getUsername();
        this.onSubmit = onSubmit == null ? group -> null : onSubmit;
        this.context = context;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(440, 380));
        setSize(520, 580);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(0, 0));
        // getContentPane().setBackground(BG);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildScrollableForm(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);

        applyLocale(context.getLocale());
    }

    private JComponent buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(true);
        // header.setBackground(BG);
        header.setBorder(BorderFactory.createEmptyBorder(14, 16, 6, 16));

        JPanel left = new JPanel(new BorderLayout(0, 2));
        left.setOpaque(false);

        titleLabel = new JLabel();
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 16f));
        titleLabel.setForeground(new Color(28, 35, 44));
        left.add(titleLabel, BorderLayout.NORTH);

        if (!authorName.isBlank()) {
            authorLabel = new JLabel();
            // authorLabel.setForeground(MUTED);
            authorLabel.setFont(authorLabel.getFont().deriveFont(Font.PLAIN, 12f));
            left.add(authorLabel, BorderLayout.SOUTH);
        }

        header.add(left, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        right.setOpaque(false);
        localeButton = new RoundLocaleButton();
        localeButton.addActionListener(e -> AppLocale.cycleLocale(this, context.getLocale()));
        right.add(localeButton);
        header.add(right, BorderLayout.EAST);

        return header;
    }

    private JComponent buildScrollableForm() {
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
        row = addSectionTitle(form, gbc, row, "form_section_name");
        nameField = addFieldRow(form, gbc, row++, "form_hint_name", true);

        row = addSectionTitle(form, gbc, row, "form_section_coords");
        coordXField = addFieldRow(form, gbc, row++, "form_hint_coord_x", true);
        coordYField = addFieldRow(form, gbc, row++, "form_hint_coord_y", false);

        row = addSectionTitle(form, gbc, row, "form_section_params");
        studentsCountField = addFieldRow(form, gbc, row++, "form_hint_students", false);
        transferredField = addFieldRow(form, gbc, row++, "form_hint_transferred", true);
        averageField = addFieldRow(form, gbc, row++, "form_hint_average", true);
        semesterCombo = addComboRow(form, gbc, row++, "form_section_semester", "form_hint_semester", true);

        row = addSectionTitle(form, gbc, row, "form_section_admin");
        adminNameField = addFieldRow(form, gbc, row++, "form_hint_admin_name", false);
        adminHeightField = addFieldRow(form, gbc, row++, "form_hint_admin_height", false);
        adminPassportField = addFieldRow(form, gbc, row++, "form_hint_admin_passport", false);
        adminHairCombo = addComboRow(form, gbc, row++, "form_hair_color", "form_hint_admin_hair", false);

        gbc.weighty = 1;
        form.add(Box.createVerticalGlue(), gbc);

        JScrollPane scroll = new JScrollPane(form);
        scroll.setBorder(BorderFactory.createEmptyBorder(0, 12, 8, 12));
        scroll.getViewport().setBackground(BG);
        scroll.setBackground(BG);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        return scroll;
    }

    private JComponent buildFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(true);
        footer.setBackground(BG);
        footer.setBorder(BorderFactory.createEmptyBorder(4, 12, 12, 12));

        statusLabel = new JLabel(" ");
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.PLAIN, 12f));
        statusLabel.setForeground(MUTED);
        statusLabel.setBorder(new EmptyBorder(0, 4, 8, 4));
        footer.add(statusLabel, BorderLayout.NORTH);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setOpaque(false);

        cancelButton = new JButton();
        styleFlatButton(cancelButton);
        cancelButton.addActionListener(e -> dispose());

        submitButton = new RoundedButton();
        submitButton.setPreferredSize(new Dimension(140, 40));
        submitButton.setMinimumSize(new Dimension(120, 40));
        submitButton.addActionListener(e -> submitForm());

        buttons.add(cancelButton);
        buttons.add(submitButton);
        footer.add(buttons, BorderLayout.SOUTH);

        return footer;
    }

    @Override
    public void applyLocale(AppLocale locale) {
        context.setLocale(locale);
        Locale.setDefault(locale.locale);

        uiText.clear();
        uiText.putAll(locale.labels);

        setTitle(txt("add"));
        titleLabel.setText(txt("form_heading"));
        if (authorLabel != null) {
            authorLabel.setText(txt("form_author") + ": " + authorName);
        }

        for (SectionTitle section : sectionTitles) {
            section.label.setText(txt(section.key));
        }
        for (FieldRow row : fieldRows) {
            updateFieldLabel(row);
            row.field.setToolTipText(txt(row.hintKey));
        }
        for (ComboRow row : comboRows) {
            updateFieldLabel(row.label, row.labelKey, row.required);
            row.combo.setToolTipText(txt(row.hintKey));
            refreshEnumCombo(row.combo, row.includeSemesters);
        }

        cancelButton.setText(txt("form_cancel"));
        submitButton.setText(txt("form_submit"));
        localeButton.setLocaleOption(locale);

        revalidate();
        repaint();
    }

    private String txt(String key) {
        return uiText.getOrDefault(key, key);
    }

    private int addSectionTitle(JPanel form, GridBagConstraints gbc, int row, String key) {
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

    private JTextField addFieldRow(JPanel form, GridBagConstraints gbc, int row, String hintKey, boolean required) {
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

    private JComboBox<String> addComboRow(JPanel form, GridBagConstraints gbc, int row, String labelKey,
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

    private void updateFieldLabel(FieldRow row) {
        updateFieldLabel(row.label, row.hintKey, row.required);
    }

    private void updateFieldLabel(JLabel label, String hintKey, boolean required) {
        String suffix = required ? " *" : "";
        label.setText("<html>" + txt(hintKey) + "<font color='#c43a3a'>" + suffix + "</font></html>");
        label.setFont(label.getFont().deriveFont(Font.PLAIN, 12f));
        label.setForeground(new Color(55, 62, 72));
    }

    private void refreshEnumCombo(JComboBox<String> combo, boolean semesters) {
        int index = combo.getSelectedIndex();
        combo.removeAllItems();
        combo.addItem(txt("form_combo_none"));
        if (semesters) {
            for (Semester semester : Semester.values()) {
                combo.addItem(semester.getName());
            }
        } else {
            for (model.Color color : model.Color.values()) {
                combo.addItem(color.getName());
            }
        }
        combo.setSelectedIndex(index >= 0 && index < combo.getItemCount() ? index : 0);
    }

    private void styleField(JTextField field) {
        field.setOpaque(true);
        field.setBackground(Color.WHITE);
        field.setForeground(new Color(30, 35, 40));
        field.setBorder(new CompoundBorder(
                new LineBorder(FIELD_BORDER, 1, true),
                new EmptyBorder(6, 10, 6, 10)));
        field.setPreferredSize(new Dimension(0, 32));
    }

    private void styleCombo(JComboBox<?> combo) {
        combo.setBackground(Color.WHITE);
        combo.setBorder(new CompoundBorder(
                new LineBorder(FIELD_BORDER, 1, true),
                new EmptyBorder(4, 8, 4, 8)));
        combo.setPreferredSize(new Dimension(0, 32));
    }

    private void styleFlatButton(JButton button) {
        button.setFocusPainted(false);
        button.setBackground(Color.WHITE);
        button.setBorder(new CompoundBorder(
                new LineBorder(new Color(188, 197, 209), 1, true),
                new EmptyBorder(8, 16, 8, 16)));
        button.setPreferredSize(new Dimension(100, 36));
    }

    private void submitForm() {
        clearFieldErrors();
        try {
            StudyGroup group = buildGroup();
            String response = onSubmit.apply(group);
            if (response == null || response.isBlank()) {
                setStatus(txt("form_error_no_server"), ERROR);
                return;
            }
            if (response.toLowerCase().contains("успешно")) {
                setStatus(response, SUCCESS);
                SwingUtilities.invokeLater(this::dispose);
            } else {
                setStatus(response, ERROR);
            }
        } catch (IllegalArgumentException ex) {
            setStatus(ex.getMessage(), ERROR);
            highlightInvalidField(ex.getMessage());
        }
    }

    private StudyGroup buildGroup() throws IllegalArgumentException {
        if (authorName.isBlank()) {
            throw new IllegalArgumentException(txt("form_error_no_author"));
        }

        GroupBuilder builder = new GroupBuilder();

        builder.setName(requireText(nameField, txt("form_section_name")));

        String coordX = requireText(coordXField, txt("form_coord_x"));
        String coordY = coordYField.getText().trim();
        String coords = coordY.isBlank() ? coordX : coordX + StudyGroup.DELIMITER + coordY;
        builder.setCoords(coords);

        builder.setStudentsCount(studentsCountField.getText().trim());
        builder.setTransferredStudents(requireText(transferredField, txt("form_hint_transferred")));
        builder.setAverageMark(requireText(averageField, txt("form_hint_average")));

        String semester = selectedComboValue(semesterCombo);
        if (semester != null) {
            builder.setSemesterEnum(semester);
        }

        if (hasAdminInput()) {
            builder.setGroupAdmin(buildAdminDescription());
        }

        StudyGroup group = builder.build();
        group.setAuthor(authorName);
        return group;
    }

    private String buildAdminDescription() throws IllegalArgumentException {
        String name = adminNameField.getText().trim();
        String height = adminHeightField.getText().trim();
        String passport = adminPassportField.getText().trim();
        String hair = selectedComboValue(adminHairCombo);

        if (name.isBlank() || height.isBlank() || passport.isBlank()) {
            throw new IllegalArgumentException(txt("form_error_admin"));
        }

        StringBuilder sb = new StringBuilder(name);
        sb.append(StudyGroup.DELIMITER).append(height);
        sb.append(StudyGroup.DELIMITER).append(passport);
        if (hair != null) {
            sb.append(StudyGroup.DELIMITER).append(hair);
        }
        return sb.toString();
    }

    private boolean hasAdminInput() {
        return !adminNameField.getText().isBlank()
                || !adminHeightField.getText().isBlank()
                || !adminPassportField.getText().isBlank()
                || selectedComboValue(adminHairCombo) != null;
    }

    private String requireText(JTextField field, String fieldName) throws IllegalArgumentException {
        String value = field.getText().trim();
        if (value.isBlank()) {
            throw new IllegalArgumentException(txt("form_error_fill").formatted(fieldName));
        }
        return value;
    }

    private static String selectedComboValue(JComboBox<String> combo) {
        if (combo == null || combo.getSelectedIndex() <= 0) {
            return null;
        }
        Object item = combo.getSelectedItem();
        return item == null ? null : item.toString();
    }

    private void setStatus(String message, Color color) {
        statusLabel.setText(message == null ? " " : message);
        statusLabel.setForeground(color);
    }

    private void clearFieldErrors() {
        JTextField[] fields = {
                nameField, coordXField, coordYField, studentsCountField,
                transferredField, averageField, adminNameField, adminHeightField, adminPassportField
        };
        for (JTextField field : fields) {
            if (field != null) {
                styleField(field);
            }
        }
    }

    private void highlightInvalidField(String message) {
        if (message == null) {
            return;
        }
        String lower = message.toLowerCase();
        JTextField target = null;
        if (lower.contains("название") || lower.contains("name") || lower.contains("navn")
                || (lower.contains("пустая строка") && nameField.getText().isBlank())) {
            target = nameField;
        } else if (lower.contains("координат") || lower.contains("coord") || lower.contains("koordinat")) {
            target = coordXField;
        } else if (lower.contains("перевед") || lower.contains("overfør")) {
            target = transferredField;
        } else if (lower.contains("оценк") || lower.contains("average") || lower.contains("karakter")) {
            target = averageField;
        } else if (lower.contains("студент") && !lower.contains("перевед")) {
            target = studentsCountField;
        } else if (lower.contains("администратор") || lower.contains("administrator")
                || lower.contains("паспорт") || lower.contains("pass") || lower.contains("рост")
                || lower.contains("høyde") || lower.contains("altura")) {
            target = adminNameField;
        } else if (lower.contains("семестр") || lower.contains("semester")) {
            semesterCombo.requestFocusInWindow();
            return;
        }
        if (target != null) {
            markFieldError(target);
            target.requestFocusInWindow();
        }
    }

    private void markFieldError(JTextField field) {
        field.setBorder(new CompoundBorder(
                new LineBorder(ERROR, 1, true),
                new EmptyBorder(6, 10, 6, 10)));
    }

    private static final class SectionTitle {
        final JLabel label;
        final String key;

        SectionTitle(JLabel label, String key) {
            this.label = label;
            this.key = key;
        }
    }

    private static final class FieldRow {
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

    private static final class ComboRow {
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
}
