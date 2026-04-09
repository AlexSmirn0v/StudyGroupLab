package model;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.function.BiConsumer;
import java.util.Objects;

import utils.CSVTools;

/**
 * Класс, представляющий учебную группу с её характеристиками.
 */
public class StudyGroup implements Comparable<StudyGroup> {
    private Long id; // Поле не может быть null, Значение поля должно быть больше 0, Значение этого
                     // поля должно быть уникальным, Значение этого поля должно генерироваться
                     // автоматически
    private String name; // Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; // Поле не может быть null
    private LocalDateTime creationDate; // Поле не может быть null, Значение этого поля должно генерироваться
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

    /**
     * Сравнивает текущую учебную группу с другой по семестру, количеству студентов
     * и переведенным студентам.
     * 
     * @param other другая учебная группа для сравнения
     * @return отрицательное число если текущая группа меньше, положительное если
     *         больше, 0 если равны
     */
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

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        StudyGroup that = (StudyGroup) o;
        return id.equals(that.getId());
    }

    /**
     * Преобразует строковое значение в число указанного типа.
     * 
     * @param val          строковое представление числа
     * @param numbClass    класс числа, который нужно получить (Integer, Long,
     *                     Double, Float)
     * @param variableName имя переменной для сообщения об ошибке
     * @return число указанного типа
     * @throws IllegalArgumentException если строка не может быть преобразована в
     *                                  число или тип не поддерживается
     */
    private static <T extends Number> T valueOf(String val, Class<T> numbClass, String variableName)
            throws IllegalArgumentException {
        try {
            if (numbClass == Integer.class) {
                return numbClass.cast(Integer.valueOf(val));
            } else if (numbClass == Long.class) {
                return numbClass.cast(Long.valueOf(val));
            } else if (numbClass == Double.class) {
                return numbClass.cast(Double.valueOf(val));
            } else if (numbClass == Float.class) {
                return numbClass.cast(Float.valueOf(val));
            } else {
                throw new IllegalArgumentException("Неподдерживаемый тип числа: " + numbClass.getSimpleName());
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Неверный формат числа для поля " + variableName);
        }
    }

    /**
     * Устанавливает ID учебной группы. Значение должно быть больше 0, не может
     * быть null и генерируется автоматически в GroupBuilder.
     * 
     * @param id
     * @throws IllegalArgumentException
     */
    void setID(Long id) throws IllegalArgumentException {
        if (id == null)
            throw new IllegalArgumentException("Пустая строка");
        if (id <= 0)
            throw new IllegalArgumentException("Значение поля должно быть больше 0");
        this.id = id;
    }

    /**
     * Устанавливает дату создания учебной группы. Значение не может быть null и
     * генерируется автоматически в GroupBuilder.
     * 
     * @param creationDate
     * @throws IllegalArgumentException
     */
    void setCreationDate(java.time.LocalDateTime creationDate) throws IllegalArgumentException {
        if (creationDate == null)
            throw new IllegalArgumentException("Пустая строка");
        this.creationDate = creationDate;
    }

    /**
     * Устанавливает имя учебной группы.
     * 
     * @param newName новое имя для группы
     * @throws IllegalArgumentException если имя пустое или null
     */
    void setName(String newName) throws IllegalArgumentException {
        if (newName == null || newName.isBlank())
            throw new IllegalArgumentException("Пустая строка");
        this.name = newName;
    }

    /**
     * Устанавливает координаты учебной группы.
     * 
     * @param newCoords описание координат в формате "x;y" или просто "x" (y будет
     *                  установлен в 0)
     * @throws IllegalArgumentException если описание некорректно
     */
    void setCoords(String newCoords) throws IllegalArgumentException {
        if (newCoords == null || newCoords.isBlank())
            throw new IllegalArgumentException("Пустая строка");
        Long x;
        long y = 0;
        if (newCoords.contains(DELIMITER) && newCoords.split(DELIMITER).length > 1
                && !newCoords.split(DELIMITER)[1].isBlank()) {
            x = valueOf(newCoords.split(DELIMITER)[0], Long.class, "Координата x");
            y = valueOf(newCoords.split(DELIMITER)[1], Long.class, "Координата y");
        } else {
            x = valueOf(newCoords.trim(), Long.class, "Координата x");
        }
        this.coordinates = new Coordinates(x, y);
    }

    /**
     * Устанавливает количество студентов в учебной группе. Значение должно быть
     * больше 0, может быть null.
     * 
     * @param studentsCount строка с количеством студентов
     * @throws IllegalArgumentException если строка не может быть преобразована в
     *                                  число или число не больше 0
     */
    void setCount(String studentsCount) throws IllegalArgumentException {
        Long countLong;
        if (studentsCount == null || studentsCount.isBlank()) {
            countLong = null;
        } else
            countLong = valueOf(studentsCount, Long.class, GroupParams.STUDENTS_COUNT.getName());
        if (countLong == null || countLong > 0) {
            this.studentsCount = countLong;
        } else
            throw new IllegalArgumentException("Невозможное число");
    }

    /**
     * Устанавливает количество переведенных студентов в учебной группе. Значение
     * должно быть больше 0 и не может быть null.
     * 
     * @param transferredStudents строка с количеством переведенных студентов
     * @throws IllegalArgumentException если строка не может быть преобразована в
     *                                  число, число не больше 0 или null
     */
    void setTransferred(String transferredStudents) throws IllegalArgumentException {
        if (transferredStudents == null || transferredStudents.isBlank())
            throw new IllegalArgumentException("Пустая строка");
        int transferredInt = valueOf(transferredStudents, Integer.class, GroupParams.TRANSFERRED_STUDENTS.getName());
        if (transferredInt > 0) {
            this.transferredStudents = transferredInt;
        } else {
            throw new IllegalArgumentException("Невозможное число");
        }

    }

    /**
     * Устанавливает среднюю оценку в учебной группе. Значение должно быть больше 0 и не может быть null.
     * 
     * @param averageMark строка со средней оценкой
     * @throws IllegalArgumentException если строка не может быть преобразована в число или число не больше 0
     */
    void setAverage(String averageMark) throws IllegalArgumentException {
        if (averageMark == null || averageMark.isBlank())
            throw new IllegalArgumentException("Пустая строка");
        Integer averageInteger;
        averageInteger = valueOf(averageMark, Integer.class, GroupParams.AVERAGE_MARK.getName());
        if (averageInteger > 0) {
            this.averageMark = averageInteger;
        } else {
            throw new IllegalArgumentException("Невозможное число");
        }
    }

    /**
     * Устанавливает семестр учебной группы. Значение может быть null.
     * 
     * @param semester строка с названием семестра
     * @throws IllegalArgumentException если строка не может быть преобразована в семестр
     */
    void setSemester(String semester) throws IllegalArgumentException {
        this.semesterEnum = Semester.getByName(semester);
    }

    /**
     * Устанавливает администратора учебной группы.
     * 
     * @param persDesription описание администратора в формате
     *                       "имя;рост;паспорт;цвет_волос"
     * @throws IllegalArgumentException если описание некорректно
     */
    void setPerson(String persDesription) throws IllegalArgumentException {
        if (persDesription == null || persDesription.isBlank())
            throw new IllegalArgumentException("Пустая строка");
        String name;
        int height = 0;
        String passportID;
        Color hairColor = null;
        if (!persDesription.contains(DELIMITER) || persDesription.split(DELIMITER).length < 3) {
            throw new IllegalArgumentException("Недостаточное количество элементов");
        }

        String[] parts = persDesription.split(DELIMITER);
        name = parts[0];
        height = valueOf(parts[1], Integer.class, "рост администратора");
        passportID = parts[2];
        if (parts.length > 3 && !parts[3].isBlank())
            hairColor = Color.getByName(parts[3]);
        this.groupAdmin = new Person(name, height, passportID, hairColor);
    }

    /**
     * Преобразует учебную группу в строку формата CSV.
     * 
     * @param delimiter разделитель полей в CSV
     * @return строка в формате CSV
     */
    public String toCSVString(String delimiter) {
        StringBuilder sb = new StringBuilder();
        CSVTools.addToStringBuilder(sb, Objects.toString(id, ""), delimiter);
        CSVTools.addToStringBuilder(sb, Objects.toString(creationDate, ""), delimiter);
        CSVTools.addToStringBuilder(sb, name, delimiter);

        if (coordinates != null) {
            CSVTools.addToStringBuilder(sb, Objects.toString(coordinates.getX(), ""), delimiter);
            CSVTools.addToStringBuilder(sb, Objects.toString(coordinates.getY(), ""), delimiter);
        } else {
            CSVTools.addToStringBuilder(sb, "", delimiter);
            CSVTools.addToStringBuilder(sb, "", delimiter);
        }

        CSVTools.addToStringBuilder(sb, Objects.toString(studentsCount, ""), delimiter);
        CSVTools.addToStringBuilder(sb, Integer.toString(transferredStudents), delimiter);
        CSVTools.addToStringBuilder(sb, Objects.toString(averageMark, ""), delimiter);
        CSVTools.addToStringBuilder(sb, semesterEnum != null ? semesterEnum.getName() : "", delimiter);

        if (groupAdmin != null) {
            CSVTools.addToStringBuilder(sb, groupAdmin.getName(), delimiter);
            CSVTools.addToStringBuilder(sb, Integer.toString(groupAdmin.getHeight()), delimiter);
            CSVTools.addToStringBuilder(sb, groupAdmin.getPassportID(), delimiter);
            CSVTools.addToStringBuilder(sb,
                    groupAdmin.getHairColor() != null ? groupAdmin.getHairColor().getName() : "",
                    delimiter);
        } else
            for (int i = 0; i < 4; i++)
                CSVTools.addToStringBuilder(sb, "", delimiter);
        return sb.toString();
    }

    /**
     * Преобразует StudyGroup в строку для отображения пользователю.
     * 
     * @return строковое представление группы
     */
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

    public LocalDateTime getCreationDate() {
        return creationDate;
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

    /**
     * Редактирует указанный параметр учебной группы.
     * 
     * @param prop параметр для редактирования
     * @param val  новое значение параметра
     * @throws IllegalArgumentException если параметр неизвестен или значение
     *                                  некорректно
     */
    public void edit(GroupParams prop, String val) throws IllegalArgumentException {
        BiConsumer<StudyGroup, String> setter = SETTERS.get(prop);

        if (setter == null)
            throw new IllegalArgumentException("Неизвестное свойство");
        setter.accept(this, val);
    }
}
