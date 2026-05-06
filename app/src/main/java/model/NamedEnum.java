package model;

import java.util.Objects;

/**
 * Интерфейс для перечислений с именами.
 */
public interface NamedEnum {
    /**
     * Возвращает отображаемое имя элемента перечисления.
     *
     * @return отображаемое имя
     */
    public String getName();

    /**
     * Ищет элемент перечисления по отображаемому имени.
     *
     * @param <T>       тип перечисления
     * @param enumClass класс перечисления
     * @param name      отображаемое имя элемента
     * @return элемент перечисления или null, если имя пустое
     * @throws IllegalArgumentException если имя не соответствует ни одному элементу
     */
    public static <T extends Enum<T> & NamedEnum> T getByName(Class<T> enumClass, String name)
            throws IllegalArgumentException {
        if (name == null || name.isBlank())
            return null;
        for (T s : enumClass.getEnumConstants()) {
            if (Objects.equals(name, s.getName()))
                return s;
        }
        throw new IllegalArgumentException("Неверное имя элемента перечисления: " + name);
    }

    /**
     * Формирует строку доступных имен элементов перечисления.
     *
     * @param <T>       тип перечисления
     * @param enumClass класс перечисления
     * @return строка с доступными именами элементов
     */
    public static <T extends Enum<T> & NamedEnum> String getStringItems(Class<T> enumClass) {
        String[] names = new String[enumClass.getEnumConstants().length];
        for (int i = 0; i < enumClass.getEnumConstants().length; i++) {
            names[i] = enumClass.getEnumConstants()[i].getName();
        }
        return String.join(", ", names);
    }
}
