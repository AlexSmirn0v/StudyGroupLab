package client;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;

import model.GroupBuilder;
import model.GroupParams;
import model.StudyGroup;
import model.UpdateRequest;

final public class PayloadMaker {
    Scanner scan;
    final static String errorMessage = "Неверный ввод. Пожалуйста, повторите попытку";

    PayloadMaker(Scanner scan) {
        this.scan = scan;
    }
    /**
     * Получает ввод от пользователя.
     * 
     * @param description описание запрашиваемого параметра
     * @return введенная строка
     */
    public String getInput(String description) {
        if (description != null && !description.isBlank())
            System.out.println(description);
        String res = scan.nextLine().trim();
        return res;
    }

    private void askUntilValid(List<String> descriptions, Consumer<String> setter) {
        String delimiter = StudyGroup.DELIMITER;
        while (true) {
            try {
                List<String> inputs = descriptions.stream().map(this::getInput).toList();
                setter.accept(String.join(delimiter, inputs));
                return;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                System.out.println(errorMessage);
            }
        }
    }

    public UpdateRequest askUpdate(String arg) {
        Long id; GroupParams param; String values;
        while (true) {
            try {
                id = Long.parseLong(arg.isBlank() ? getInput(null) : arg);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Неверный формат числа");
                System.out.println(errorMessage);
            }
        }

        while (true) {
            try {
                String prop = getInput(
                        "Введите название поля для изменения (" + String.join(", ", GroupParams.getStringItems()) + "): ");
                param = GroupParams.getByName(prop);
                String[] inputAsks = param.getInputAsks();
                String[] val = new String[inputAsks.length];
                for (int i = 0; i < inputAsks.length; i++) {
                    val[i] = getInput("Введите " + inputAsks[i] + ": ");
                }
                values = String.join(StudyGroup.DELIMITER, val);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                System.out.println(errorMessage);
            }
        }
        return new UpdateRequest(id, param, values);
    }

    public StudyGroup askGroup() {
        GroupBuilder builder = new GroupBuilder();
        askUntilValid(makeDesc(GroupParams.NAME.getInputAsks()), builder::setName);
        askUntilValid(makeDesc(GroupParams.COORDS.getInputAsks()), builder::setCoords);
        askUntilValid(makeDesc(GroupParams.STUDENTS_COUNT.getInputAsks()), builder::setStudentsCount);
        askUntilValid(makeDesc(GroupParams.TRANSFERRED_STUDENTS.getInputAsks()), builder::setTransferredStudents);
        askUntilValid(makeDesc(GroupParams.AVERAGE_MARK.getInputAsks()), builder::setAverageMark);
        askUntilValid(makeDesc(GroupParams.SEMESTER_ENUM.getInputAsks()), builder::setSemesterEnum);
        askUntilValid(makeDesc(GroupParams.GROUP_ADMIN.getInputAsks()), builder::setGroupAdmin);

        StudyGroup group = builder.build();
        return group;
    }

    private List<String> makeDesc(String[] descriptions) {
        List<String> descList = new ArrayList<>();
        for (String desc : descriptions) {
            descList.add("Введите " + desc + ": ");
        }
        return descList;
    }
}
