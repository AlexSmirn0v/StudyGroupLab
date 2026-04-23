package commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import model.CommandFormat;
import model.StudyGroup;

/**
 * Команда для сортировки коллекции в порядке возрастания.
 */
public class AscendCommand extends Command<Void, List<StudyGroup>> {
    public AscendCommand() {
        super();
        name = CommandFormat.ASCEND.getName();
    }

    @Override
    public List<StudyGroup> execute(HashSet<StudyGroup> collection, Void empty) {
        List<StudyGroup> sortedList = new ArrayList<>(collection);
        Collections.sort(sortedList);
        return sortedList;
    }
}