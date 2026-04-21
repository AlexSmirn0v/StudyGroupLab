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

        collection.stream().filter((StudyGroup gr) -> gr.compareTo(group) < 0).forEach(gr -> {
            collection.remove(gr);
            System.out.println("Группа " + gr.getName() + " была удалена из коллекции");
        });
    }
}
