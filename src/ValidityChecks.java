import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;

public class ValidityChecks {
    private String input;
    ArrayList<Boolean> ssnChecks = new ArrayList<>(); // Personnummer
    ArrayList<Boolean> cnChecks = new ArrayList<>(); // Samordningsnummer
    ArrayList<Boolean> onChecks = new ArrayList<>(); // Organisationsnummer

    public ValidityChecks(String input){ this.input = input; }

    public void runValidityChecks(){
        String pass = "pass";
        String fail = "fail";

        // Check if the format is one of the accepted formats
        // for social security number and coordination number
        boolean formatCheck = formatTest();
        ssnChecks.add(formatCheck);
        cnChecks.add(formatCheck);
        System.out.println("Format (personnummer & samordningsnummer): " + (formatCheck ? pass : fail));

        // Check if string is on the format of an organization number
        boolean orgCheck = organizationNrTest();
        System.out.println("Format (organisationsnummer): " + (orgCheck ? pass : fail));
        onChecks.add(orgCheck);

        if (formatCheck) {
            // Check if the date is an actual calendar date
            boolean dateCheck = dateTest();
            ssnChecks.add(dateCheck);
            System.out.println("Datum (personnummer): " + (dateCheck ? pass : fail));

            // Check if date is a coordination number referencing an actual calendar date
            boolean dateCNCheck = coordinationNrTest();
            cnChecks.add(dateCNCheck);
            System.out.println("Datum (samordningsnummer): " + (dateCNCheck ? pass : fail));

            // Check if calculated control number equals the one in the given string
            int control = luhns();
            int inputControl = Character.getNumericValue(input.charAt(input.length()-1));
            boolean controlNrCheck = control == inputControl;
            ssnChecks.add(controlNrCheck);
            cnChecks.add(controlNrCheck);
            onChecks.add(controlNrCheck);
            System.out.println("Kontrollnummer (samtliga): " + (controlNrCheck ? pass : fail));
        }
    }

    public boolean isSSN() { return !ssnChecks.contains(false); }
    public boolean isCN() { return !cnChecks.contains(false); }
    public boolean isOrgN() { return !onChecks.contains(false); }

    private boolean formatTest() {
        return input.matches("\\d{10}") ||
                input.matches("\\d{6}-\\d{4}") ||
                input.matches("\\d{6}\\+\\d{4}") ||
                input.matches("\\d{12}") ||
                input.matches("\\d{8}-\\d{4}");
    }

    private boolean dateTest() {
        int year;
        int month;
        int day;

        if (input.length() <= 11) {
            year = Integer.parseInt(input.substring(0, 2));
            month = Integer.parseInt(input.substring(2, 4));
            day = Integer.parseInt(input.substring(4, 6));

            // Add century
            int yearDiff = LocalDate.now().getYear() - year;
            year = (yearDiff >= 2000 ? 2000 + year : 1900 + year);
            if (input.contains("+")) { year -= 100; }

        } else {
            year = Integer.parseInt(input.substring(0,4));
            month = Integer.parseInt(input.substring(4, 6));
            day = Integer.parseInt(input.substring(6, 8));
        }

        try {
            LocalDate date = LocalDate.of(year, month, day);
            return true;
        } catch (DateTimeException e) {
            return false;
        }
    }

    private int luhns() {
        StringBuilder modifiedString = new StringBuilder();

        int startIndex = 0;
        if (input.length() > 11) {
            startIndex = 2;
        }

        // Create a modified string without '-' or '+' and control number
        for (int i = startIndex; i < input.length()-1; i++) {
            if (Character.isDigit(input.charAt(i))) {
                modifiedString.append(input.charAt(i));
            }
        }

        int sum = 0;
        for (int i = 0; i < modifiedString.length(); i++) {
            int currentNumber = Character.getNumericValue(modifiedString.charAt(i));

            if(i % 2 == 0) {
                int temp = currentNumber*2;
                if (temp > 9) {
                    int tempsum = 0;
                    while (temp != 0)
                    {
                        tempsum = tempsum + temp % 10;
                        temp = temp/10;
                    }
                    temp = tempsum;
                }
                sum += temp;
            } else { sum += currentNumber; }
        }
        return (10 - (sum % 10)) % 10;
    }

    private boolean coordinationNrTest() {
        int day;
        if (input.length() <= 11) {
            day = Integer.parseInt(input.substring(4, 6));
        } else {
            day = Integer.parseInt(input.substring(6, 8));
        }

        if ((day >= 61) && (day <= 91)) {
            String temp;
            day -= 60;
            if (input.length() <= 11) {
                temp = input.substring(0, 4) + day + input.substring(6);
            } else {
                temp = input.substring(0, 6) + day + input.substring(8);
            }
            ValidityChecks vc = new ValidityChecks(temp);
            return vc.dateTest();
        }

        return false;
    }

    private boolean organizationNrTest() {
        if (orgNrFormatCheck()) {
            return orgNrStartCheck() && orgNrMiddleCheck();
        }
        return false;
    }

    private boolean orgNrFormatCheck() {
        return input.matches("\\d{6}-\\d{4}") || input.matches("\\d{8}-\\d{4}");
    }

    private boolean orgNrStartCheck() {
        return input.length() <= 11 || input.startsWith("16");
    }

    private boolean orgNrMiddleCheck() {
        String temp;
        if (input.length() > 11) {
            temp = input.substring(4, 6);
        } else {
            temp = input.substring(2, 4);
        }

        return Integer.parseInt(temp) >= 20;
    }
}
