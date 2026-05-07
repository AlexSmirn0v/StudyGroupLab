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
        String newName = group.getName();
        for (StudyGroup existing : collection) {
            if (existing.getName() != null && existing.getName().equals(newName)) {
                return "Группа с таким именем уже существует";
            }
        }

        try {
            if (collection.add(group)) {
                return "Группа успешно добавлена в коллекцию";
            }
            return "Не удалось добавить группу в коллекцию";
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        } catch (RuntimeException e) {
            return "Не удалось добавить группу: " + e.getMessage();
        }
    }
}
