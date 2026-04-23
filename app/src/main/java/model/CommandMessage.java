package model;

import java.io.Serializable;

public record CommandMessage(CommandFormat command, Sendable payload) implements Serializable {

    non-sealed interface Wrap extends Sendable {
        Object value();
    }

    public static final record LongWrap(Long value) implements Wrap {
    }

    public static final record StringWrap(String value) implements Wrap {
    }

    private static Object unwrapPayload(Sendable payload) {
        if (payload instanceof Wrap w) {
            return w.value();
        }
        return payload;
    }

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

    public CommandMessage(CommandFormat command) {
        this(command, (Sendable) null);
    }

    public CommandMessage(CommandFormat command, Long payload) {
        this(command, new LongWrap(payload));
    }

    public CommandMessage(CommandFormat command, String payload) {
        this(command, new StringWrap(payload));
    }

    public Object getPayload() {
        return unwrapPayload(this.payload);
    }
}