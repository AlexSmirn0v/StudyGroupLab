package commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;

import model.GroupBuilder;
import model.GroupParams;
import model.StudyGroup;

/**
 * Абстрактный класс для команд, работающих с элементами коллекции.
 */
public abstract class ElementCommand extends Command {
    public ElementCommand(Scanner sc) {
        super(sc);
    }
}
