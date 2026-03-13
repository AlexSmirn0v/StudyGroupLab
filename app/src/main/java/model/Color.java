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

    public static Color getByName(String name) throws IllegalArgumentException {
        return NamedEnum.getByName(Color.class, name);
    }

    public static String getStringItems() {
        return NamedEnum.getStringItems(Color.class);
    }
}
