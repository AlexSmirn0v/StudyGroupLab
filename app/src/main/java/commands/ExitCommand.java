package commands;

import java.util.Scanner;

/**
 * Команда для выхода из приложения.
 */
public class ExitCommand extends SystemCommand {
    public ExitCommand(Scanner sc) {
        super(sc);
        name = "exit";
    }
}
