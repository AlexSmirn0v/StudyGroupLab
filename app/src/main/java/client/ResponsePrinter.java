package client;

import java.util.Collection;

import model.StudyGroup;

/**
 * Класс для вывода ответов сервера.
 */
public final class ResponsePrinter {
    /**
     * Выводит в консоль клиента ответ в оптимальном форматировании.
     * @param response ответ для вывода
     */
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
                System.out.println();
            }
            return;
        }

        System.out.println(response);
    }
}
