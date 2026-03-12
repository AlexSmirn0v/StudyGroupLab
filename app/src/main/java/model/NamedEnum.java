package model;

import java.util.Objects;

public interface NamedEnum {
    public String getName();

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

    public static <T extends Enum<T> & NamedEnum> String getStringItems(Class<T> enumClass) {
        String[] names = new String[enumClass.getEnumConstants().length];
        for (int i = 0; i < enumClass.getEnumConstants().length; i++) {
            names[i] = enumClass.getEnumConstants()[i].getName();
        }
        return String.join(", ", names);
    }
}
