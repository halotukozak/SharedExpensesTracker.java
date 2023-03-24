package splitter.db;

import splitter.db.model.User;
import splitter.db.model.UserComparator;

import java.util.ArrayList;
import java.util.List;

public class Users {
    private final List<User> users = new ArrayList<>();
    UserComparator userComparator = new UserComparator();

    public User getOrCreate(String person) {
        for (User user : users) if (person.equals(user.name())) return user;
        User user = new User(person);
        users.add(user);
        users.sort(userComparator);
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
