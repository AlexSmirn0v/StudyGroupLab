package client.components;

import model.Semester;
import model.StudyGroup;

import java.util.List;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.table.AbstractTableModel;

import client.AppContext;
import client.components.TableHandlers.UpdateHandler;

public class TableModel extends AbstractTableModel {
    final private AppContext appContext;

    TableModel(AppContext context) {
        this.appContext = context;
    }

    enum EditableProperty {
        NAME,
        COORDS,
        STUDENTS_COUNT,
        TRANSFERRED_STUDENTS,
        AVERAGE_MARK,
        SEMESTER_ENUM,
        GROUP_ADMIN
    }

    enum Column {
        ID("table_col_id", Long.class, false),
        NAME("table_col_name", String.class, true),
        AUTHOR("table_col_author", String.class, false),
        CREATION_DATE("table_col_creation_date", LocalDateTime.class, false),
        COORDS("table_col_coords", String.class, true),
        STUDENTS_COUNT("table_col_students_count", Long.class, true),
        TRANSFERRED_STUDENTS("table_col_transferred_students", Integer.class, true),
        AVERAGE_MARK("table_col_average_mark", Integer.class, true),
        SEMESTER_ENUM("table_col_semester", String.class, true),
        GROUP_ADMIN("table_col_admin", String.class, true),
        DELETE("table_col_delete", String.class, false);

        final String labelKey;
        final Class<?> columnClass;
        final boolean editable;
        String title;

        Column(String labelKey, Class<?> columnClass, boolean editable) {
            this.labelKey = labelKey;
            this.columnClass = columnClass;
            this.editable = editable;
        }

        public void setTitle(AppContext appContext) {
            this.title = appContext.getLocalText(labelKey);
        }

        public String getTitle() {
            return title;
        }
    }

    private final List<StudyGroup> rows = new ArrayList<>();

    void setGroups(Collection<StudyGroup> groups) {
        rows.clear();
        if (groups != null) {
            rows.addAll(groups);
        }
        fireTableDataChanged();
    }

    StudyGroup getGroupAt(int modelRow) {
        return rows.get(modelRow);
    }

    void removeRow(int modelRow) {
        rows.remove(modelRow);
        fireTableRowsDeleted(modelRow, modelRow);
    }

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public int getColumnCount() {
        return Column.values().length;
    }

    @Override
    public String getColumnName(int column) {
        return Column.values()[column].getTitle();
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return Column.values()[columnIndex].columnClass;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return Column.values()[columnIndex].editable;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        StudyGroup group = rows.get(rowIndex);
        Column column = Column.values()[columnIndex];

        return switch (column) {
            case ID -> group.getId();
            case NAME -> group.getName();
            case AUTHOR -> group.getAuthorName();
            case CREATION_DATE -> group.getCreationDate();
            case COORDS -> formatCoordinates(group);
            case STUDENTS_COUNT -> group.getStudentsCount();
            case TRANSFERRED_STUDENTS -> group.getTransferredStudents();
            case AVERAGE_MARK -> group.getAverageMark();
            case SEMESTER_ENUM -> group.getSemesterEnum() == null ? "" : group.getSemesterEnum().getName();
            case GROUP_ADMIN -> formatPerson(group);
            case DELETE -> TableHandlers.getCanDelete(appContext).test(group) ? "Delete" : "";
        };
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        StudyGroup group = rows.get(rowIndex);
        Column column = Column.values()[columnIndex];

        if (!column.editable) {
            return;
        }

        String raw = aValue == null ? "" : aValue.toString();
        String validated = validateAndNormalize(group, column, raw);

        if (validated == null && column != Column.STUDENTS_COUNT && column != Column.SEMESTER_ENUM) {
            return;
        }
        UpdateHandler updateHandler = TableHandlers.getUpdateHandler(appContext);
        updateHandler.update(group, toProperty(column), validated);
        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    private EditableProperty toProperty(Column column) {
        return switch (column) {
            case NAME -> EditableProperty.NAME;
            case COORDS -> EditableProperty.COORDS;
            case STUDENTS_COUNT -> EditableProperty.STUDENTS_COUNT;
            case TRANSFERRED_STUDENTS -> EditableProperty.TRANSFERRED_STUDENTS;
            case AVERAGE_MARK -> EditableProperty.AVERAGE_MARK;
            case SEMESTER_ENUM -> EditableProperty.SEMESTER_ENUM;
            case GROUP_ADMIN -> EditableProperty.GROUP_ADMIN;
            default -> throw new IllegalArgumentException("Unsupported column: " + column);
        };
    }

    private String validateAndNormalize(StudyGroup group, Column column, String raw) {
        String value = raw == null ? "" : raw.trim();

        switch (column) {
            case NAME:
                if (value.isBlank()) {
                    throw new IllegalArgumentException("Name cannot be empty.");
                }
                return value;

            case AUTHOR:
                if (value.isBlank()) {
                    throw new IllegalArgumentException("Author cannot be empty.");
                }
                return value;

            case COORDS:
                return normalizeCoordinates(value);

            case STUDENTS_COUNT:
                if (value.isBlank()) {
                    return "";
                }
                long students = parseLong(value, "Students count");
                if (students <= 0) {
                    throw new IllegalArgumentException("Students count must be greater than 0.");
                }
                return Long.toString(students);

            case TRANSFERRED_STUDENTS:
                int transferred = parseInt(value, "Transferred students");
                if (transferred <= 0) {
                    throw new IllegalArgumentException("Transferred students must be greater than 0.");
                }
                return Integer.toString(transferred);

            case AVERAGE_MARK:
                int average = parseInt(value, "Average mark");
                if (average <= 0) {
                    throw new IllegalArgumentException("Average mark must be greater than 0.");
                }
                return Integer.toString(average);

            case SEMESTER_ENUM:
                if (value.isBlank()) {
                    return "";
                }
                Semester semester = Semester.getByName(value);
                return semester == null ? "" : semester.getName();

            case GROUP_ADMIN:
                return normalizePerson(value);

            default:
                throw new IllegalArgumentException("Unsupported column.");
        }
    }

    private String formatCoordinates(StudyGroup group) {
        if (group.getCoordinates() == null) {
            return "";
        }
        return group.getCoordinates().getX() + ";" + group.getCoordinates().getY();
    }

    private String formatPerson(StudyGroup group) {
        if (group.getGroupAdmin() == null) {
            return "";
        }

        String hair = group.getGroupAdmin().getHairColor() == null
                ? ""
                : group.getGroupAdmin().getHairColor().getName();

        return group.getGroupAdmin().getName()
                + ";"
                + group.getGroupAdmin().getHeight()
                + ";"
                + group.getGroupAdmin().getPassportID()
                + ";"
                + hair;
    }

    private String normalizeCoordinates(String raw) {
        if (raw.isBlank()) {
            throw new IllegalArgumentException("Coordinates cannot be empty.");
        }

        String normalized = raw.replace('\n', ';');
        String[] parts = normalized.split(";", -1);

        if (parts.length == 0 || parts.length > 2) {
            throw new IllegalArgumentException("Coordinates must be in format x;y or x.");
        }

        long x = parseLong(parts[0].trim(), "Coordinate x");
        long y = 0L;

        if (parts.length == 2 && !parts[1].trim().isBlank()) {
            y = parseLong(parts[1].trim(), "Coordinate y");
        }

        return x + "\n" + y;
    }

    private String normalizePerson(String raw) {
        if (raw.isBlank()) {
            throw new IllegalArgumentException("Group admin cannot be empty.");
        }

        String[] parts = raw.split(";", -1);
        if (parts.length < 3) {
            throw new IllegalArgumentException(
                    "Group admin must be in format name;height;passport;hair");
        }

        String name = parts[0].trim();
        if (name.isBlank()) {
            throw new IllegalArgumentException("Admin name cannot be empty.");
        }

        int height = parseInt(parts[1].trim(), "Admin height");
        if (height <= 0) {
            throw new IllegalArgumentException("Admin height must be greater than 0.");
        }

        String passport = parts[2].trim();
        if (passport.isBlank()) {
            throw new IllegalArgumentException("Admin passport cannot be empty.");
        }

        String hair = parts.length > 3 ? parts[3].trim() : "";
        if (!hair.isBlank()) {
            // validates against your domain enum/model
            model.Color.getByName(hair);
        }

        return name + "\n" + height + "\n" + passport + "\n" + hair;
    }

    private long parseLong(String text, String field) {
        try {
            return Long.parseLong(text);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid number for " + field + ".");
        }
    }

    private int parseInt(String text, String field) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid number for " + field + ".");
        }
    }
}
