package splitter.db;

import splitter.db.model.Bill;
import splitter.db.model.Group;
import splitter.db.model.Saldo;
import splitter.db.model.User;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DTO {

    final Users users = new Users();
    final Bills bills = new Bills();
    final Groups groups = new Groups();

    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    public void borrow(LocalDate date, String personOne, String personTwo, BigDecimal amount) {
        User user1 = users.getOrCreate(personOne);
        User user2 = users.getOrCreate(personTwo);

        Bill bill = new Bill(date, user1, user2, amount.multiply(ONE_HUNDRED).intValue());
        bills.add(bill);
    }


    public void repay(LocalDate date, String personOne, String personTwo, BigDecimal amount) {
        borrow(date, personTwo, personOne, amount);
    }

    public Collection<Saldo> balance(LocalDate date, boolean isOpen) {
        if (isOpen) date = date.withDayOfMonth(1).minusDays(1);
        List<Bill> billList = bills.getBeforeInclusive(date);

        return getSaldoList(billList);
    }

    private Collection<Saldo> getSaldoList(List<Bill> billList) {
        List<Saldo> saldoList = new ArrayList<>();

        List<List<User>> pairs = users.getPairs();
        for (var pair : pairs) {
            Saldo saldo = calculateSaldo(pair.get(0), pair.get(1), billList);
            if (saldo.amount() != 0) saldoList.add(saldo);
        }
        return saldoList;
    }

    private Saldo calculateSaldo(User user1, User user2, List<Bill> billList) {
        int person1Saldo = billList.stream().filter(bill -> bill.from().name().equals(user1.name()) && bill.to().name().equals(user2.name())).mapToInt(Bill::amount).sum();
        int person2Saldo = billList.stream().filter(bill -> bill.from().name().equals(user2.name()) && bill.to().name().equals(user1.name())).mapToInt(Bill::amount).sum();

        if (person1Saldo >= person2Saldo) {
            return new Saldo(user2, user1, (person1Saldo - person2Saldo) / 100d);
        } else {
            return new Saldo(user1, user2, (person2Saldo - person1Saldo) / 100d);
        }
    }

    public Group getGroup(String name) {
        Optional<Group> group = groups.get(name);
        return group.orElse(null);
    }

    public void createGroup(String name, List<String> members) {
        Group group = new Group(name, members.stream().sorted().map(users::getOrCreate).collect(Collectors.toList()));
        groups.add(group);
    }

    public void purchase(LocalDate date, String userName, BigDecimal amount, String groupName) {

        User user = users.getOrCreate(userName);
        Group group = groups.get(groupName).get();

        List<Integer> dividedAmount = calculateDividedAmount(amount.multiply(ONE_HUNDRED).intValue(), group.members().size());
        for (int i = 0; i < dividedAmount.size(); i++) {
            borrow(date, group.members().get(i).name(), user.name(), new BigDecimal(dividedAmount.get(i)).divide(ONE_HUNDRED, 2, RoundingMode.UNNECESSARY));
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



