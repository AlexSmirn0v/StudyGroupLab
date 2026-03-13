package commands;

import java.util.HashSet;
import java.util.Scanner;

import model.StudyGroup;

public class FilterCommand extends Command {
    public FilterCommand(Scanner sc) {
        super(sc);
        name = "filter_contains_name";
    }

    @Override
    public void execute(HashSet<StudyGroup> collection) {
        String name = popArgument();
        for (StudyGroup group : collection) {
            if (!group.getName().contains(name)) continue;
            System.out.println(group.toString());
            System.out.println();
        }
    }
    
}
