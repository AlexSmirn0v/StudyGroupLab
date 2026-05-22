package client.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import client.AppLocale;
import client.AppContext;
import model.GroupBuilder;
import model.Semester;
import model.StudyGroup;

public class GroupAddFrame extends AppFrame {
    private final String authorName, key;
    private final Function<StudyGroup, String> onSubmit;

    private JLabel titleLabel = new JLabel();
    private JLabel authorLabel = new JLabel();
    private JButton cancelButton, submitButton;

    private JTextField nameField, coordXField, coordYField, studentsCountField, transferredField, averageField,
            adminNameField, adminHeightField, adminPassportField;
    private JComboBox<String> semesterCombo, adminHairCombo;

    public GroupAddFrame(AppContext context, String key, Function<StudyGroup, String> onSubmit) {
        super(context);
        this.key = key;
        this.authorName = context.getUsername();
        this.onSubmit = onSubmit == null ? group -> null : onSubmit;

        setMinimumSize(new Dimension(440, 380));
        setSize(520, 580);

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

        if (!authorName.isBlank()) {
            authorLabel = new JLabel();
            authorLabel.setFont(authorLabel.getFont().deriveFont(Font.PLAIN, 12f));
            left.add(authorLabel, BorderLayout.SOUTH);
        }

        header.add(left, BorderLayout.WEST);

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

    @Override
    JComponent buildFooter() {
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
        submitButton = new JButton();
        styleFlatButton(submitButton);
        submitButton.addActionListener(e -> submitForm());

        buttons.add(cancelButton);
        buttons.add(submitButton);
        footer.add(buttons, BorderLayout.SOUTH);

        return footer;
    }

    @Override
    public void applyLocale(AppLocale locale) {
        if (!locale.equals(context.getLocale()))
            context.setLocale(locale);
        Locale.setDefault(locale.locale);

        setTitle(context.getLocalText(key));
        if (titleLabel != null)
            titleLabel.setText(context.getLocalText("form_heading"));
        if (authorLabel != null) {
            authorLabel.setText(context.getLocalText("form_author") + ": " + authorName);
        }

        for (SectionTitle section : sectionTitles) {
            section.label.setText(context.getLocalText(section.key));
        }
        for (FieldRow row : fieldRows) {
            updateFieldLabel(row);
            row.field.setToolTipText(context.getLocalText("form_input") + context.getLocalText(row.hintKey));
        }
        for (ComboRow row : comboRows) {
            updateFieldLabel(row.label, row.labelKey, row.required);
            row.combo.setToolTipText(context.getLocalText("form_input") + context.getLocalText(row.hintKey));
            refreshEnumCombo(row.combo, row.includeSemesters);
        }
        if (cancelButton != null)
            cancelButton.setText(context.getLocalText("form_cancel"));
        if (submitButton != null)
            submitButton.setText(context.getLocalText("form_submit"));

        revalidate();
        repaint();
    }

    private void refreshEnumCombo(JComboBox<String> combo, boolean semesters) {
        int index = combo.getSelectedIndex();
        combo.removeAllItems();
        combo.addItem(context.getLocalText("form_combo_none"));
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

    private void submitForm() {
        clearFieldErrors();
        try {
            StudyGroup group = buildGroup();
            String response = onSubmit.apply(group);
            if (response == null || response.isBlank()) {
                setStatus(context.getLocalText("form_error_no_server"), ERROR);
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
            throw new IllegalArgumentException(context.getLocalText("form_error_no_author"));
        }

        GroupBuilder builder = new GroupBuilder();

        builder.setName(requireText(nameField, context.getLocalText("form_section_name")));

        String coordX = requireText(coordXField, context.getLocalText("form_coord_x"));
        String coordY = coordYField.getText().trim();
        String coords = coordY.isBlank() ? coordX : coordX + StudyGroup.DELIMITER + coordY;
        builder.setCoords(coords);

        builder.setStudentsCount(studentsCountField.getText().trim());
        builder.setTransferredStudents(requireText(transferredField, context.getLocalText("form_hint_transferred")));
        builder.setAverageMark(requireText(averageField, context.getLocalText("form_hint_average")));

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
            throw new IllegalArgumentException(context.getLocalText("form_error_admin"));
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
        if (lower.contains("Название")) {
            target = nameField;
        } else if (lower.contains("x")) {
            target = coordXField;
        } else if (lower.contains("y")) {
            target = coordYField;
        } else if (lower.contains("количество студентов")) {
            target = studentsCountField;
        } else if (lower.contains("переведенных студентов")) {
            target = transferredField;
        } else if (lower.contains("оценк")) {
            target = averageField;
        } else if (lower.contains("семестр")) {
            semesterCombo.requestFocusInWindow();
            return;
        } else if (lower.contains("имя") && lower.contains("рост")) {
            for (JTextField field : List.of(adminNameField, adminHeightField, adminPassportField)) {
                if (field.getText().isBlank()) {
                    target = field;
                    break;
                }
            }
        } else if (lower.contains("имя")) {
            target = adminNameField;
        } else if (lower.contains("рост")) {
            target = adminHeightField;
        } else if (lower.contains("паспорт")) {
            target = adminPassportField;
        } else if (lower.contains("волос")) {
            adminHairCombo.requestFocusInWindow();
            return;
        } else if (lower.contains("невозможное число")) {
            for (JTextField field : List.of(studentsCountField, transferredField, averageField, adminHeightField)) {
                if (Long.parseLong(field.getText()) <= 0) {
                    target = field;
                    break;
                }
            }
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
}
