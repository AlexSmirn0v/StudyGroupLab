package commands;

import java.util.Collection;

import model.GroupParams;
import model.StudyGroup;
import model.UpdateRequest;
import server.db.DBCollection;

/**
 * Команда для обновления элемента коллекции по ID.
 */
public class UpdateCommand extends Command<UpdateRequest, String> {
    public UpdateCommand() {
        super();
        name = "update";
    }

    @Override
    public String execute(String username, Collection<StudyGroup> collection, UpdateRequest upd) {
        StudyGroup groupToUpdate = null;
        while (true) {
            try {
                Long id = upd.id();
                for (StudyGroup group : collection) {
                    if (group.getId().equals(id)) {
                        groupToUpdate = group;
                        break;
                    }
                }
                groupToUpdate.getId(); // Триггер для NullPointerException, если группа не найдена
                break;
            } catch (NumberFormatException e) {
                return "Неверный формат числа\n" + errorMessage;
            } catch (NullPointerException e) {
                return "Группы с таким id не найдено\n" + errorMessage;
            }
        }

        while (true) {
            try {
                GroupParams param = upd.parameter();
                String value = upd.value();
                if (collection instanceof DBCollection dbCollection) {
                    if (!dbCollection.update(groupToUpdate, param, value, username))
                        throw new IllegalArgumentException("Ошибка в параметре или значении для базы данных");
                } else {
                    groupToUpdate.edit(param, value);
                }
                break;
            } catch (IllegalArgumentException e) {
                return e.getMessage() + "\n" + errorMessage;
            } catch (IllegalCallerException e) {
                return e.getMessage() + "\n" + errorMessage;
            }
        }
        return "Свойство поля успешно обновлено";

    }
}
