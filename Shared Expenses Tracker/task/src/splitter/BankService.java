package splitter;

import splitter.db.DTO;
import splitter.model.Saldo;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

public class BankService {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    private final DTO DTO = new DTO();

    void balance(String input) {
        String[] args = input.split(" ");
        LocalDate date = LocalDate.now();
        boolean isOpen = false;
        switch (args.length) {
            case 1 -> {
            }
            case 2 -> {
                if (args[0].equals("balance")) {
                    isOpen = args[1].equals("open");
                } else {
                    date = LocalDate.parse(args[0], formatter);
                }
            }
            case 3 -> {
                isOpen = args[2].equals("open");
                date = LocalDate.parse(args[0], formatter);
            }
            default -> throw new IllegalCommandArguments();
        }

        Collection<Saldo> saldoList = DTO.balance(date, isOpen);
        if (saldoList.size() != 0) {
            for (Saldo saldo : saldoList) {
                System.out.printf("%s owes %s %d%n", saldo.isOwed().name(), saldo.ows().name(), saldo.amount());
            }
        } else
            System.out.println("No repayments");
    }


    void borrow(String input) {
        parseAndExecute(input, DTO::borrow);
    }

    void repay(String input) {
        parseAndExecute(input, DTO::repay);
    }

    private void parseAndExecute(String input, MultiConsumer<LocalDate, String, String, Integer> function) {
        String[] args = input.split(" ");

        int offset = 0;
        LocalDate date;
        if (args.length == 4) {
            date = LocalDate.now();
        } else if (args.length == 5) {
            date = LocalDate.parse(args[0], formatter);
            offset = 1;
        } else throw new IllegalCommandArguments();
        String personOne = args[1 + offset];
        String personTwo = args[2 + offset];
        int amount = Integer.parseInt(args[3 + offset]);
        function.apply(date, personOne, personTwo, amount);
    }

    @FunctionalInterface
    interface MultiConsumer<One, Two, Three, Four> {
        void apply(One one, Two two, Three three, Four four);
    }
}