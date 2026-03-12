package commands;

import java.util.HashSet;
import java.util.Scanner;

public class ExitCommand extends Command {
    public ExitCommand(Scanner sc) {
        super(sc);
        name = "exit";
    }

    @Override
    public void execute(HashSet<model.StudyGroup> collection) {
        System.out.println("Завершение программы без сохранения. Спасибо за работу!");
    }
    
}
