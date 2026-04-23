package commands;

import java.util.HashSet;
import java.util.Scanner;

import model.CommandFormat;
import model.StudyGroup;

/**
 * Команда для добавления новой учебной группы, если она минимальная.
 */
public class AddMinCommand extends ElementCommand {
    public AddMinCommand(Scanner sc) {
        super(sc);
        name = CommandFormat.ADD_MIN.getName();
    }

    @Override
    public void execute(HashSet<StudyGroup> collection) {
        StudyGroup group = askGroup();
        boolean shouldAdd = collection.stream().allMatch(x -> group.compareTo(x) < 0);
        if (shouldAdd) {
            collection.add(group);
            System.out.println("Группа успешно добавлена в коллекцию");
        } else {
            System.out.println("Группа не была добавлена, так как она не является минимальной");
        }
    }
}
