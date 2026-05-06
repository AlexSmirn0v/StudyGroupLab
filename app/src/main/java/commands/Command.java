package commands;

import java.io.Serializable;
import java.util.Collection;

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
     * @param payload данные для выполнения команды
     * @return результат выполнения команды
     */
    abstract public Out execute(Collection<StudyGroup> collection, In payload);
}
