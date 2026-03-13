package commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;

import model.StudyGroup;

public abstract class ElementCommand extends Command {
    public ElementCommand(Scanner sc) {
        super(sc);
    }

    protected void askUntilValid(List<String> descriptions, Consumer<String> setter) {
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

    protected List<String> makeDesc(String[] descriptions) {
        List<String> descList = new ArrayList<>();
        for (String desc : descriptions) {
            descList.add("Введите " + desc + ": ");
        }
        return descList;
    }
}
