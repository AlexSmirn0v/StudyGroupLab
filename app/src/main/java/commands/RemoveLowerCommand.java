package commands;

import java.util.HashSet;
import java.util.Scanner;

import model.GroupBuilder;
import model.GroupParams;
import model.StudyGroup;

public class RemoveLowerCommand extends ElementCommand {
    public RemoveLowerCommand(Scanner sc) {
        super(sc);
        name = "remove_lower";
    }

    @Override
    public void execute(HashSet<StudyGroup> collection) {
        GroupBuilder builder = new GroupBuilder();
        askUntilValid(makeDesc(GroupParams.NAME.getInputAsks()), builder::setName);
        askUntilValid(makeDesc(GroupParams.COORDS.getInputAsks()), builder::setCoords);
        askUntilValid(makeDesc(GroupParams.STUDENTS_COUNT.getInputAsks()), builder::setStudentsCount);
        askUntilValid(makeDesc(GroupParams.TRANSFERRED_STUDENTS.getInputAsks()), builder::setTransferredStudents);
        askUntilValid(makeDesc(GroupParams.AVERAGE_MARK.getInputAsks()), builder::setAverageMark);
        askUntilValid(makeDesc(GroupParams.SEMESTER_ENUM.getInputAsks()), builder::setSemesterEnum);
        askUntilValid(makeDesc(GroupParams.GROUP_ADMIN.getInputAsks()), builder::setGroupAdmin);

        StudyGroup group = builder.build();
        collection.add(group);
        System.out.println("Группа успешно добавлена в коллекцию");
    }
}
