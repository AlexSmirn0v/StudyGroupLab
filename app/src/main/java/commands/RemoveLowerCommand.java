package commands;

import java.util.HashSet;
import java.util.Scanner;

import model.StudyGroup;

/**
 * Команда для удаления групп, меньших заданной.
 */
public class RemoveLowerCommand extends ElementCommand {
    public RemoveLowerCommand(Scanner sc) {
        super(sc);
        name = "remove_lower";
    }

    @Override
    public void execute(HashSet<StudyGroup> collection) {
        StudyGroup group = askGroup();

        for (StudyGroup g : collection) {
            if (g.compareTo(group) < 0) {
                collection.remove(g);
                System.out.println("Группа " + g.getName() + " была удалена из коллекции");
            }
        }
    }
}
