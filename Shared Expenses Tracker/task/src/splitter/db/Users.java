package splitter.db;

import splitter.model.User;

import java.util.ArrayList;
import java.util.List;

public class Users {
    private final List<User> users = new ArrayList<>();

    public User getOrCreate(String person) {
        for (User user : users) if (person.equals(user.name())) return user;
        User user = new User(person);
        users.add(user);
        return user;
    }

    public List<List<User>> getPairs() {
        List<List<User>> result = new ArrayList<>();
        for (int i = 0; i < users.size(); i++)
            for (int j = i + 1; j < users.size(); j++)
                result.add(List.of(users.get(i), users.get(j)));
        return result;
    }
}
