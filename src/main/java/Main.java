import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        while (true) {
            System.out.print("$ ");
            Scanner scanner = new Scanner(System.in);
            String[] input = scanner.nextLine().trim().split(" ");
            String command = input[0];
            String[] inputArgs = Arrays.copyOfRange(input, 1, input.length);
            List<String> validCommands = List.of("exit", "echo", "type");
            if (command.equals("exit")) {
                return;
            }
            else if (command.equals("echo")) {
                for (String s: inputArgs) {
                    System.out.print(s + " ");
                }
                System.out.println();
            }
            else if (command.equals("type") && inputArgs.length != 0) {
                String executableFile = Environment.locateExecutableFileInPATH(inputArgs[0]);
                if (validCommands.contains(inputArgs[0])) {
                    System.out.println(inputArgs[0] + " is a shell builtin");
                }
                else if (executableFile != null) {
                    System.out.println(inputArgs[0] + " is " + executableFile);
                }
                else {
                    System.out.println(inputArgs[0] + ": not found");
                }
            }
            else {
                System.out.println(command + ": command not found");
            }
        }

    }
}
