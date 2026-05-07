package commands;

import java.util.Collection;

import model.CommandFormat;
import model.StudyGroup;

/**
 * Команда для добавления новой учебной группы в коллекцию.
 */
public class AddCommand extends Command<StudyGroup, String> {
    public AddCommand() {
        super();
        name = CommandFormat.ADD.getName();
    }

    @Override
    public String execute(String username, Collection<StudyGroup> collection, StudyGroup group) {
        if (collection.add(group)) {
            return "Группа успешно добавлена в коллекцию";
        } else {
            return "Не удалось добавить группу в коллекцию";
        }
    }
}
