package commands;

import java.util.HashSet;
import java.util.Scanner;

import model.StudyGroup;

public class ClearCommand extends Command {
    public ClearCommand(Scanner sc) {
        super(sc);
        name = "clear";
    }

    @Override
    public void execute(HashSet<StudyGroup> collection) {
        collection.clear();
        System.out.println("Коллекция очищена.");
    }
}
