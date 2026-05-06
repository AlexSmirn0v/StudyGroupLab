package model;

/**
 * Перечисление цветов волос.
 */
public enum Color implements NamedEnum {
    RED("Красный"),
    BLUE("Синий"),
    ORANGE("Оранжевый"),
    BROWN("Коричневый");

    private final String name;

    private Color(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    /**
     * Получает элемент перечисления по его имени.
     *
     * @param name отображаемое имя цвета
     * @return элемент перечисления
     * @throws IllegalArgumentException если имя неверное
     */
    public static Color getByName(String name) throws IllegalArgumentException {
        return NamedEnum.getByName(Color.class, name);
    }

    /**
     * Возвращает строку всех доступных цветов.
     *
     * @return строка с перечислением цветов
     */
    public static String getStringItems() {
        return NamedEnum.getStringItems(Color.class);
    }
}
