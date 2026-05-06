package model;

import java.util.List;

/**
 * Перечисление форматов команд для клиента с названиями и типами ввода-вывода.
 */
public enum CommandFormat implements NamedEnum {
    ADD("add", StudyGroup.class, String.class),
    ADD_MIN("add_min", StudyGroup.class, String.class),
    ASCEND("print_ascending", List.class),
    CLEAR("clear", String.class),
    EXECUTE("execute", Void.class),
    EXIT("exit", Void.class),
    FILTER("filter_contains_name", String.class, List.class),
    HELP("help", String.class),
    HISTORY("history", String.class),
    INFO("info", String.class),
    MAX_SEM("max_by_semester_enum", String.class),
    REMOVE("remove_by_id", Long.class, String.class),
    REMOVE_LOW("remove_lower", StudyGroup.class, String.class),
    SHOW("show", List.class),
    UPDATE("update", UpdateRequest.class, String.class);

    private final String name;
    private final Class<?> reqClass;
    private final Class<?> respClass;

    private CommandFormat(String name, Class<?> reqClass, Class<?> respClass) {
        this.name = name;
        this.reqClass = reqClass;
        this.respClass = respClass;
    }

    private CommandFormat(String name, Class<?> respClass) {
        this(name, Void.class, respClass);
    }

    @Override
    public String getName() {
        return this.name;
    }

    /**
     * Возвращает класс ожидаемых параметров запроса команды.
     *
     * @return класс запроса или Void.class, если запрос не требуется
     */
    public Class<?> getReqClass() {
        return this.reqClass;
    }

    /**
     * Возвращает класс ответа команды.
     *
     * @return класс ответа или Void.class, если ответ не формируется
     */
    public Class<?> getRespClass() {
        return this.respClass;
    }

    /**
     * Проверяет, требуется ли команде объект запроса.
     *
     * @return true, если команда принимает запрос
     */
    public boolean hasRequest() {
        return this.reqClass != Void.class;
    }

    /**
     * Проверяет, возвращает ли команда ответ.
     *
     * @return true, если команда формирует ответ
     */
    public boolean hasResponse() {
        return this.respClass != Void.class;
    }

    /**
     * Получает элемент перечисления по его имени.
     *
     * @param name строковое имя команды
     * @return элемент перечисления
     * @throws IllegalArgumentException если имя неверное
     */
    public static CommandFormat getByName(String name) throws IllegalArgumentException {
        return NamedEnum.getByName(CommandFormat.class, name);
    }

    /**
     * Возвращает строку всех доступных имен команд.
     *
     * @return строка с перечислением имен команд
     */
    public static String getStringItems() {
        return NamedEnum.getStringItems(CommandFormat.class);
    }
}