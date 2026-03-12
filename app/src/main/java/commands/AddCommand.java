package commands;

import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;

import model.Color;
import model.GroupBuilder;
import model.Semester;
import model.StudyGroup;

public class AddCommand extends Command {
    public AddCommand(Scanner sc) {
        super(sc);
        name = "add";
    }

    private void askUntilValid(String description, java.util.function.Consumer<String> setter) {
        while (true) {
            try {
                String input = getInput(description);
                setter.accept(input);
                return;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                System.out.println(errorMessage);
            }
        }
    }

    private void askUntilValid(List<String> descriptions, String delimiter, Consumer<String> setter) {
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

    @Override
    public void execute(HashSet<StudyGroup> collection) {
        GroupBuilder builder = new GroupBuilder();
        askUntilValid("Введите название группы: ", builder::setName);
        askUntilValid(List.of("Введите первую координату (x): ", "Введите вторую координату (y): "),
                StudyGroup.DELIMITER, builder::setCoords);
        askUntilValid("Введите количество студентов в группе: ", builder::setStudentsCount);
        askUntilValid("Введите количество переведенных студентов: ", builder::setTransferredStudents);
        askUntilValid("Введите среднюю оценку студентов: ", builder::setAverageMark);
        askUntilValid("Введите номер семестра (возможные варианты: " + Semester.getStringItems() + "): ", builder::setSemesterEnum);
        askUntilValid(
                List.of("Введите имя администратора: ", "Введите рост администратора: ",
                        "Введите ID паспорта администратора: ", "Введите цвет волос администратора (возможные варианты: " + Color.getStringItems() + "): "),
                StudyGroup.DELIMITER, builder::setGroupAdmin);

        StudyGroup group = builder.build();
        collection.add(group);
        System.out.println("Группа успешно добавлена в коллекцию");
    }
}
