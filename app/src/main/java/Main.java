import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

import commands.*;
import model.GroupBuilder;
import model.StudyGroup;

/**
 * Главный класс приложения для управления коллекцией учебных групп.
 */
public class Main {
    static final String ENV_VAR = "GROUPS_FILE";
    static final String CSV_DELIMITER = ";";

    static HashMap<String, Command> commandsMap = new HashMap<>();
    static Deque<String> scriptHistory = new ArrayDeque<>();
    static boolean keepRunning = true;
    static boolean insideFile = false;
    static Deque<String> history = new ArrayDeque<>() {
        private final int maxSize = 5;

        @Override
        public boolean add(String s) {
            if (this.size() == maxSize)
                this.pollFirst();
            return super.add(s);
        }
    };

    private static HashSet<StudyGroup> loadCollection() {
        HashSet<StudyGroup> collection = new HashSet<>();
        String filename = System.getenv(ENV_VAR);
        try (Scanner fileScanner = new Scanner(new BufferedInputStream(new FileInputStream(filename)))) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine().trim();
                if (!line.isEmpty()) {
                    try {
                        StudyGroup group = new GroupBuilder().fromCSVString(line, CSV_DELIMITER).build();
                        collection.add(group);
                    } catch (IllegalArgumentException e) {
                        System.out.println("Ошибка при загрузке строки: " + e.getMessage());
                    }
                }
            }
        } catch (FileNotFoundException | NullPointerException e) {
            System.out.println("Файл не найден или не может быть открыт: " + filename);
        }
        return collection;
    }

    private static void saveCollection(HashSet<StudyGroup> collection, String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, StandardCharsets.UTF_8))) {
            for (StudyGroup group : collection) {
                writer.write(group.toCSVString(CSV_DELIMITER));
                writer.newLine();
            }
            writer.flush();
            System.out.println("Коллекция сохранена в файл " + filename);
        } catch (FileNotFoundException e) {
            System.out.println("Файл не найден или не может быть открыт для записи: " + filename);
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении коллекции");
        }
    }

    public static void main(String[] args) {
        HashSet<StudyGroup> groupSet = loadCollection();

        Charset consoleCharset = (System.console() != null)
                ? System.console().charset()
                : StandardCharsets.UTF_8;

        Scanner sc = new Scanner(System.in, consoleCharset);
        commandsMap = listCommands(sc);
        System.out.println("Добро пожаловать! Введите 'help' для получения списка доступных команд.");
        while (keepRunning) {
            System.out.print("> ");
            if (!sc.hasNextLine())
                break;

            String line = sc.nextLine().trim();
            if (line.isEmpty())
                continue;
            String[] parts = line.split("\\s+", 2);
            Command command = commandsMap.get(parts[0]);
            if (command == null) {
                System.out.println("Команда не распознана. Введите 'help' для получения списка доступных команд.");
                continue;
            }
            String argument = (parts.length > 1) ? parts[1].trim() : "";
            command.setArgument(argument);
            switch (command.name) {
                case "exit":
                    if (insideFile) {
                        sc = new Scanner(System.in, consoleCharset);
                        insideFile = false;
                        System.out.println("Завершение выполнения скрипта. Возвращение к консольному режиму.");
                    } else {
                        keepRunning = false;
                        System.out.println("Завершение программы без сохранения");
                    }
                    break;
                case "history":
                    System.out.print("Последние " + history.size() + " команд");
                    switch (history.size()) {
                        case 1:
                            System.out.print("а");
                            break;
                        case 0:
                        case 5:
                            break;
                        default:
                            System.out.print("ы");
                    }
                    System.out.println(": ");
                    for (String comm : history)
                        System.out.println(comm);
                    break;
                case "execute_script":
                    try {
                        if (scriptHistory.contains(argument)) {
                            System.out.println("Обнаружена рекурсия при выполнении скрипта: " + argument);
                            break;
                        }
                        scriptHistory.add(argument);
                        sc = new Scanner(new BufferedInputStream(new FileInputStream(argument)));
                        insideFile = true;
                    } catch (FileNotFoundException e) {
                        System.out.println("Файл не найден или не может быть открыт: " + argument);
                    }
                    break;
                case "save":
                    saveCollection(groupSet, argument);
                    break;
                default:
                    command.execute(groupSet);
            }
            history.add(command.name);
        }
    }

    public static HashMap<String, Command> listCommands(Scanner sc) {
        HashMap<String, Command> commHashMap = new HashMap<>();
        Command[] comms = {
                new HelpCommand(sc),
                new InfoCommand(sc),
                new ShowCommand(sc),
                new AddCommand(sc),
                new UpdateCommand(sc),
                new RemoveCommand(sc),
                new ClearCommand(sc),
                new SaveCommand(sc),
                new ExecuteCommand(sc),
                new ExitCommand(sc),
                new AddMinCommand(sc),
                new RemoveLowerCommand(sc),
                new HistoryCommand(sc),
                new MaxSemCommand(sc),
                new FilterCommand(sc),
                new AscendCommand(sc)
        };
        for (Command comm : comms)
            commHashMap.put(comm.name, comm);
        return commHashMap;
    }
}
