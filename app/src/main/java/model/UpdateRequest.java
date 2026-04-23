package model;

public record UpdateRequest(Long id, GroupParams parameter, String value) implements Sendable {
}
