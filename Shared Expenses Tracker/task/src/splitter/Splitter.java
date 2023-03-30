package splitter;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;


@SpringBootApplication
public class Splitter
        implements CommandLineRunner {


    public static void main(String[] args) {
        SpringApplication.run(Splitter.class, args);
    }

    @Override
    public void run(String... args) {
        enum COMMAND {
            balance, borrow, cashback, exit, group, help, purchase, repay, secretSanta, writeOff, unknown;

            static public COMMAND parseCommand(String input) {
                for (var command : values()) if (input.contains(command.name())) return command;
                return unknown;
            }

            static public List<String> getPrintable() {
                return Stream.of(balance, borrow, cashback, exit, group, help, purchase, repay, secretSanta, writeOff).map(Enum::name).toList();
            }
        }


        SplitterController bankService = new SplitterController();
        Scanner scanner = new Scanner(System.in);

        String input = scanner.nextLine();
        COMMAND command = COMMAND.parseCommand(input);

        while (!command.equals(COMMAND.exit)) {
            try {
                switch (command) {
                    case balance -> bankService.balance(input);
                    case borrow -> bankService.borrow(input);
                    case cashback -> bankService.cashback(input);
                    case group -> bankService.group(input);
                    case help -> COMMAND.getPrintable().forEach(System.out::println);
                    case purchase -> bankService.purchase(input);
                    case repay -> bankService.repay(input);
                    case writeOff -> bankService.writeOff(input);
                    case unknown -> System.out.println("Unknown command. Print help to show commands list");
                }
            } catch (IllegalCommandArguments e) {
                System.out.println("Illegal command arguments");
            }
            input = scanner.nextLine();
            command = COMMAND.parseCommand(input);
        }
    }

}