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

    /**
     * Ничего не выполняет, так как для системных команд выполнение обрабатывается в основном цикле приложения.
     * @param collection коллекция учебных групп
     */
    @Override
    public void execute(HashSet<StudyGroup> collection) {
        return;
    }
    
}
