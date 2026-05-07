package client;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import model.GroupBuilder;
import model.GroupParams;
import model.StudyGroup;
import model.UpdateRequest;

/**
 * Класс для формирования запросов к серверу.
 */
final public class PayloadMaker {
    IOHandler ioHandler;
    final static String errorMessage = "Неверный ввод. Пожалуйста, повторите попытку";

    /**
     * Конструктор для составителя запросов.
     * 
     * @param ioHandler сканер для чтения ввода
     */
    PayloadMaker(IOHandler ioHandler) {
        this.ioHandler = ioHandler;
    }

    /**
     * Получает ввод от пользователя.
     * 
     * @param description описание запрашиваемого параметра
     * @return введенная строка
     */
    public String getInput(String description) {
        if (description != null && !description.isBlank())
            ioHandler.println(description);
        String res = ioHandler.readLine().trim();
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
                ioHandler.println(e.getMessage());
                ioHandler.println(errorMessage);
            }
        }
    }

    /**
     * Запрашивает у пользователя данные для обновления элемента коллекции.
     * 
     * @param arg
     * @return объект UpdateRequest с данными для обновления элемента
     */
    public UpdateRequest askUpdate(String arg) {
        Long id;
        GroupParams param;
        String values;
        while (true) {
            try {
                id = Long.parseLong(arg.isBlank() ? getInput(null) : arg);
                break;
            } catch (NumberFormatException e) {
                ioHandler.println("Неверный формат числа");
                ioHandler.println(errorMessage);
            }
        }

        while (true) {
            try {
                String prop = getInput(
                        "Введите название поля для изменения (" + String.join(", ", GroupParams.getStringItems())
                                + "): ");
                param = GroupParams.getByName(prop);
                String[] inputAsks = param.getInputAsks();
                String[] val = new String[inputAsks.length];
                for (int i = 0; i < inputAsks.length; i++) {
                    val[i] = getInput("Введите " + inputAsks[i] + ": ");
                }
                values = String.join(StudyGroup.DELIMITER, val);
                break;
            } catch (IllegalArgumentException e) {
                ioHandler.println(e.getMessage());
                ioHandler.println(errorMessage);
            }
        }
        return new UpdateRequest(id, param, values);
    }

    /**
     * Запрашивает у пользователя данные для создания нового элемента коллекции.
     * 
     * @return объект StudyGroup с данными нового элемента
     */
    public StudyGroup askGroup(String authorName) {
        GroupBuilder builder = new GroupBuilder();
        builder.setAuthor(authorName);
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
