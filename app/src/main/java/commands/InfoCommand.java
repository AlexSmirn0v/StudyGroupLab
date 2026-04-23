package commands;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Objects;
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
    public String execute(HashSet<StudyGroup> collection, Void empty) {
        System.out.println("Количество элементов: " + collection.size());

        long sumParticipants = collection.stream()
                .map((StudyGroup gr) -> gr.getStudentsCount())
                .reduce(Long.valueOf(0), (a, b) -> a + b);

        LocalDateTime creationDate = collection.stream()
                .map((StudyGroup gr) -> gr.getCreationDate())
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder())
                .orElse(null);

        return ("Общее количество студентов: " + sumParticipants + "\nДата инициализации коллекции: "
                + (creationDate != null ? creationDate.toString() : "Не установлена"));
    }
}
