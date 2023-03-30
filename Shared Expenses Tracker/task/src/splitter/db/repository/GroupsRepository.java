package splitter.db.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import splitter.db.model.Group;

import java.util.Optional;

@Repository
public interface GroupsRepository extends CrudRepository<Group, Long> {

    Optional<Group> findByName(String name);
}
