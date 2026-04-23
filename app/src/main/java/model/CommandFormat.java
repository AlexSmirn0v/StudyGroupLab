package model;

import java.util.HashSet;

public enum CommandFormat implements NamedEnum {
    ADD("add", StudyGroup.class, String.class),
    ADD_MIN("add_min", StudyGroup.class, String.class),
    ASCEND("print_ascending", HashSet.class),
    CLEAR("clear", String.class),
    EXECUTE("execute", Void.class),
    EXIT("exit", Void.class),
    FILTER("filter_contains_name", String.class, HashSet.class),
    HELP("help", String.class),
    HISTORY("history", String.class),
    INFO("info", String.class),
    MAX_SEM("max_by_semester_enum", StudyGroup.class),
    REMOVE("remove_by_id", Long.class, String.class),
    REMOVE_LOW("remove_lower", StudyGroup.class, String.class),
    SHOW("show", HashSet.class),
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

    public Class<?> getReqClass() {
        return this.reqClass;
    }

    public Class<?> getRespClass() {
        return this.respClass;
    }

    public boolean hasRequest() {
        return this.reqClass != Void.class;
    }

    public boolean hasResponse() {
        return this.respClass != Void.class;
    }

    public static CommandFormat getByName(String name) throws IllegalArgumentException {
        return NamedEnum.getByName(CommandFormat.class, name);
    }

    public static String getStringItems() {
        return NamedEnum.getStringItems(CommandFormat.class);
    }
}