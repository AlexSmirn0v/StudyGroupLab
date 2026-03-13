package model;

/**
 * Класс, представляющий координаты учебной группы.
 */
public class Coordinates {
    private Long x; // Поле не может быть null
    private long y;

    public Coordinates(Long x, long y) throws IllegalArgumentException {
        if (x == null) {
            throw new IllegalArgumentException("x не может быть null");
        }
        this.x = x;
        this.y = y;
    }

    public Long getX() {
        return x;
    }

    public long getY() {
        return y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
