package commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

import model.StudyGroup;

/**
 * Команда для отображения группы с максимальным семестром.
 */
public class MaxSemCommand extends Command {
    public MaxSemCommand(Scanner sc) {
        super(sc);
        name = "max_by_semester_enum";
    }

    @Override
    public void execute(HashSet<StudyGroup> collection) {
        List<StudyGroup> sortedList = new ArrayList<>(collection);
        Collections.sort(sortedList, Comparator.comparing(StudyGroup::getSemesterEnum).reversed());
        try {
            System.out.println(sortedList.get(0).toString());
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Коллекция пуста");
        }
    }
}