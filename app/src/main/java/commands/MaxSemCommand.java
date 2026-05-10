package commands;

import java.util.Comparator;
import java.util.Collection;

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
    public String execute(String username, Collection<StudyGroup> collection, Void empty) {
        StudyGroup maxGroup = collection.stream()
                .max(Comparator.comparing(StudyGroup::getSemesterEnum,
                        Comparator.nullsFirst(Comparator.naturalOrder())).reversed())
                .orElse(null);

        if (maxGroup != null) {
            return maxGroup.toString();
        } else {
            return "Коллекция пуста";
        }
    }
}