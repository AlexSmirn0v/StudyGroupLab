package commands;

import java.util.Collection;
import java.util.Optional;

import model.CommandFormat;
import model.StudyGroup;
import server.db.DBCollection;

/**
 * Команда для удаления групп, меньших заданной.
 */
public class RemoveLowerCommand extends Command<StudyGroup, String> {
    public RemoveLowerCommand() {
        super();
        name = CommandFormat.REMOVE_LOW.getName();
    }

    @Override
    public String execute(String username, Collection<StudyGroup> collection, StudyGroup group) {
        Optional<StudyGroup> firstGroup = collection.stream().filter((StudyGroup gr) -> gr.compareTo(group) < 0).findFirst();
        if (firstGroup.isPresent()) {
            StudyGroup gr = firstGroup.get();
            if (collection instanceof DBCollection dbCollection) {
                dbCollection.remove(gr, username);
            } else {
                collection.remove(gr);
            }
            return "Группа " + gr.getName() + " была удалена из коллекции\n";
        }
        return "Группа не найдена";
    }
}
