package commands;

import java.util.HashSet;

import model.CommandFormat;
import model.StudyGroup;

/**
 * Команда для удаления группы по ID.
 */
public class RemoveCommand extends Command<Long, String> {
    public RemoveCommand() {
        super();
        name = CommandFormat.REMOVE.getName();
    }

    @Override
    public String execute(HashSet<StudyGroup> collection, Long id) {
        try {
            for (StudyGroup group : collection) {
                if (group.getId().equals(id)) {
                    collection.remove(group);
                    return "Группа успешно удалена";
                }
            }
            return "Группы с таким id не найдено";
        } catch (NumberFormatException e) {
            return "Неверный формат числа";
        } catch (NullPointerException e) {
            return "Группы с таким id не найдено";
        }
    }

}
