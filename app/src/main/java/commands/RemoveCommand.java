package commands;

import java.util.HashSet;
import java.util.Scanner;

import model.StudyGroup;

public class RemoveCommand extends Command {
    public RemoveCommand(Scanner sc) {
        super(sc);
        name = "remove_by_id";
    }

    @Override
    public void execute(HashSet<StudyGroup> collection) {

    }
    
}
