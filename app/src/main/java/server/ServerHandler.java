package server;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

import model.CommandFormat;
import model.CommandMessage;
import model.GroupBuilder;
import model.StudyGroup;
import commands.*;

/**
 * Обработчик команд сервера.
 */
public class ServerHandler {
    static final String ENV_VAR = "GROUPS_FILE";
    static final String CSV_DELIMITER = ";";

    static HashMap<String, Command<?, ?>> commandsMap = new HashMap<>();
    static HashSet<StudyGroup> groupSet;
    static Deque<String> history = new ArrayDeque<>() {
        private final int maxSize = 5;

        @Override
        public boolean add(String s) {
            if (this.size() == maxSize)
                this.pollFirst();
            return super.add(s);
        }
    };

    public ServerHandler() {
        groupSet = loadCollection();
        commandsMap = listCommands();
    }

    /**
     * Загружает коллекцию из файла.
     * @return коллекция учебных групп
     */
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
                        ServerLogger.log("Ошибка при загрузке строки: " + e.getMessage());
                    }
                }
            }
        } catch (FileNotFoundException | NullPointerException e) {
            ServerLogger.log("Файл не найден или не может быть открыт: " + filename);
        }
        return collection;
    }

    /**
     * Создает список команд.
     * @return список команд
     */
    public static HashMap<String, Command<?, ?>> listCommands() {
        HashMap<String, Command<?, ?>> commHashMap = new HashMap<>();
        Command<?, ?>[] comms = {
                new HelpCommand(),
                new InfoCommand(),
                new ShowCommand(),
                new AddCommand(),
                new UpdateCommand(),
                new RemoveCommand(),
                new ClearCommand(),
                new SaveCommand(),
                new AddMinCommand(),
                new RemoveLowerCommand(),
                new HistoryCommand(),
                new MaxSemCommand(),
                new FilterCommand(),
                new AscendCommand()
        };
        for (Command<?, ?> comm : comms)
            commHashMap.put(comm.name, comm);
        return commHashMap;
    }

    /**
     * Выполняет команду.
     * @param request запрос команды
     * @return результат выполнения
     */
    Object run(CommandMessage request) {
        CommandFormat commandForm = request.command();
        Command<?, ?> command = commandsMap.get(commandForm.getName());
        if (command == null) {
            return null;
        }

        history.add(commandForm.getName());
        if (commandForm == CommandFormat.HISTORY) {
            return ((HistoryCommand) command).execute(groupSet, history);
        }
        return executeCommand(command, request.getPayload());
    }

    /**
     * Обрабатывает консольный ввод.
     * @param consoleInput ввод с консоли
     * @param status статус сервера (на позиции [0] прерыватель цикла)
     * @return результат обработки
     */
    String runConsole(String consoleInput, boolean[] status) {
        String commandName = consoleInput.trim();
        if (commandName.isEmpty())
            return "Пустая команда";
        switch (commandName) {
            case "exit":
                status[0] = false;
                ServerLogger.log("Завершение работы сервера...");
            case "save":
                String filename = System.getenv(ENV_VAR);
                SaveCommand command = (SaveCommand) commandsMap.get("save");
                return command.execute(groupSet, filename);
            default:
                return "Из консоли сервера поддерживается только команды save и exit";
        }
    }

    /**
     * Выполняет команду.
     * @param command команда
     * @param payload входные данные для команды
     * @return результат
     */
    @SuppressWarnings("unchecked")
    private Object executeCommand(Command<?, ?> command, Object payload) {
        return ((Command<Object, Object>) command).execute(groupSet, payload);
    }
}
