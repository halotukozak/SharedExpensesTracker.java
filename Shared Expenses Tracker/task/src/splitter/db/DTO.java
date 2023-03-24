package splitter.db;

import splitter.model.Bill;
import splitter.model.Saldo;
import splitter.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DTO {

    Users users = new Users();
    Bills bills = new Bills();

    public void borrow(LocalDate date, String personOne, String personTwo, int amount) {
        User user1 = users.getOrCreate(personOne);
        User user2 = users.getOrCreate(personTwo);

        Bill bill = new Bill(date, user1, user2, amount);
        bills.add(bill);
    }


    public void repay(LocalDate date, String personOne, String personTwo, int amount) {
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
        Integer person1Saldo = billList.stream().filter(bill -> bill.from().name().equals(user1.name()) && bill.to().name().equals(user2.name())).mapToInt(Bill::amount).sum();
        Integer person2Saldo = billList.stream().filter(bill -> bill.from().name().equals(user2.name()) && bill.to().name().equals(user1.name())).mapToInt(Bill::amount).sum();

        if (person1Saldo >= person2Saldo) {
            return new Saldo(user2, user1, person1Saldo - person2Saldo);
        } else {
            return new Saldo(user1, user2, person2Saldo - person1Saldo);
        }
    }
}



