package splitter.model;

import java.time.LocalDate;

public record Bill(LocalDate date, User from, User to, Integer amount) {
}
