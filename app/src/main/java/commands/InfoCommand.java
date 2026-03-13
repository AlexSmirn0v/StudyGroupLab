package commands;

import java.util.HashSet;
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
        int sumParticipants = 0;
        LocalDateTime creationDate = null;

        for (StudyGroup group : collection) {
            sumParticipants += group.getStudentsCount();
            if (creationDate == null || group.getCreationDate().isBefore(creationDate)) {
                creationDate = group.getCreationDate();
            }
        }
        System.out.println("Общее количество студентов: " + sumParticipants);
        System.out.println("Дата инициализации коллекции: " + (creationDate != null ? creationDate.toString() : "Не установлена"));
    }
}
