package commands;

import java.util.HashSet;
import java.util.Optional;

import model.CommandFormat;
import model.StudyGroup;

/**
 * Команда для удаления групп, меньших заданной.
 */
public class RemoveLowerCommand extends Command<StudyGroup, String> {
    public RemoveLowerCommand() {
        super();
        name = CommandFormat.REMOVE_LOW.getName();
    }

    @Override
    public String execute(HashSet<StudyGroup> collection, StudyGroup group) {
        Optional<StudyGroup> firstGroup = collection.stream().filter((StudyGroup gr) -> gr.compareTo(group) < 0).findFirst();
        if (firstGroup.isPresent()) {
            StudyGroup gr = firstGroup.get();
            collection.remove(gr);
            return "Группа " + gr.getName() + " была удалена из коллекции\n";
        }
        return "Группа не найдена";
    }
}
