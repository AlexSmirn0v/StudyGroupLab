package commands;

import java.util.Comparator;
import java.util.Collection;
import java.util.List;

import model.CommandFormat;
import model.StudyGroup;

/**
 * Команда для отображения группы с максимальным семестром.
 */
public class MaxSemCommand extends Command<Void, String> {
    public MaxSemCommand() {
        super();
        name = CommandFormat.MAX_SEM.getName();
    }

    @Override
    public String execute(Collection<StudyGroup> collection, Void empty) {
        List<StudyGroup> sortedList = collection.stream()
        .sorted(Comparator.comparing(StudyGroup::getSemesterEnum).reversed()).toList();
        
        try {
            return sortedList.get(0).toString();
        } catch (IndexOutOfBoundsException e) {
            return "Коллекция пуста";
        }
    }
}