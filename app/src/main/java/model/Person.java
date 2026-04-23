package model;

import java.io.Serializable;

/**
 * Класс, представляющий человека (администратора группы).
 */
public class Person implements Serializable {
    private String name; // Поле не может быть null, Строка не может быть пустой
    private int height; // Значение поля должно быть больше 0
    private String passportID; // Поле не может быть null
    private Color hairColor; // Поле может быть null

    /**
     * Создает объект человека.
     * @param name имя человека (не может быть null или пустым)
     * @param height рост человека (должен быть больше 0)
     * @param passportID ID паспорта (не может быть null)
     * @param hairColor цвет волос (может быть null)
     * @throws IllegalArgumentException если параметры некорректны
     */
    Person(String name, int height, String passportID, Color hairColor)
            throws IllegalArgumentException {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Имя не может быть пустым");
        if (height <= 0)
            throw new IllegalArgumentException("Рост должен быть больше 0");
        if (passportID == null || passportID.isBlank())
            throw new IllegalArgumentException("ID паспорта не может быть пустым");
        
        this.name = name;
        this.height = height;
        this.passportID = passportID;
        this.hairColor = hairColor;
    }

    String getName() {
        return name;
    }

    int getHeight() {
        return height;
    }

    String getPassportID() {
        return passportID;
    }

    Color getHairColor() {
        return hairColor;
    }

    /**
     * Возвращает объект человека в виде строки.
     * @return строковое представление с именем, ростом, ID паспорта и цветом волос
     */
    @Override
    public String toString() {
        String res = "";
        res += "Имя: " + name + "\n";
        res += "Рост: " + height + "\n";
        res += "ID паспорта: " + passportID + "\n";
        res += "Цвет волос: " + (hairColor != null ? hairColor.getName() : "не указан");
        return res;
    }

}
