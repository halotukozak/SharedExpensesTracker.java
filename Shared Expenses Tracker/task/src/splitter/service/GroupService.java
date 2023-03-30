package splitter.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import splitter.db.model.Group;
import splitter.db.model.User;
import splitter.db.repository.GroupsRepository;
import splitter.db.repository.UsersRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
public class GroupService {
    final GroupsRepository groupsRepository;

    public Optional<Group> getGroup(String name) {
        return groupsRepository.findByName(name);
    }

    public void createGroup(String name, List<User> members) {
        Group group = new Group(name, members);
        groupsRepository.save(group);
    }

    public List<User> calculateGroup(List<String> membersToAdd, List<String> membersToRemove) {
        Set<User> members = new HashSet<>();
        for (var member : membersToAdd) {
            if (member.matches("[A-Z]+"))
                groupsRepository.get(member).ifPresent(group -> members.addAll(group.getMembers()));
            else members.add(users.getOrCreate(member));
        }
        for (var member : membersToRemove) {
            if (member.matches("[A-Z]+"))
                groupsRepository.get(member).ifPresent(group -> group.getMembers().forEach(members::remove));
            else members.remove(users.getOrCreate(member));
        }
        return members.stream().sorted(UsersRepository.getUserComparator()).toList();
    }

    public void updateGroup(String name, List<User> members) {
        groupsRepository.get(name).ifPresent((group) -> group.setMembers(members));
    }
}
