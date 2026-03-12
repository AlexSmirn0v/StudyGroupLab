package commands;

import java.util.HashSet;
import java.util.Scanner;

import model.StudyGroup;

public class InfoCommand extends Command {
    public InfoCommand(Scanner sc) {
        super(sc);
        name = "info";
    }

    @Override
    public void execute(HashSet<StudyGroup> collection) {
        System.out.println("Количество элементов: " + collection.size());
    }
}
