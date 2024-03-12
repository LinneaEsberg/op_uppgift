import java.util.Scanner;

public class Main {
    static boolean running = true;

    public static void main(String[] args) {
        while (running) {
            Scanner scanner = new Scanner(System.in);
            System.out.print("\nAnge nummer att validera: ");
            String input = scanner.nextLine();

            ValidityChecks vc = new ValidityChecks(input);
            vc.runValidityChecks();

            System.out.println("\n'" + input + "'" + " är: ");
            System.out.println("    - " + (vc.isSSN() ? "" : "inte ") + "ett korrekt personnummer");
            System.out.println("    - " + (vc.isCN() ? "" : "inte ") + "ett korrekt samordningsnummer");
            System.out.println("    - " + (vc.isOrgN() ? "" : "inte ") + "ett korrekt organisationsnummer");

            continuePrompt(scanner);
        }
    }

    private static void continuePrompt(Scanner scanner) {
        System.out.print("\nVill du fortsätta? (y/n): ");

        String option = scanner.nextLine();
        if (option.equals("n")) {
            running = false;
        } else if (!option.equals("y")) {
            continuePrompt(scanner);
        }
    }
}

