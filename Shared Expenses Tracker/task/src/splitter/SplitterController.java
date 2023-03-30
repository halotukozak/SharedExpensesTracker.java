package splitter;

import lombok.AllArgsConstructor;
import splitter.db.DTO;
import splitter.db.model.Group;
import splitter.db.model.Saldo;
import splitter.db.model.User;
import splitter.service.GroupService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@AllArgsConstructor
public class SplitterController {
    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
    final GroupService groupService;

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
                System.out.printf("%s owes %s %.2f%n", saldo.isOwed().getClass(), saldo.ows().name(), saldo.amount());
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
        final String option = args[1];
        final String groupName = args[2];

        final String[] memberArgs = Arrays.copyOfRange(args, 3, args.length);

        switch (option) {
            case "create" -> {
                List<User> members = parseMembers(memberArgs, new ArrayList<>());
                DTO.createGroup(groupName, members);
            }
            case "add" -> {
                List<String> oldMembers = DTO.getGroup(groupName).getMembers().stream().map(User::name).collect(Collectors.toList());
                List<User> members = parseMembers(memberArgs, oldMembers);
                DTO.updateGroup(groupName, members);
            }
            case "remove" -> {
                List<String> oldMembers = DTO.getGroup(groupName).getMembers().stream().map(User::name).collect(Collectors.toList());
                List<User> membersToRemove = parseMembers(memberArgs, new ArrayList<>());
                List<User> members = DTO.calculateGroup(oldMembers, membersToRemove.stream().map(User::name).collect(Collectors.toList()));
                DTO.updateGroup(groupName, members);
            }
            case "show" -> {
                Group group = DTO.getGroup(groupName);
                if (group == null) {
                    System.out.println("Unknown group");
                    return;
                }
                List<User> members = group.getMembers();
                if (members.isEmpty()) System.out.println("Group is empty");
                else members.stream().map(User::name).sorted().forEach(System.out::println);
            }
        }

    }

    private String polishName(String arg) {
        return arg.replace("(", "").replace(")", "").replace(",", "");
    }

    private List<User> parseMembers(String[] args, List<String> membersToAdd) {
        List<String> membersToRemove = new ArrayList<>();
        for (String arg : args) {
            String member = polishName(arg);
            if (member.matches("-[A-Za-z]+")) membersToRemove.add(member.replace("-", ""));
            else membersToAdd.add(member.replace("+", ""));
        }
        return DTO.calculateGroup(membersToAdd, membersToRemove);
    }

    public void purchase(String input) {
        String[] args = input.split(" ");
        LocalDate date = LocalDate.now();
        int offset = 0;
        if (args[1].equals("purchase")) {
            date = LocalDate.parse(args[0], formatter);
            offset = 1;
        }

        String[] memberArgs = Arrays.copyOfRange(args, 4 + offset, args.length);
        List<User> members = parseMembers(memberArgs, new ArrayList<>());
        if (members.isEmpty()) {
            System.out.println("Group is empty");
            return;
        }

        String userName = args[1 + offset];
        BigDecimal amount = new BigDecimal(args[3 + offset]);

        DTO.purchase(date, userName, amount, members);
    }

    public void cashback(String input) {
        Pattern pattern = Pattern.compile("^\\d+$");
        int start = pattern.matcher(input).start();
        String left = input.substring(0, start - 1);
        String right = input.substring(start);
        repay(left + "-" + right);

    }

    public void writeOff(String input) {
        String[] args = input.split(" ");
        DTO.writeOff(args.length == 2 ? Optional.of(LocalDate.parse(args[0], formatter)) : Optional.empty());

    }

    @FunctionalInterface
    interface MultiConsumer<One, Two, Three, Four> {
        void apply(One one, Two two, Three three, Four four);
    }
}