package model;

public class GroupBuilder {
    private final StudyGroup group = new StudyGroup();
    
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
        return group;
    }
}
