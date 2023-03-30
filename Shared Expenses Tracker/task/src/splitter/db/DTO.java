package splitter.db;

import lombok.AllArgsConstructor;
import splitter.db.repository.BillsRepository;
import splitter.db.repository.GroupsRepository;
import splitter.db.repository.UsersRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@AllArgsConstructor
public class DTO {

    final UsersRepository usersRepository;
    final BillsRepository billsRepository;
    final GroupsRepository groupsRepository;

    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");




    public void writeOff(Optional<LocalDate> date) {
        date.ifPresentOrElse(billsRepository::clearAfter, billsRepository::clearAll);

    }
}



