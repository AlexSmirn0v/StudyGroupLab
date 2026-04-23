package commands;

import java.util.Deque;
import java.util.HashSet;

import model.CommandFormat;
import model.StudyGroup;

/**
 * Команда для отображения истории выполненных команд.
 */
public class HistoryCommand extends Command<Deque<String>, String> {
    public HistoryCommand() {
        super();
        name = CommandFormat.HISTORY.getName();
    }

    @Override
    public String execute(HashSet<StudyGroup> collection, Deque<String> history) {
        String res = "Последние " + history.size() + " команд";
        switch (history.size()) {
            case 1:
                res += "а";
                break;
            case 0:
            case 5:
                break;
            default:
                res += "ы";
        }
        res += ":\n";
        for (String comm : history)
            res += comm + "\n";
        return res;
    }
}
