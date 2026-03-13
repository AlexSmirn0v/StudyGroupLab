package commands;

import java.util.HashSet;
import java.util.Scanner;

import model.StudyGroup;

/**
 * Абстрактный класс для системных команд.
 */
public abstract class SystemCommand extends Command {
    public SystemCommand(Scanner sc) {
        super(sc);
    }

    @Override
    public void execute(HashSet<StudyGroup> collection) {
        // Системные команды обрабатываются в основном цикле, поэтому при запуске execute ничего не происходит.
        return;
    }
    
}
