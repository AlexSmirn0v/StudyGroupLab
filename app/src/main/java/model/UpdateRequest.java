package model;

/**
 * Запрос на обновление поля учебной группы.
 *
 * @param id        идентификатор группы
 * @param parameter обновляемый параметр
 * @param value     новое значение параметра
 */
public record UpdateRequest(Long id, GroupParams parameter, String value) implements Sendable {
}
