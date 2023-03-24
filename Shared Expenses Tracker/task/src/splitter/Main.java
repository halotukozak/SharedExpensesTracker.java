package splitter;

import java.util.List;
import java.util.Scanner;

import static splitter.Main.COMMAND.*;

public class Main {

    public static void main(String[] args) {
        BankService bankService = new BankService();
        Scanner scanner = new Scanner(System.in);

        String input = scanner.nextLine();
        COMMAND command = COMMAND.parseCommand(input);

        while (!command.equals(exit)) {
            try {
                switch (command) {
                    case help ->
                            List.of(balance.name(), borrow.name(), exit.name(), help.name(), repay.name()).forEach(System.out::println);
                    case borrow -> bankService.borrow(input);
                    case repay -> bankService.repay(input);
                    case balance -> bankService.balance(input);
                    case unknown -> System.out.println("Unknown command. Print help to show commands list");
                }
            } catch (IllegalCommandArguments e) {
                System.out.println("Illegal command arguments");
            }
            input = scanner.nextLine();
            command = COMMAND.parseCommand(input);
        }
    }


    enum COMMAND {
        balance, borrow, exit, help, repay, unknown;

        static public COMMAND parseCommand(String input) {
            for (var command : values()) if (input.contains(command.name())) return command;
            return unknown;
        }
    }
}

