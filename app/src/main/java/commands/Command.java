package commands;

import java.util.HashSet;
import java.util.Scanner;

import model.StudyGroup;

public abstract class Command {
    public String name;
    final Scanner scan;
    final String errorMessage = "Неверный ввод. Пожалуйста, повторите попытку";

    Command(Scanner sc) {
        scan = sc;
    }

    abstract public void execute(HashSet<StudyGroup> collection);

    protected String getInput(String description) {
        if (description != null && !description.isBlank())
            System.out.println(description);
        String res = scan.nextLine().trim();
        return res;
    }

    // int getInt(String description) {
    // System.out.println(description);
    // while (!scan.hasNextInt()) {
    // scan.nextLine();
    // System.out.println(errorMessage);
    // }
    // int res = scan.nextInt();
    // return res;
    // }

    // long getLong(String description) {
    // System.out.println(description);
    // while (!scan.hasNextLong()) {
    // scan.nextLine();
    // System.out.println(errorMessage);
    // }
    // long res = scan.nextLong();
    // return res;
    // }

    // <E extends Enum<E>> E getEnum(String description, Class<E> enumClass) {
    // System.out.println(description);
    // String res = scan.nextLine();
    // try {
    // return Enum.valueOf(enumClass, res);
    // } catch (IllegalArgumentException e) {
    // System.out.println(errorMessage);
    // return getEnum(description, enumClass);
    // }
    // }
}
