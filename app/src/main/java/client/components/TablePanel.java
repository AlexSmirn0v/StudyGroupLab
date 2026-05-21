package client.components;

import client.AppContext;
import client.AppLocale;
import client.components.TableModel.Column;
import model.CommandFormat;
import model.CommandMessage;
import model.Semester;
import model.StudyGroup;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;

public class TablePanel extends JPanel implements AppContext.Updatable, AppLocale.Localizable {
    private final TableModel tableModel;
    private final JTable table;
    private final TableRowSorter<TableModel> sorter;

    private final AppContext appContext;

    /**
     * Creates a TablePanel with data from AppContext.
     * 
     * @param appContext the application context containing ConnectFacade and
     *                   credentials
     */
    public TablePanel(AppContext appContext) {
        super(new BorderLayout());
        this.appContext = appContext;
        setBorder(new EmptyBorder(0, 0, 0, 0));
        applyLocale(appContext.getLocale());

        tableModel = new TableModel(appContext);
        table = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);

        configureTable();
        configureSorting();
        configureEditors();
        configureDeleteClick();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);
        appContext.registerLocalizable(this);
        appContext.registerUpdatable(this);
    }

    public JTable getTable() {
        return table;
    }

    @Override
    public void update() {
        try {
            refreshFromServer();
        } catch (Exception e) {
            System.err.println("Failed to refresh table from server: " + e.getMessage());
        }
    }

    @Override
    public void applyLocale(AppLocale locale) {
        for (Column column : Column.values()) {
            column.setTitle(appContext);
        }
        // Update table header with new locale
        if (table != null) {
            table.getTableHeader().repaint();
            table.repaint();
        }
        if (tableModel != null) {
            tableModel.fireTableStructureChanged();
            configureTable();
        }
    }

    public void refresh(Collection<StudyGroup> groups) {
        tableModel.setGroups(groups);
    }

    /**
     * Fetches all study groups from the server and refreshes the table.
     * 
     * @throws Exception if the server request fails or response cannot be parsed
     */
    public void refreshFromServer() throws Exception {
        if (appContext.getConnectFacade() == null) {
            throw new IllegalStateException("ConnectFacade is not available");
        }

        CommandMessage message = new CommandMessage(
                CommandFormat.SHOW,
                appContext.getUsername(),
                appContext.getPassword());

        Collection<StudyGroup> groups = appContext.getConnectFacade().askStudyGroup(message);
        if (groups != null)
            refresh(groups);
    }

    private void configureTable() {
        table.setRowHeight(28);
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(false);
        table.setRowSorter(sorter);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setSurrendersFocusOnKeystroke(true);
        table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(true);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table,
                    Object value,
                    boolean isSelected,
                    boolean hasFocus,
                    int row,
                    int column) {
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                int modelColumn = table.convertColumnIndexToModel(column);
                if (modelColumn == Column.DELETE.ordinal()) {
                    boolean deletable = TableHandlers.getCanDelete(appContext)
                            .test(tableModel.getGroupAt(table.convertRowIndexToModel(row)));
                    setHorizontalAlignment(SwingConstants.CENTER);
                    setText(deletable ? "Delete" : "");
                    setForeground(deletable
                            ? (isSelected ? getForeground() : new java.awt.Color(180, 40, 40))
                            : new java.awt.Color(160, 160, 160));
                } else {
                    setHorizontalAlignment(LEFT);
                }
                return c;
            }
        });

        setWidth(Column.ID, 70);
        setWidth(Column.NAME, 170);
        setWidth(Column.AUTHOR, 150);
        setWidth(Column.CREATION_DATE, 190);
        setWidth(Column.COORDS, 120);
        setWidth(Column.STUDENTS_COUNT, 120);
        setWidth(Column.TRANSFERRED_STUDENTS, 135);
        setWidth(Column.AVERAGE_MARK, 105);
        setWidth(Column.SEMESTER_ENUM, 130);
        setWidth(Column.GROUP_ADMIN, 280);
        setWidth(Column.DELETE, 80);
    }

    private void setWidth(Column column, int width) {
        int viewIndex = table.convertColumnIndexToView(column.ordinal());
        if (viewIndex >= 0) {
            table.getColumnModel().getColumn(viewIndex).setPreferredWidth(width);
        }
    }

    private void configureSorting() {
        sorter.setSortsOnUpdates(true);

        Comparator<String> stringComparator = Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER);
        Comparator<Long> longComparator = Comparator.nullsLast(Comparator.naturalOrder());
        Comparator<Integer> intComparator = Comparator.nullsLast(Comparator.naturalOrder());
        Comparator<LocalDateTime> dateComparator = Comparator.nullsLast(Comparator.naturalOrder());

        sorter.setComparator(Column.ID.ordinal(), longComparator);
        sorter.setComparator(Column.NAME.ordinal(), stringComparator);
        sorter.setComparator(Column.AUTHOR.ordinal(), stringComparator);
        sorter.setComparator(Column.CREATION_DATE.ordinal(), dateComparator);
        sorter.setComparator(Column.COORDS.ordinal(), stringComparator);
        sorter.setComparator(Column.STUDENTS_COUNT.ordinal(), longComparator);
        sorter.setComparator(Column.TRANSFERRED_STUDENTS.ordinal(), intComparator);
        sorter.setComparator(Column.AVERAGE_MARK.ordinal(), intComparator);
        sorter.setComparator(Column.SEMESTER_ENUM.ordinal(), stringComparator);
        sorter.setComparator(Column.GROUP_ADMIN.ordinal(), stringComparator);
    }

    private void configureEditors() {
        JComboBox<String> semesterBox = new JComboBox<>();
        semesterBox.addItem("");
        for (Semester semester : Semester.values()) {
            semesterBox.addItem(semester.getName());
        }

        int semesterViewColumn = table.convertColumnIndexToView(Column.SEMESTER_ENUM.ordinal());
        if (semesterViewColumn >= 0) {
            table.getColumnModel().getColumn(semesterViewColumn)
                    .setCellEditor(new DefaultCellEditor(semesterBox));
        }
    }

    private void configureDeleteClick() {
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1) {
                    return;
                }

                int viewRow = table.rowAtPoint(e.getPoint());
                int viewCol = table.columnAtPoint(e.getPoint());
                if (viewRow < 0 || viewCol < 0) {
                    return;
                }

                int modelCol = table.convertColumnIndexToModel(viewCol);
                if (modelCol != Column.DELETE.ordinal()) {
                    return;
                }

                int modelRow = table.convertRowIndexToModel(viewRow);
                StudyGroup group = tableModel.getGroupAt(modelRow);

                if (!TableHandlers.getCanDelete(appContext).test(group)) {
                    Toolkit.getDefaultToolkit().beep();
                    return;
                }

                int answer = JOptionPane.showConfirmDialog(
                        TablePanel.this,
                        "Delete this group?",
                        "Confirm delete",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);

                if (answer != JOptionPane.YES_OPTION) {
                    return;
                }

                try {
                    TableHandlers.getDeleteHandler(appContext).accept(group);
                    tableModel.removeRow(modelRow);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            TablePanel.this,
                            ex.getMessage(),
                            "Delete failed",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        table.setCursor(Cursor.getDefaultCursor());
    }
}
