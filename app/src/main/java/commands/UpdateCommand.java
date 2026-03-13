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
                String arg = popArgument();
                Long id = Long.parseLong(arg.isBlank() ? getInput(null) : arg);
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
                GroupParams param = GroupParams.getByName(prop);
                String[] inputAsks = param.getInputAsks();
                String[] val = new String[inputAsks.length];
                for (int i = 0; i < inputAsks.length; i++) {
                    val[i] = getInput("Введите " + inputAsks[i] + ": ");
                }
                groupToUpdate.edit(param, String.join(StudyGroup.DELIMITER, val));
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                System.out.println(errorMessage);
            }
        }
        System.out.println("Свойство поля успешно обновлено");

    }
}
