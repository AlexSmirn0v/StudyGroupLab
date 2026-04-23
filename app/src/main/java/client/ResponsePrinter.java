package client;

import java.util.Collection;

import model.StudyGroup;

public final class ResponsePrinter {
    public static void print(Object response) {
        if (response == null) {
            System.out.println("Сервер вернул пустой ответ.");
            return;
        }

        if (response instanceof String text) {
            System.out.println(text);
            return;
        }

        if (response instanceof StudyGroup group) {
            System.out.println(group);
            return;
        }

        if (response instanceof Collection<?> collection) {
            if (collection.isEmpty()) {
                System.out.println("Коллекция пуста.");
                return;
            }
            for (Object item : collection) {
                System.out.println(item);
            }
            return;
        }

        System.out.println(response);
    }
}
