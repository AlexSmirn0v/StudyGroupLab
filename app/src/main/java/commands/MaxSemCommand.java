package commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

import model.StudyGroup;

public class MaxSemCommand extends Command {
    public MaxSemCommand(Scanner sc) {
        super(sc);
        name = "max_by_semester_enum";
    }

    @Override
    public void execute(HashSet<StudyGroup> collection) {
        List<StudyGroup> sortedList = new ArrayList<>(collection);
        Collections.sort(sortedList, Comparator.comparing(StudyGroup::getSemesterEnum).reversed());
        for (StudyGroup group : sortedList) {
            System.out.println(group.toString());
            System.out.println();
        }
    }
}