package commands;

import java.util.Collection;
import java.time.LocalDateTime;

import model.CommandFormat;
import model.StudyGroup;

/**
 * Команда для отображения информации о коллекции.
 */
public class InfoCommand extends Command<Void, String> {
    public InfoCommand() {
        super();
        name = CommandFormat.INFO.getName();
    }

    @Override
    public String execute(String username, Collection<StudyGroup> collection, Void empty) {
        int size = 0;
        long sumParticipants = 0;
        LocalDateTime minCreation = null;

        for (StudyGroup gr : collection) {
            size++;
            Long count = gr.getStudentsCount();
            if (count != null) {
                sumParticipants += count;
            }
            LocalDateTime created = gr.getCreationDate();
            if (created != null && (minCreation == null || created.isBefore(minCreation))) {
                minCreation = created;
            }
        }

        return ("Количество элементов: " + size + "\nОбщее количество студентов: " + sumParticipants
                + "\nДата инициализации коллекции: "
                + (minCreation != null ? minCreation.toString() : "Не установлена"));
    }
}
