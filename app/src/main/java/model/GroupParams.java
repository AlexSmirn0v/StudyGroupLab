package model;

import java.util.List;

/**
 * Перечисление параметров учебной группы.
 */
public enum GroupParams implements NamedEnum {
    NAME("Название", "название группы (строка)"),
    COORDS("Координаты", List.of("первую координату (x, целое число)", "вторую координату (y, целое число, необязательно)")),
    STUDENTS_COUNT("Количество студентов", "количество студентов (целое число, больше 0, необязательно)"),
    TRANSFERRED_STUDENTS("Количество переведенных студентов", "количество переведенных студентов (целое число, больше 0)"),
    AVERAGE_MARK("Средняя оценка", "среднюю оценку (целое число, больше 0)"),
    SEMESTER_ENUM("Номер семестра", "номер семестра (необязательно, возможные варианты: " + Semester.getStringItems() + ")"),
    GROUP_ADMIN("Администратор группы", List.of("имя администратора (строка)", "рост администратора (целое число, больше 0)",
            "номер паспорта администратора (строка)",
            "цвет волос администратора (необязательно, возможные варианты: " + Color.getStringItems() + ")"));

    private final String name;
    private final String[] inputAsks;

    private GroupParams(String name, List<String> inputAsks) {
        this.name = name;
        this.inputAsks = inputAsks.toArray(new String[0]);
    }

    private GroupParams(String name, String inputAsk) {
        this.name = name;
        this.inputAsks = new String[] { inputAsk };
    }

    @Override
    public String getName() {
        return this.name;
    }

    public String[] getInputAsks() {
        return this.inputAsks;
    }

    public static GroupParams getByName(String name) throws IllegalArgumentException {
        return NamedEnum.getByName(GroupParams.class, name);
    }

    public static String getStringItems() {
        return NamedEnum.getStringItems(GroupParams.class);
    }
}
