package commands;

import java.util.HashSet;
import java.util.Scanner;

import model.StudyGroup;

/**
 * Команда для удаления группы по ID.
 */
public class RemoveCommand extends Command {
    public RemoveCommand(Scanner sc) {
        super(sc);
        name = "remove_by_id";
    }

    @Override
    public void execute(HashSet<StudyGroup> collection) {
        try {
            String arg = popArgument();
            Long id = Long.parseLong(arg);
            for (StudyGroup group : collection) {
                if (group.getId().equals(id)) {
                    collection.remove(group);
                    System.out.println("Группа успешно удалена");
                    return;
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Неверный формат числа");
        } catch (NullPointerException e) {
            System.out.println("Группы с таким id не найдено");
        }
    }

}
