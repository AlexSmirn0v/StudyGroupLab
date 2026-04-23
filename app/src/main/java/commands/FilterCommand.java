package commands;

import java.util.List;
import java.util.Comparator;
import java.util.HashSet;

import model.CommandFormat;
import model.StudyGroup;

/**
 * Команда для фильтрации элементов коллекции по имени.
 */
public class FilterCommand extends Command<String, List<StudyGroup>> {
    public FilterCommand() {
        super();
        name = CommandFormat.FILTER.getName();
    }

    @Override
    public List<StudyGroup> execute(HashSet<StudyGroup> collection, String name) {
        List<StudyGroup> res = collection.stream().filter((StudyGroup group) -> group.getName().contains(name))
                .sorted(Comparator.comparingLong(StudyGroup::getSerializedSize)).toList();
        return res;
    }

}
