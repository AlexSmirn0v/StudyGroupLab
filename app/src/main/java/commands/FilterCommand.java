package commands;

import java.util.HashSet;
import java.util.Scanner;

import model.StudyGroup;

/**
 * Команда для фильтрации элементов коллекции по имени.
 */
public class FilterCommand extends Command {
    public FilterCommand(Scanner sc) {
        super(sc);
        name = "filter_contains_name";
    }

    @Override
    public void execute(HashSet<StudyGroup> collection) {
        String name = popArgument();
        HashSet<StudyGroup> res = new HashSet<>(
                collection.stream().filter((StudyGroup group) -> group.getName().contains(name)).toList());
        for (StudyGroup group : res) {
            System.out.println(group.toString());
            System.out.println();
        }
    }

}
