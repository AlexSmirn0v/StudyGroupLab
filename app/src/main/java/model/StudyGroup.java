package model;

import java.util.EnumMap;
import java.util.function.BiConsumer;
import java.util.UUID;

public class StudyGroup {
    private Long id; // Поле не может быть null, Значение поля должно быть больше 0, Значение этого
                     // поля должно быть уникальным, Значение этого поля должно генерироваться
                     // автоматически
    private String name; // Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; // Поле не может быть null
    private java.time.LocalDateTime creationDate; // Поле не может быть null, Значение этого поля должно генерироваться
                                                  // автоматически
    private Long studentsCount; // Значение поля должно быть больше 0, Поле может быть null
    private int transferredStudents; // Значение поля должно быть больше 0
    private Integer averageMark; // Значение поля должно быть больше 0, Поле не может быть null
    private Semester semesterEnum; // Поле может быть null
    private Person groupAdmin; // Поле может быть null

    private static final EnumMap<GroupParams, BiConsumer<StudyGroup, String>> SETTERS = new EnumMap<>(
            GroupParams.class);
    public static final String DELIMITER = "\n";

    static {
        SETTERS.put(GroupParams.NAME, StudyGroup::setName);
        SETTERS.put(GroupParams.COORDS, StudyGroup::setCoords);
        SETTERS.put(GroupParams.STUDENTS_COUNT, StudyGroup::setCount);
        SETTERS.put(GroupParams.TRANSFERRED_STUDENTS, StudyGroup::setTransferred);
        SETTERS.put(GroupParams.AVERAGE_MARK, StudyGroup::setAverage);
        SETTERS.put(GroupParams.SEMESTER_ENUM, StudyGroup::setSemester);
        SETTERS.put(GroupParams.GROUP_ADMIN, StudyGroup::setPerson);
    }

    StudyGroup() {
        this.id = Integer.toUnsignedLong(Math.abs(UUID.randomUUID().toString().hashCode()));
        this.creationDate = java.time.LocalDateTime.now();
    }

    void setName(String newName) throws IllegalArgumentException {
        if (newName == null || newName.isBlank())
            throw new IllegalArgumentException("Пустая строка");
        this.name = newName;
    }

    void setCoords(String newCoords) throws IllegalArgumentException {
        if (newCoords == null || newCoords.isBlank())
            throw new IllegalArgumentException("Пустая строка");
        Long x;
        long y = 0;
        try {
            if (newCoords.contains(DELIMITER) && newCoords.split(DELIMITER).length > 1
                    && !newCoords.split(DELIMITER)[1].isBlank()) {
                x = Long.valueOf(newCoords.split(DELIMITER)[0]);
                y = Long.valueOf(newCoords.split(DELIMITER)[1]);
                System.out.println(x);
                System.out.println(y);
            } else {
                x = Long.valueOf(newCoords);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Неверный формат числа");
        }
        this.coordinates = new Coordinates(x, y);
    }

    void setCount(String studentsCount) throws IllegalArgumentException {
        Long countLong;
        try {
            countLong = Long.valueOf(studentsCount);
        } catch (NumberFormatException e) {
            if (studentsCount == null || studentsCount.isBlank()) {
                countLong = null;
            } else
                throw new IllegalArgumentException("Неверный формат числа");
        }
        if (countLong == null || countLong > 0) {
            this.studentsCount = countLong;
        } else {
            throw new IllegalArgumentException("Невозможное число");
        }
    }

    void setTransferred(String transferredStudents) throws IllegalArgumentException {
        if (transferredStudents == null || transferredStudents.isBlank())
            throw new IllegalArgumentException("Пустая строка");
        int transferredInt = 0;
        try {
            transferredInt = Integer.valueOf(transferredStudents);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Неверный формат числа");
        }
        if (transferredInt > 0) {
            this.transferredStudents = transferredInt;
        } else {
            throw new IllegalArgumentException("Невозможное число");
        }

    }

    void setAverage(String averageMark) throws IllegalArgumentException {
        if (averageMark == null || averageMark.isBlank())
            throw new IllegalArgumentException("Пустая строка");
        Integer averageInteger;
        try {
            averageInteger = Integer.parseInt(averageMark);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Неверный формат числа");
        }
        if (averageInteger > 0) {
            this.averageMark = averageInteger;
        } else {
            throw new IllegalArgumentException("Невозможное число");
        }
    }

    void setSemester(String semester) throws IllegalArgumentException {
        this.semesterEnum = Semester.getByName(semester);
    }

    void setPerson(String persDesription) throws IllegalArgumentException {
        if (persDesription == null || persDesription.isBlank())
            throw new IllegalArgumentException("Пустая строка");
        String[] params = persDesription.split(DELIMITER);

        //TODO: Закончить метод
    }

    @Override
    public String toString() {
        return "Название: " + name;
    }

    public Long getId() {
        return id;
    }

    public void edit(String prop, String val) throws IllegalArgumentException {
        BiConsumer<StudyGroup, String> setter = SETTERS.get(GroupParams.getByName(prop));

        if (setter == null)
            throw new IllegalArgumentException("Неизвестное свойство");
        setter.accept(this, val);
    }
}
