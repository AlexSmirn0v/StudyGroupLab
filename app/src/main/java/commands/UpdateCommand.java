package commands;

import java.util.HashSet;
import java.util.Scanner;

import model.GroupParams;
import model.StudyGroup;

public class UpdateCommand extends Command {
    public UpdateCommand(Scanner sc) {
        super(sc);
        name = "update";
    }

    @Override
    public void execute(HashSet<StudyGroup> collection) {
        StudyGroup groupToUpdate = null;
        while (true) {
            try {
                Long id = Long.parseLong(getInput(null));
                for (StudyGroup group : collection) {
                    if (group.getId().equals(id)) {
                        groupToUpdate = group;
                        break;
                    }
                }
                groupToUpdate.getId(); // Триггер для NullPointerException, если группа не найдена
                break;
            } catch (NumberFormatException e) {
                System.out.println("Неверный формат числа");
                System.out.println(errorMessage);
            } catch (NullPointerException e) {
                System.out.println("Группы с таким id не найдено");
                System.out.println(errorMessage);
            }
        }

        while (true) {
            try {
                String prop = getInput(
                        "Введите название поля для изменения (" + String.join(", ", GroupParams.getStringItems()) + "): ");
                String val = getInput("Введите новое значение: ");
                groupToUpdate.edit(prop, val);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                System.out.println(errorMessage);
            }
        }
        System.out.println("Свойство поля успешно обновлено");

    }
}
