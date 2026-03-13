package commands;

import java.util.Scanner;

/**
 * Команда для выполнения скрипта из файла.
 */
public class ExecuteCommand extends SystemCommand {
    public ExecuteCommand(Scanner sc) {
        super(sc);
        name = "execute_script";
    }
    
}
