package model;

public enum GroupParams implements NamedEnum {
    NAME("Название"),
    COORDS("Координаты"),
    STUDENTS_COUNT("Количество студентов"),
    TRANSFERRED_STUDENTS("Количество переведенных студентов"),
    AVERAGE_MARK("Средняя оценка"),
    SEMESTER_ENUM("Номер семестра"),
    GROUP_ADMIN("Группа администраторов");

    private final String name;

    private GroupParams(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public static GroupParams getByName(String name) throws IllegalArgumentException {
        return NamedEnum.getByName(GroupParams.class, name);
    }

    public static String getStringItems() {
        return NamedEnum.getStringItems(GroupParams.class);
    }
}
