package model;

import java.io.Serializable;

/**
 * Сообщение команды с возможной доп информацией для передачи между клиентом и сервером.
 *
 * @param command тип команды
 * @param payload дополнительная информация для выполнения команды
 */
public record CommandMessage(CommandFormat command, Sendable payload) implements Serializable {

    /**
     * Интерфейс-обертка для простых значений, пересылаемых как часть сообщения.
     */
    non-sealed interface Wrap extends Sendable {
        Object value();
    }

    /**
     * Обертка для значения типа Long.
     *
     * @param value целочисленное значение
     */
    public static final record LongWrap(Long value) implements Wrap {
    }

    /**
     * Обертка для значения типа String.
     *
     * @param value строковое значение
     */
    public static final record StringWrap(String value) implements Wrap {
    }

    /**
     * Распаковывает дополнительную информацию, возвращая оригинальное значение для оберток.
     *
     * @param payload объект Sendable
     * @return внутреннее значение или сам объект, если он не является оберткой
     */
    private static Object unwrapPayload(Sendable payload) {
        if (payload instanceof Wrap w) {
            return w.value();
        }
        return payload;
    }

    /**
     * Проверяет соответствие дополнительной информации ожидаемому типу для команды.
     *
     * @param command команда
     * @param payload дополнительная информация
     * @throws IllegalArgumentException если payload не соответствует ожидаемому типу
     */
    public CommandMessage {
        Class<?> expected = command.getReqClass();
        Object actualPayload = unwrapPayload(payload);

        if (expected == Void.class) {
            if (actualPayload != null) {
                throw new IllegalArgumentException("Команда" + command.getName() + "' не принимает аргументов");
            }
        } else if (actualPayload == null) {
            throw new IllegalArgumentException(
                    "Команда" + command.getName() + "' ожидает аргумент типа " + expected.getSimpleName());
        } else if (!expected.isAssignableFrom(actualPayload.getClass())) {
            throw new IllegalArgumentException(
                    "Неверный тип аргумента для команды" + command.getName() + "'. Ожидается: "
                            + expected.getSimpleName() + ", получено: " + actualPayload.getClass().getSimpleName());
        }
    }

    /**
     * Создает сообщение без дополнительной информации.
     *
     * @param command команда
     */
    public CommandMessage(CommandFormat command) {
        this(command, (Sendable) null);
    }

    /**
     * Создает сообщение с целым числом в качестве аргумента команды.
     *
     * @param command команда
     * @param payload целочисленное значение
     */
    public CommandMessage(CommandFormat command, Long payload) {
        this(command, new LongWrap(payload));
    }

    /**
     * Создает сообщение со строкой в качестве аргумента команды.
     *
     * @param command команда
     * @param payload строковое значение
     */
    public CommandMessage(CommandFormat command, String payload) {
        this(command, new StringWrap(payload));
    }

    /**
     * Возвращает распакованную дополнительную информацию.
     *
     * @return дополнительная информация без оберток
     */
    public Object getPayload() {
        return unwrapPayload(this.payload);
    }
}