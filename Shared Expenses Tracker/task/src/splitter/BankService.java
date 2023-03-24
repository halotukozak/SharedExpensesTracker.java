package splitter;

import splitter.db.DTO;
import splitter.db.model.Group;
import splitter.db.model.Saldo;
import splitter.db.model.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BankService {
    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

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
                System.out.printf("%s owes %s %.2f%n", saldo.isOwed().name(), saldo.ows().name(), saldo.amount());
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

    private void parseAndExecute(String input, MultiConsumer<LocalDate, String, String, BigDecimal> function) {
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
        BigDecimal amount = new BigDecimal(args[3 + offset]);
        function.apply(date, personOne, personTwo, amount);
    }

    public void group(String input) {
        String[] args = input.split(" ");
        switch (args[1]) {
            case "create" -> {
                List<String> members = new ArrayList<>();
                for (int i = 3; i < args.length; i++)
                    members.add(args[i].replace("(", "").replace(")", "").replace(",", ""));

                DTO.createGroup(args[2], members);
            }
            case "show" -> {
                Group group = DTO.getGroup(args[2]);
                if (group == null) {
                    System.out.println("Unknown group");
                    return;
                }
                group.members().stream().map(User::name).sorted().forEach(System.out::println);
            }
        }

    }

    public void purchase(String input) {
        String[] args = input.split(" ");
        LocalDate date = LocalDate.now();
        int offset = 0;
        if (args.length == 6) {
            date = LocalDate.parse(args[0], formatter);
            offset = 1;
        }
        DTO.purchase(date, args[1 + offset], new BigDecimal(args[3 + offset]), args[4 + offset].replace("(", "").replace(")", ""));
    }

    @FunctionalInterface
    interface MultiConsumer<One, Two, Three, Four> {
        void apply(One one, Two two, Three three, Four four);
    }
}