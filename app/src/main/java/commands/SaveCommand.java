package commands;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;

import model.StudyGroup;

/**
 * Команда для сохранения коллекции в файл.
 */
public class SaveCommand extends Command<String, String> {
    public SaveCommand() {
        super();
        name = "save";
    }

    @Override
    public String execute(HashSet<StudyGroup> collection, String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, StandardCharsets.UTF_8))) {
            for (StudyGroup group : collection) {
                writer.write(group.toCSVString(";"));
                writer.newLine();
            }
            writer.flush();
            return "Коллекция сохранена в файл " + filename;
        } catch (FileNotFoundException | NullPointerException e) {
            return "Файл не найден или не может быть открыт для записи: " + filename;
        } catch (IOException e) {
            return "Ошибка при сохранении коллекции";
        }
    }
}
