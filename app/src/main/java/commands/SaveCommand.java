package commands;

import java.util.Scanner;

/**
 * Команда для сохранения коллекции в файл.
 */
public class SaveCommand extends SystemCommand {
    public SaveCommand(Scanner sc) {
        super(sc);
        name = "save";
    }
}
