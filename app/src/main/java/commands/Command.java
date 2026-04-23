package commands;

import java.io.Serializable;
import java.util.HashSet;

import model.StudyGroup;

/**
 * Абстрактный класс, представляющий команду для выполнения операций над коллекцией учебных групп.
 */
public abstract class Command<In extends Object, Out extends Object> implements Serializable {
    public String name;
    final static String errorMessage = "Неверный ввод. Пожалуйста, повторите попытку";

    /**
     * Выполняет команду.
     * @param collection коллекция учебных групп
     */
    abstract public Out execute(HashSet<StudyGroup> collection, In payload);
}
