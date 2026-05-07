package commands;

import java.util.Collection;

import model.CommandFormat;
import model.StudyGroup;
import server.db.DBCollection;

/**
 * Команда для удаления группы по ID.
 */
public class RemoveCommand extends Command<Long, String> {
    public RemoveCommand() {
        super();
        name = CommandFormat.REMOVE.getName();
    }

    @Override
    public String execute(String username, Collection<StudyGroup> collection, Long id) {
        try {
            for (StudyGroup group : collection) {
                if (group.getId().equals(id)) {
                    if (collection instanceof DBCollection dbCollection) {
                        dbCollection.remove(group, username);
                    } else {
                        collection.remove(group);
                    }
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
