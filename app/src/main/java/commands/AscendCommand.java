package commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

import model.StudyGroup;

public class AscendCommand extends Command {
    public AscendCommand(Scanner sc) {
        super(sc);
        name = "print_ascending";
    }

    @Override
    public void execute(HashSet<StudyGroup> collection) {
        List<StudyGroup> sortedList = new ArrayList<>(collection);
        Collections.sort(sortedList);
        for (StudyGroup group : sortedList) {
            System.out.println(group.toString());
            System.out.println();
        }
    }
}