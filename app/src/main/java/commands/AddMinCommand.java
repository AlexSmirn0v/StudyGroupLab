package commands;

import java.util.Collection;

import model.CommandFormat;
import model.StudyGroup;

/**
 * Команда для добавления новой учебной группы, если она минимальная.
 */
public class AddMinCommand extends AddCommand {
    public AddMinCommand() {
        super();
        name = CommandFormat.ADD_MIN.getName();
    }

    @Override
    public String execute(String username, Collection<StudyGroup> collection, StudyGroup group) {
        boolean shouldAdd = collection.stream().allMatch(x -> group.compareTo(x) < 0);
        if (shouldAdd) {
            return super.execute(username, collection, group);
        } else {
            return "Группа не была добавлена, так как она не является минимальной";
        }
    }
}
