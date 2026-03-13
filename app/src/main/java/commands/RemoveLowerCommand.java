package commands;

import java.util.HashSet;
import java.util.Scanner;

import model.GroupBuilder;
import model.GroupParams;
import model.StudyGroup;

public class RemoveLowerCommand extends ElementCommand {
    public RemoveLowerCommand(Scanner sc) {
        super(sc);
        name = "remove_lower";
    }

    @Override
    public void execute(HashSet<StudyGroup> collection) {
        StudyGroup group = askGroup();
        collection.add(group);
        System.out.println("Группа успешно добавлена в коллекцию");
    }
}
