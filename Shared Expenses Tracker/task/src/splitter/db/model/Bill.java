package splitter.db.model;

import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.time.Instant;
import java.time.LocalDate;


@Entity
@NoArgsConstructor
public final class Bill {

    @Id
    @GeneratedValue
    private Long id;


    private LocalDate date;
    @OneToOne
    private User from;
    @OneToOne
    private User to;
    private Integer amount;

    public Bill(LocalDate date, User from, User to, Integer amount) {
        this.date = date;
        this.from = from;
        this.to = to;
        this.amount = amount;
    }

    public User from() {
        return from;
    }

    public User to() {
        return to;
    }

    public Integer amount() {
        return amount;
    }

    public LocalDate date() {
        return date;
    }
}
