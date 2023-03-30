package splitter.db.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@NoArgsConstructor
public final class User {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    public User(String name) {
        this.name = name;
    }

    public int compare(User other) {
        return this.name().compareTo(other.name());
    }

    public String name() {
        return name;
    }
}

