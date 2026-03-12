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
        Scanner sc = new Scanner(System.in);
        commandsMap = listCommands(sc);
        System.out.println("Hello, world!");

        while (keepRunning) {
            Command command = commandsMap.get(sc.next());
            if (command == null) {
                System.out.println("Команда не распознана. Введите 'help' для получения списка доступных команд.");
                continue;
            }

            command.execute(groupSet);
            switch (command.name) {
                case "exit":
                    keepRunning = false;
                    break;
                case "history":
                    for (String comm : history)
                        System.out.println(comm);
                    break;
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
                new ExitCommand(sc),
                new HistoryCommand(sc)
        };
        for (Command comm : comms)
            commHashMap.put(comm.name, comm);
        return commHashMap;
    }
}
