package commands;

import java.util.HashSet;
import java.util.Scanner;

import model.StudyGroup;

public abstract class Command {
    public String name;
    private String argument = "";
    final Scanner scan;
    final static String errorMessage = "Неверный ввод. Пожалуйста, повторите попытку";

    Command(Scanner sc) {
        scan = sc;
    }

    public void setArgument(String arg) {
        argument = arg == null ? "" : arg.trim();
    }

    protected String popArgument() {
        String result = argument;
        argument = "";
        return result;
    }

    abstract public void execute(HashSet<StudyGroup> collection);

    protected String getInput(String description) {
        if (description != null && !description.isBlank())
            System.out.println(description);
        String res = scan.nextLine().trim();
        return res;
    }
}
