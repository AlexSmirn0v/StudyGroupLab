package commands;

import java.util.HashSet;

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
    public String execute(HashSet<StudyGroup> collection, StudyGroup group) {
        collection.add(group);
        return "Группа успешно добавлена в коллекцию";
    }
}
