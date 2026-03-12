package commands;

import java.util.HashSet;
import java.util.Scanner;

import model.StudyGroup;

public class ShowCommand extends Command {
    public ShowCommand(Scanner sc) {
        super(sc);
        name = "show";
    }

    @Override
    public void execute(HashSet<StudyGroup> collection) {
        for (StudyGroup group : collection) {
            System.out.println(group.toString());
            System.out.println();
        }
    }
}
