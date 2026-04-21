package model;

public enum CommandName implements NamedEnum {
    ADD("add"),
    ADD_MIN("add_min"),
    ASCEND("print_ascending"),
    CLEAR("clear"),
    EXECUTE("execute"),
    EXIT("exit"),
    FILTER("filter_contains_name"),
    HELP("help"),
    HISTORY("history"),
    INFO("info"),
    MAX_SEM("max_by_semester_enum"),
    REMOVE("remove_by_id"),
    REMOVE_LOW("remove_lower"),
    SHOW("show"),
    UPDATE("update");
    private final String name;

    private CommandName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public static Color getByName(String name) throws IllegalArgumentException {
        return NamedEnum.getByName(Color.class, name);
    }

    public static String getStringItems() {
        return NamedEnum.getStringItems(Color.class);
    }
}
