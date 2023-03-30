package splitter.service;

import lombok.AllArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import splitter.db.model.User;
import splitter.db.repository.UsersRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
    final UsersRepository usersRepository;

    public List<Pair<User, User>> getPairs() {
        List<User> users = usersRepository.findAll();
        List<Pair<User, User>> result = new ArrayList<>();
        for (int i = 0; i < users.size(); i++)
            for (int j = i + 1; j < users.size(); j++)
                result.add(Pair.of(users.get(i), users.get(j)));
        return result;
    }

    public User getOrCreate(String name) {
        Optional<User> user = usersRepository.findByName(name);
        return user.orElse(usersRepository.save(new User(name)));
    }
}
