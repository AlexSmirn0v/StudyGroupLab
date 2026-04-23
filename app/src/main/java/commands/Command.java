package commands;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Scanner;

import model.StudyGroup;

/**
 * Абстрактный класс, представляющий команду для выполнения операций над коллекцией учебных групп.
 */
public abstract class Command implements Serializable {
    public String name;
    private String argument = "";
    final Scanner scan;
    final static String errorMessage = "Неверный ввод. Пожалуйста, повторите попытку";

    /**
     * Создает объект команды.
     * @param sc сканер для чтения ввода
     */
    Command(Scanner sc) {
        scan = sc;
    }

    /**
     * Записывает аргумент команды в локальную переменную.
     * @param arg аргумент команды
     */
    public void setArgument(String arg) {
        argument = arg == null ? "" : arg.trim();
    }

    /**
     * Извлекает и возвращает аргумент команды.
     * @return аргумент команды
     */
    protected String popArgument() {
        String result = argument;
        argument = "";
        return result;
    }

    /**
     * Выполняет команду.
     * @param collection коллекция учебных групп
     */
    abstract public void execute(HashSet<StudyGroup> collection);
}
