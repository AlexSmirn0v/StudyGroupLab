package model;

import java.util.EnumMap;
import java.util.function.BiConsumer;
import java.util.UUID;

public class StudyGroup implements Comparable<StudyGroup> {
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

    @Override
    public int compareTo(StudyGroup other) {
        if (this.semesterEnum == null && other.semesterEnum == null) {
            return 0;
        } else if (this.semesterEnum == null) {
            return -1;
        } else if (other.semesterEnum == null) {
            return 1;
        } else if (this.semesterEnum.compareTo(other.semesterEnum) != 0) {
            return this.semesterEnum.compareTo(other.semesterEnum);
        }

        if (this.studentsCount == null && other.studentsCount == null) {
            return 0;
        } else if (this.studentsCount == null) {
            return -1;
        } else if (other.studentsCount == null) {
            return 1;
        } else if (this.studentsCount.compareTo(other.studentsCount) != 0) {
            return this.studentsCount.compareTo(other.studentsCount);
        }

        return this.transferredStudents - other.transferredStudents;
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
        String name;
        int height = 0;
        String passportID;
        Color hairColor = null;
        try {
            if (!persDesription.contains(DELIMITER) || persDesription.split(DELIMITER).length < 3) {
                throw new IllegalArgumentException("Недостаточное количество элементов");
            }

            String[] parts = persDesription.split(DELIMITER);
            name = parts[0];
            height = Integer.valueOf(parts[1]);
            passportID = parts[2];
            if (parts.length > 3 && !parts[3].isBlank())
                hairColor = Color.getByName(parts[3]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Неверный формат числа");
        }
        this.groupAdmin = new Person(name, height, passportID, hairColor);
    }

    @Override
    public String toString() {
        String res = "";
        res += "ID: " + id + "\n";
        res += "Дата создания: " + creationDate.toString() + "\n";
        res += GroupParams.NAME.getName() + ": " + name + "\n";
        if (coordinates != null)
            res += GroupParams.COORDS.getName() + ": " + coordinates.toString() + "\n";
        if (studentsCount != null)
            res += GroupParams.STUDENTS_COUNT.getName() + ": " + studentsCount + "\n";
        res += GroupParams.TRANSFERRED_STUDENTS.getName() + ": " + transferredStudents + "\n";
        res += GroupParams.AVERAGE_MARK.getName() + ": " + averageMark + "\n";
        if (semesterEnum != null)
            res += GroupParams.SEMESTER_ENUM.getName() + ": " + semesterEnum.getName() + "\n";
        if (groupAdmin != null)
            res += GroupParams.GROUP_ADMIN.getName() + ": " + groupAdmin.toString();
        return res;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Semester getSemesterEnum() {
        return semesterEnum;
    }

    public Long getStudentsCount() {
        return studentsCount;
    }

    public int getTransferredStudents() {
        return transferredStudents;
    }

    public void edit(GroupParams prop, String val) throws IllegalArgumentException {
        BiConsumer<StudyGroup, String> setter = SETTERS.get(prop);

        if (setter == null)
            throw new IllegalArgumentException("Неизвестное свойство");
        setter.accept(this, val);
    }
}
