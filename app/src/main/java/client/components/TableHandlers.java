package client.components;

import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.swing.JOptionPane;

import client.AppContext;
import client.components.TableModel.EditableProperty;
import model.CommandFormat;
import model.CommandMessage;
import model.StudyGroup;
import model.UpdateRequest;
import model.GroupParams;

public class TableHandlers {
    @FunctionalInterface
    public interface UpdateHandler {
        void update(StudyGroup group, EditableProperty property, String validatedValue);
    }

    static UpdateHandler getUpdateHandler(AppContext appContext) {
        return (group, property, validatedValue) -> {
            switch (property) {
                case NAME -> group.edit(GroupParams.NAME, validatedValue);
                case COORDS -> group.edit(GroupParams.COORDS, validatedValue);
                case STUDENTS_COUNT -> group.edit(GroupParams.STUDENTS_COUNT, validatedValue);
                case TRANSFERRED_STUDENTS -> group.edit(GroupParams.TRANSFERRED_STUDENTS, validatedValue);
                case AVERAGE_MARK -> group.edit(GroupParams.AVERAGE_MARK, validatedValue);
                case SEMESTER_ENUM -> group.edit(GroupParams.SEMESTER_ENUM, validatedValue);
                case GROUP_ADMIN -> group.edit(GroupParams.GROUP_ADMIN, validatedValue);
            }

            // Then send the update to server if ConnectFacade is available
            if (appContext.getConnectFacade() != null) {
                GroupParams param = switch (property) {
                    case NAME -> GroupParams.NAME;
                    case COORDS -> GroupParams.COORDS;
                    case STUDENTS_COUNT -> GroupParams.STUDENTS_COUNT;
                    case TRANSFERRED_STUDENTS -> GroupParams.TRANSFERRED_STUDENTS;
                    case AVERAGE_MARK -> GroupParams.AVERAGE_MARK;
                    case SEMESTER_ENUM -> GroupParams.SEMESTER_ENUM;
                    case GROUP_ADMIN -> GroupParams.GROUP_ADMIN;
                };

                UpdateRequest updateRequest = new UpdateRequest(group.getId(), param, validatedValue);
                CommandMessage message = new CommandMessage(
                        CommandFormat.UPDATE,
                        updateRequest,
                        appContext.getUsername(),
                        appContext.getPassword());

                String response = appContext.getConnectFacade().askServer(message);
                if (response.contains("Ошибка") || response.contains("ошибка")) {
                    JOptionPane.showMessageDialog(appContext.getRoot(), "Server update failed: " + response);
                    return;
                }
                appContext.notifyUpdate();
            }
        };
    }

    public static Predicate<StudyGroup> getCanDelete(AppContext appContext) {
        return g -> {
            String currentUser = appContext.getUsername();
            return currentUser != null && currentUser.equals(g.getAuthorName());
        };
    }

    /**
     * Creates a delete handler that communicates with the server.
     * 
     * @return a Consumer that deletes groups via server
     */
    public static Consumer<StudyGroup> getDeleteHandler(AppContext appContext) {
        return group -> {
            if (appContext.getConnectFacade() == null) {
                throw new RuntimeException("ConnectFacade is not available");
            }

            CommandMessage.LongWrap idWrap = new CommandMessage.LongWrap(group.getId());
            CommandMessage message = new CommandMessage(
                    CommandFormat.REMOVE,
                    idWrap,
                    appContext.getUsername(),
                    appContext.getPassword());

            String response = appContext.getConnectFacade().askServer(message);
            if (response.contains("Ошибка") || response.contains("ошибка")) {
                JOptionPane.showMessageDialog(appContext.getRoot(), "Удаление на сервере не удалось: " + response);
                return;
            }
            appContext.notifyUpdate();
        };
    }
}
