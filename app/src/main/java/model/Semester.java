package model;

/**
 * Перечисление семестров.
 */
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

    /**
     * Получает элемент перечисления по его имени.
     *
     * @param name отображаемое имя семестра
     * @return элемент перечисления
     * @throws IllegalArgumentException если имя неверное
     */
    public static Semester getByName(String name) throws IllegalArgumentException {
        return NamedEnum.getByName(Semester.class, name);
    }

    /**
     * Возвращает строку всех доступных имен семестров.
     *
     * @return строка с перечислением имен семестров
     */
    public static String getStringItems() {
        return NamedEnum.getStringItems(Semester.class);
    }
}
