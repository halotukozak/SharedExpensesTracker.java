package splitter.db.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import splitter.IllegalCommandArguments;

import javax.persistence.*;
import java.util.List;
import java.util.regex.Pattern;

@Entity
@Getter
@NoArgsConstructor
public final class Group {
    @Id
    @GeneratedValue
    private Long id;


    private final static Pattern namePattern = Pattern.compile("[A-Z]+");
    private  String name;
    @ManyToMany
    private List<User> members;


    public Group(String name, List<User> members) {
        if (name.matches(namePattern.pattern())) this.name = name;
        else throw new IllegalCommandArguments();
        this.members = members;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }

}
