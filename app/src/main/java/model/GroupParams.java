package model;

import java.util.List;

/**
 * Перечисление параметров учебной группы.
 */
public enum GroupParams implements NamedEnum {
    NAME("Название", "название группы"),
    COORDS("Координаты", List.of("первую координату (x)", "вторую координату (y)")),
    STUDENTS_COUNT("Количество студентов", "количество студентов"),
    TRANSFERRED_STUDENTS("Количество переведенных студентов", "количество переведенных студентов"),
    AVERAGE_MARK("Средняя оценка", "среднюю оценку"),
    SEMESTER_ENUM("Номер семестра", "номер семестра (возможные варианты: " + Semester.getStringItems() + ")"),
    GROUP_ADMIN("Администратор группы", List.of("имя администратора", "рост администратора",
            "номер паспорта администратора",
            "цвет волос администратора (возможные варианты: " + Color.getStringItems() + ")"));

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
