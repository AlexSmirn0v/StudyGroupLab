package commands;

import java.util.HashSet;
import java.util.Scanner;

import model.StudyGroup;

/**
 * Команда для добавления новой учебной группы, если она минимальная.
 */
public class AddMinCommand extends ElementCommand {
    public AddMinCommand(Scanner sc) {
        super(sc);
        name = "add_if_min";
    }

    @Override
    public void execute(HashSet<StudyGroup> collection) {
        StudyGroup group = askGroup();
        boolean shouldAdd = true;
        for (StudyGroup existingGroup : collection) {
            if (group.compareTo(existingGroup) >= 0) {
                shouldAdd = false;
                break;
            }
        }
        if (shouldAdd) {
            collection.add(group);
            System.out.println("Группа успешно добавлена в коллекцию");
        } else {
            System.out.println("Группа не была добавлена, так как она не является минимальной");
        }
    }
}
