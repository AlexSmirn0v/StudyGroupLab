package commands;

import java.util.Collection;

import model.CommandFormat;
import model.StudyGroup;
import server.db.DBCollection;

/**
 * Команда для очистки коллекции.
 */
public class ClearCommand extends Command<Void, String>  {
    public ClearCommand() {
        super();
        name = CommandFormat.CLEAR.getName();
    }

    @Override
    public String execute(String username, Collection<StudyGroup> collection, Void empty) {
        if (collection instanceof DBCollection dbCollection) {
            dbCollection.clear(username);
        } else {
            collection.clear();
        }
        return "Ваши элементы в коллекции очищены.";
    }
}
