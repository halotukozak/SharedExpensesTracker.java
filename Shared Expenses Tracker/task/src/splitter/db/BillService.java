package splitter.db;

import lombok.AllArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import splitter.db.model.Bill;
import splitter.db.model.Saldo;
import splitter.db.model.User;
import splitter.db.repository.BillsRepository;
import splitter.service.UserService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@AllArgsConstructor
public class BillService {

    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    final BillsRepository billsRepository;
    final UserService userService;

    public void borrow(LocalDate date, User personOne, User personTwo, BigDecimal amount) {
        Bill bill = new Bill(date, personOne, personTwo, amount.multiply(ONE_HUNDRED).intValue());
        billsRepository.save(bill);
    }


    public void repay(LocalDate date, User personOne, User personTwo, BigDecimal amount) {
        borrow(date, personTwo, personOne, amount);
    }

    public Collection<Saldo> balance(LocalDate date, boolean isOpen) {
        if (isOpen) date = date.withDayOfMonth(1).minusDays(1);
        List<Bill> billList = billsRepository.findBillsByDateBefore(date);
        billList.addAll(billsRepository.findBillsByDate(date));
        return getSaldoList(billList);
    }

    private Collection<Saldo> getSaldoList(List<Bill> billList) {
        List<Saldo> saldoList = new ArrayList<>();

        List<Pair<User, User>> pairs = userService.getPairs();
        for (Pair<User, User> pair : pairs) {
            Saldo saldo = calculateSaldo(pair.getFirst(), pair.getSecond(), billList);
            if (saldo.amount() != 0) saldoList.add(saldo);
        }
        return saldoList;
    }

    private Saldo calculateSaldo(User user1, User user2, List<Bill> billList) {
        int person1Saldo = billList.stream().filter(bill -> bill.from().name().equals(user1.name()) && bill.to().name().equals(user2.name())).mapToInt(Bill::amount).sum();
        int person2Saldo = billList.stream().filter(bill -> bill.to().name().equals(user2.name()) && bill.to().name().equals(user1.name())).mapToInt(Bill::amount).sum();

        if (person1Saldo >= person2Saldo) {
            return new Saldo(user2, user1, (person1Saldo - person2Saldo) / 100d);
        } else {
            return new Saldo(user1, user2, (person2Saldo - person1Saldo) / 100d);
        }
    }


    public void purchase(LocalDate date, String userName, BigDecimal amount, List<User> borrowers) {

        User user = userService.getOrCreate(userName);

        List<Integer> dividedAmount = calculateDividedAmount(amount.multiply(ONE_HUNDRED).intValue(), borrowers.size());
        for (int i = 0; i < dividedAmount.size(); i++) {
            borrow(date, borrowers.get(i).name(), user.name(), new BigDecimal(dividedAmount.get(i)).divide(ONE_HUNDRED, 2, RoundingMode.UNNECESSARY));
        }

    }

    private List<Integer> calculateDividedAmount(int amount, int size) {
        int part = amount / size;
        int change = amount - part * size;

        List<Integer> parts = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            parts.add((change-- > 0) ? part + 1 : part);
        }
        return parts;
    }

}
