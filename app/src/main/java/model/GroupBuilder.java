package model;

import java.util.UUID;

import utils.CSVTools;

/**
 * Builder для создания объектов StudyGroup.
 */
public class GroupBuilder {
    private final StudyGroup group = new StudyGroup();
    
    public GroupBuilder fromCSVString(String csv, String delimiter) throws IllegalArgumentException {
        String[] parts = CSVTools.parseCSVLine(csv, delimiter);
        String newDelim = StudyGroup.DELIMITER;
        if (parts.length < 13) {
            System.out.println(parts.length);
            throw new IllegalArgumentException("Неверный формат CSV строки для StudyGroup");
        }
        try {
            group.setID(Long.valueOf(parts[0].trim()));
            group.setCreationDate(java.time.LocalDateTime.parse(parts[1].trim()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Ошибка при парсинге ID или даты создания: " + e.getMessage(), e);
        }
        setName(parts[2].trim());
        setCoords(parts[3].trim() + newDelim + parts[4].trim());
        setStudentsCount(parts[5].trim());
        setTransferredStudents(parts[6].trim());
        setAverageMark(parts[7].trim());
        setSemesterEnum(parts[8].trim());
        setGroupAdmin(parts[9].trim() + newDelim + parts[10].trim() + newDelim + parts[11].trim() + newDelim + parts[12].trim());
        return this;
    }

    public GroupBuilder setName(String name) throws IllegalArgumentException {
        group.setName(name);
        return this;
    }

    public GroupBuilder setCoords(String coords) throws IllegalArgumentException {
        group.setCoords(coords);
        return this;
    }

    public GroupBuilder setStudentsCount(String studentsCount) throws IllegalArgumentException {
        group.setCount(studentsCount);
        return this;
    }

    public GroupBuilder setTransferredStudents(String transferredStudents) throws IllegalArgumentException {
        group.setTransferred(transferredStudents);
        return this;
    }

    public GroupBuilder setAverageMark(String averageMark) throws IllegalArgumentException {
        group.setAverage(averageMark);
        return this;
    }

    public GroupBuilder setSemesterEnum(String semesterEnum) throws IllegalArgumentException {
        group.setSemester(semesterEnum);
        return this;
    }

    public GroupBuilder setGroupAdmin(String groupAdmin) throws IllegalArgumentException {
        group.setPerson(groupAdmin);
        return this;
    }

    public StudyGroup build() {
        group.setID(Integer.toUnsignedLong(Math.abs(UUID.randomUUID().toString().hashCode())));
        group.setCreationDate(java.time.LocalDateTime.now());
        return group;
    }
}
