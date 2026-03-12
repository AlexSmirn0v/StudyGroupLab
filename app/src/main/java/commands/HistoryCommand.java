package commands;

import java.util.HashSet;
import java.util.Scanner;

public class HistoryCommand extends Command {
    public HistoryCommand(Scanner sc) {
        super(sc);
        name = "history";
    }

    @Override
    public void execute(HashSet<model.StudyGroup> collection) {
        System.out.println("Последние 5 команд:");
    }
    
}
