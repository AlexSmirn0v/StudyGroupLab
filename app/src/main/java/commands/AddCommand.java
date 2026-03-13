package commands;

import java.util.HashSet;
import java.util.Scanner;

import model.StudyGroup;

/**
 * Команда для добавления новой учебной группы в коллекцию.
 */
public class AddCommand extends ElementCommand {
    public AddCommand(Scanner sc) {
        super(sc);
        name = "add";
    }

    @Override
    public void execute(HashSet<StudyGroup> collection) {
        StudyGroup group = askGroup();
        collection.add(group);
        System.out.println("Группа успешно добавлена в коллекцию");
    }
}
