package client;

import java.util.function.Consumer;

import client.components.GroupAddFrame;
import model.CommandFormat;
import model.CommandMessage;

public enum ActionCommand {
    ADD("add", ActionCommand::onAddGroup),
    ADD_MIN("add_min", ActionCommand::onAddMin),
    REMOVE("remove_by_id", ActionCommand::onRemoveGroup),
    REMOVE_LOW("remove_lower", ActionCommand::onRemoveLow),
    CLEAR("clear", ActionCommand::onClear),
    INFO("info", ActionCommand::onInfo),
    MAX_SEM("max_by_semester_enum", ActionCommand::onMaxSem),
    HISTORY("history", ActionCommand::onHistory),
    EXECUTE("execute_script", ActionCommand::onExecuteScript);

    private final String command;

    private final Consumer<AppContext> handler;

    ActionCommand(String command, Consumer<AppContext> handler) {
        this.command = command;
        this.handler = handler;
    }

    public String getCommand() {
        return command;
    }

    public Runnable getHandler(AppContext context) {
        return () -> handler.accept(context);
    }

    public CommandFormat getFormat() {
        return CommandFormat.getByName(command);
    }

    private static void onAddGroup(AppContext context) {
        GroupAddFrame addFrame = new GroupAddFrame(context, (group) -> {
            CommandMessage message = new CommandMessage(ADD.getFormat(), group, context.getUsername(), context.getPassword());
            String response = context.getConnectFacade().askServer(message);
            return response;
        });
        addFrame.setVisible(true);
    }

    private static void onAddMin(AppContext context) {
        // Empty business-logic handler.
    }

    private static void onRemoveGroup(AppContext context) {
        // Empty business-logic handler.
    }

    private static void onRemoveLow(AppContext context) {
        // Empty business-logic handler.
    }

    private static void onClear(AppContext context) {
        // Empty business-logic handler.
    }

    private static void onInfo(AppContext context) {
        // Empty business-logic handler.
    }

    private static void onHistory(AppContext context) {
        // Empty business-logic handler.
    }

    private static void onMaxSem(AppContext context) {
        // Empty business-logic handler.
    }

    private static void onExecuteScript(AppContext context) {
        // Empty business-logic handler.
    }
}
