package commands;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Objects;
import java.util.Scanner;
import java.time.LocalDateTime;

import model.StudyGroup;

/**
 * Команда для отображения информации о коллекции.
 */
public class InfoCommand extends Command {
    public InfoCommand(Scanner sc) {
        super(sc);
        name = "info";
    }

    @Override
    public void execute(HashSet<StudyGroup> collection) {
        System.out.println("Количество элементов: " + collection.size());
        
        long sumParticipants = collection.stream()
        .map((StudyGroup gr) -> gr.getStudentsCount())
        .reduce(Long.valueOf(0), (a, b) -> a + b);

        LocalDateTime creationDate = collection.stream()
        .map((StudyGroup gr) -> gr.getCreationDate())
        .filter(Objects::nonNull)
        .min(Comparator.naturalOrder())
        .orElse(null);

        System.out.println("Общее количество студентов: " + sumParticipants);
        System.out.println("Дата инициализации коллекции: " + (creationDate != null ? creationDate.toString() : "Не установлена"));
    }
}
