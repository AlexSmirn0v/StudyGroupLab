package model;

public enum Semester implements NamedEnum {
    FIRST("Первый"),
    SECOND("Второй"),
    FOURTH("Четвертый"),
    FIFTH("Пятый"),
    EIGHTH("Восьмой");

    private final String name;

    private Semester(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public static Semester getByName(String name) throws IllegalArgumentException {
        return NamedEnum.getByName(Semester.class, name);
    }

    public static String getStringItems() {
        return NamedEnum.getStringItems(Semester.class);
    }
}
