package splitter.db.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import splitter.db.model.Bill;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public interface BillsRepository extends CrudRepository<Bill, Long> {

    List<Bill> findBillsByDateBefore(LocalDate date);
    List<Bill> findBillsByDate(LocalDate date);

    void deleteBillsByDateAfter(LocalDate date);

}
