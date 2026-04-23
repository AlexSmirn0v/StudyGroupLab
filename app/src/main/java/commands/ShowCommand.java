package commands;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import model.CommandFormat;
import model.StudyGroup;


/**
 * Команда для отображения всех элементов коллекции.
 */
public class ShowCommand extends Command<Void, List<StudyGroup>> {
    public ShowCommand() {
        super();
        name = CommandFormat.SHOW.getName();
    }

    @Override
    public List<StudyGroup> execute(HashSet<StudyGroup> collection, Void empty) {
        return collection.stream().sorted(Comparator.comparingLong(StudyGroup::getSerializedSize)).toList();
    }
}
