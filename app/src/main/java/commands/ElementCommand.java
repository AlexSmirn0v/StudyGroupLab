package commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;

import model.GroupBuilder;
import model.GroupParams;
import model.StudyGroup;

/**
 * Абстрактный класс для команд, работающих с элементами коллекции.
 */
public abstract class ElementCommand extends Command {
    public ElementCommand(Scanner sc) {
        super(sc);
    }

    protected void askUntilValid(List<String> descriptions, Consumer<String> setter) {
        String delimiter = StudyGroup.DELIMITER;
        while (true) {
            try {
                List<String> inputs = descriptions.stream().map(this::getInput).toList();
                setter.accept(String.join(delimiter, inputs));
                return;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                System.out.println(errorMessage);
            }
        }
    }

    protected StudyGroup askGroup() {
        GroupBuilder builder = new GroupBuilder();
        askUntilValid(makeDesc(GroupParams.NAME.getInputAsks()), builder::setName);
        askUntilValid(makeDesc(GroupParams.COORDS.getInputAsks()), builder::setCoords);
        askUntilValid(makeDesc(GroupParams.STUDENTS_COUNT.getInputAsks()), builder::setStudentsCount);
        askUntilValid(makeDesc(GroupParams.TRANSFERRED_STUDENTS.getInputAsks()), builder::setTransferredStudents);
        askUntilValid(makeDesc(GroupParams.AVERAGE_MARK.getInputAsks()), builder::setAverageMark);
        askUntilValid(makeDesc(GroupParams.SEMESTER_ENUM.getInputAsks()), builder::setSemesterEnum);
        askUntilValid(makeDesc(GroupParams.GROUP_ADMIN.getInputAsks()), builder::setGroupAdmin);

        StudyGroup group = builder.build();
        return group;
    }

    protected List<String> makeDesc(String[] descriptions) {
        List<String> descList = new ArrayList<>();
        for (String desc : descriptions) {
            descList.add("Введите " + desc + ": ");
        }
        return descList;
    }
}
