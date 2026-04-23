package commands;

import java.util.HashSet;

import model.CommandFormat;
import model.StudyGroup;

/**
 * Команда для очистки коллекции.
 */
public class ClearCommand extends Command<Void, String>  {
    public ClearCommand() {
        super();
        name = CommandFormat.CLEAR.getName();
    }

    @Override
    public String execute(HashSet<StudyGroup> collection, Void empty) {
        collection.clear();
        return "Коллекция очищена.";
    }
}
