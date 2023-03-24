package splitter.db.model;

public record Saldo(User ows, User isOwed, Double amount) {
}