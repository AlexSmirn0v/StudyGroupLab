package client;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Consumer;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

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
        GroupAddFrame addFrame = new GroupAddFrame(context, ADD.getCommand(), (group) -> {
            CommandMessage message = new CommandMessage(ADD.getFormat(), group, context.getUsername(),
                    context.getPassword());
            String response = context.getConnectFacade().askServer(message);
            return response;
        });
        addFrame.setVisible(true);
    }

    private static void onAddMin(AppContext context) {
        GroupAddFrame addFrame = new GroupAddFrame(context, ADD_MIN.getCommand(), (group) -> {
            CommandMessage message = new CommandMessage(ADD_MIN.getFormat(), group, context.getUsername(),
                    context.getPassword());
            String response = context.getConnectFacade().askServer(message);
            return response;
        });
        addFrame.setVisible(true);
    }

    private static void onRemoveGroup(AppContext context) {
        String idStr = JOptionPane.showInputDialog(context.getRoot(), context.getLocalText("dialog_remove"));
        if (idStr == null || idStr.trim().isEmpty()) {
            return;
        }
        try {
            Long id = Long.parseLong(idStr);
            CommandMessage message = new CommandMessage(REMOVE.getFormat(), id, context.getUsername(),
                    context.getPassword());
            String response = context.getConnectFacade().askServer(message);
            JOptionPane.showMessageDialog(context.getRoot(), response, context.getLocalText(REMOVE.getCommand()),
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(context.getRoot(), context.getLocalText("dialog_invalid_id"),
                    context.getLocalText(REMOVE.getCommand()),
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void onRemoveLow(AppContext context) {
        GroupAddFrame addFrame = new GroupAddFrame(context, REMOVE_LOW.getCommand(), (group) -> {
            CommandMessage message = new CommandMessage(REMOVE_LOW.getFormat(), group, context.getUsername(),
                    context.getPassword());
            String response = context.getConnectFacade().askServer(message);
            return response;
        });
        addFrame.setVisible(true);
    }

    private static void onClear(AppContext context) {
        int answer = JOptionPane.showConfirmDialog(
                context.getRoot(),
                context.getLocalText("dialog_clear"),
                context.getLocalText("dialog_proof"),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (answer != JOptionPane.YES_OPTION) {
            return;
        }
        CommandMessage message = new CommandMessage(CLEAR.getFormat(), context.getUsername(), context.getPassword());
        String response = context.getConnectFacade().askServer(message);
        JOptionPane.showMessageDialog(context.getRoot(), response, context.getLocalText(CLEAR.getCommand()),
                JOptionPane.INFORMATION_MESSAGE);
    }

    private static void onInfo(AppContext context) {
        CommandMessage message = new CommandMessage(INFO.getFormat(), context.getUsername(), context.getPassword());
        String response = context.getConnectFacade().askServer(message);
        JOptionPane.showMessageDialog(context.getRoot(), response, context.getLocalText(INFO.getCommand()),
                JOptionPane.INFORMATION_MESSAGE);
    }

    private static void onHistory(AppContext context) {
        CommandMessage message = new CommandMessage(HISTORY.getFormat(), context.getUsername(), context.getPassword());
        String response = context.getConnectFacade().askServer(message);
        JOptionPane.showMessageDialog(context.getRoot(), response, context.getLocalText(HISTORY.getCommand()),
                JOptionPane.INFORMATION_MESSAGE);
    }

    private static void onMaxSem(AppContext context) {
        CommandMessage message = new CommandMessage(MAX_SEM.getFormat(), context.getUsername(), context.getPassword());
        String response = context.getConnectFacade().askServer(message);
        JOptionPane.showMessageDialog(context.getRoot(), response, context.getLocalText(MAX_SEM.getCommand()),
                JOptionPane.INFORMATION_MESSAGE);
    }

    private static void onExecuteScript(AppContext context) {
        JFileChooser chooser = new JFileChooser();
        Deque<String> scriptHistory = new ArrayDeque<>();

        int result = chooser.showOpenDialog(context.getRoot());
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        try {
            File selectedFile = chooser.getSelectedFile();
            String scriptPath = selectedFile.getCanonicalPath();
            if (scriptHistory.contains(scriptPath)) {
                JOptionPane.showMessageDialog(context.getRoot(),
                        context.getLocalText("dialog_recursion"), context.getLocalText("dialog_error"),
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            scriptHistory.add(scriptPath);

            StringBuilder executionLog = new StringBuilder();
            try (java.util.Scanner scanner = new java.util.Scanner(
                    new BufferedInputStream(new FileInputStream(scriptPath)))) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine().trim();
                    if (line.isEmpty() || line.startsWith("#")) {
                        continue;
                    }

                    String[] parts = line.split("\\s+", 2);
                    CommandFormat command;
                    try {
                        command = CommandFormat.getByName(parts[0]);
                    } catch (IllegalArgumentException e) {
                        executionLog.append("[ERROR] Команда не распознана: ").append(parts[0]).append("\n");
                        continue;
                    }

                    String argument = (parts.length > 1) ? parts[1].trim() : "";
                    CommandMessage message = null;

                    try {
                        switch (command) {
                            case EXIT:
                                executionLog.append("[INFO] Команда exit пропущена в скрипте\n");
                                break;
                            case EXECUTE:
                                executionLog.append("[INFO] Вложенные скрипты не поддерживаются\n");
                                break;
                            case REMOVE:
                                message = new CommandMessage(command, Long.valueOf(argument),
                                        context.getUsername(), context.getPassword());
                                break;
                            case FILTER:
                                message = new CommandMessage(command, argument, context.getUsername(),
                                        context.getPassword());
                                break;
                            default:
                                message = new CommandMessage(command, context.getUsername(),
                                        context.getPassword());
                        }

                        if (message != null) {
                            String response = context.getConnectFacade().askServer(message);
                            executionLog.append("[").append(parts[0]).append("] ").append(response)
                                    .append("\n");
                        }
                    } catch (NumberFormatException e) {
                        executionLog.append("[ERROR] Неверный формат аргумента для ").append(parts[0])
                                .append(": ").append(argument).append("\n");
                    }
                }
            }
            JOptionPane.showMessageDialog(context.getRoot(), executionLog.toString(),
                    context.getLocalText(EXECUTE.getCommand()), JOptionPane.INFORMATION_MESSAGE);
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(context.getRoot(),
                    context.getLocalText("dialog_file_not_found") + e.getMessage(), context.getLocalText("dialog_error"),
                    JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(context.getRoot(),
                    context.getLocalText("dialog_script_error") + e.getMessage(), context.getLocalText("dialog_error"), JOptionPane.ERROR_MESSAGE);
        }
    }
}
