package commands;

import java.util.Scanner;

/**
 * Команда для отображения истории выполненных команд.
 */
public class HistoryCommand extends SystemCommand {
    public HistoryCommand(Scanner sc) {
        super(sc);
        name = "history";
    }
}
