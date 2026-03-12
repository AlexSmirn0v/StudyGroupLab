package model;

public class Person {
    private String name; // Поле не может быть null, Строка не может быть пустой
    private int height; // Значение поля должно быть больше 0
    private String passportID; // Поле не может быть null
    private Color hairColor; // Поле может быть null

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
}
