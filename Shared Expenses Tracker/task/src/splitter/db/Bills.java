package splitter.db;

import splitter.db.model.Bill;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Bills {
    private final Set<Bill> bills = new HashSet<>();

    public void add(Bill bill) {
        bills.add(bill);
    }

    public List<Bill> getBeforeInclusive(LocalDate date) {
        return bills.stream().filter(it -> it.date().isBefore(date) || it.date().isEqual(date)).collect(Collectors.toList());
    }

}
