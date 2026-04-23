package commands;

import java.util.HashSet;

import model.CommandFormat;
import model.StudyGroup;

/**
 * Команда для добавления новой учебной группы, если она минимальная.
 */
public class AddMinCommand extends Command<StudyGroup, String> {
    public AddMinCommand() {
        name = CommandFormat.ADD_MIN.getName();
    }

    @Override
    public String execute(HashSet<StudyGroup> collection, StudyGroup group) {
        boolean shouldAdd = collection.stream().allMatch(x -> group.compareTo(x) < 0);
        if (shouldAdd) {
            collection.add(group);
            return "Группа успешно добавлена в коллекцию";
        } else {
            return "Группа не была добавлена, так как она не является минимальной";
        }
    }
}
