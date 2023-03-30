package splitter.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import splitter.db.model.User;

import java.util.Comparator;
import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<User, Long> {
    Comparator<User> userComparator = User::compare;



    static Comparator<User> getUserComparator() {
        return userComparator;
    }

    Optional<User> findByName(String name);
}
