package validation;




import java.util.Scanner;
import java.util.function.Function;

public class InputValidator {

    private final Scanner SC = new Scanner(System.in);

    public String readString(String prompt) {
        System.out.print(prompt);
        return SC.nextLine().trim();
    }

    // Keeps asking until checker returns null (null = valid, otherwise the error message)
    public String readValid(String prompt, Function<String, String> checker) {
        while (true) {
            String input = readString(prompt);
            String error = checker.apply(input);
            if (error == null) {
                return input;
            }
            System.out.println(error);
        }
    }

    // Integer in [min, max], used for menu choices
    public int readInt(String prompt, int min, int max) {
        while (true) {
            String input = readString(prompt);
            try {
                int number = Integer.parseInt(input);
                if (number >= min && number <= max) {
                    return number;
                }
            } catch (NumberFormatException e) {
                // fall through to the error message
            }
            System.out.println("Please enter an integer from " + min + " to " + max + ".");
        }
    }

    public boolean readYesNo(String prompt) {
        String answer = readValid(prompt,
                s -> s.equalsIgnoreCase("Y") || s.equalsIgnoreCase("N") ? null : "Please enter Y or N.");
        return answer.equalsIgnoreCase("Y");
    }
}