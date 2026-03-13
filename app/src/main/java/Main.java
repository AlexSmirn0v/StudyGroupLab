import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

import commands.*;
import model.StudyGroup;

public class Main {
    static HashSet<StudyGroup> groupSet = new HashSet<>();
    static HashMap<String, Command> commandsMap = new HashMap<>();
    static boolean keepRunning = true;
    static Deque<String> history = new ArrayDeque<>() {
        private final int maxSize = 5;

        @Override
        public boolean add(String s) {
            if (this.size() == maxSize)
                this.pollFirst();
            return super.add(s);
        }
    };

    public static void main(String[] args) {
        Charset consoleCharset = (System.console() != null)
                ? System.console().charset()
                : StandardCharsets.UTF_8;

        Scanner sc = new Scanner(System.in, consoleCharset);
        commandsMap = listCommands(sc);
        System.out.println("Добро пожаловать! Введите 'help' для получения списка доступных команд.");

        while (keepRunning) {
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
            if (parts.length > 1)
                command.setArgument(parts[1]);
            switch (command.name) {
                case "exit":
                    System.out.println("Завершение программы без сохранения");
                    keepRunning = false;
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
                        sc = new Scanner(new BufferedInputStream(new FileInputStream(parts[1])));
                    } catch (FileNotFoundException e) {
                        System.out.println("Файл не найден или не может быть открыт: " + parts[1]);
                    }
                    break;
                case "save":
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
