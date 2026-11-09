import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {

        // gets the absolute PATH of the current directory the program runs in
        Path currentDirectory = Paths.get(System.getProperty("user.dir")).toAbsolutePath();

        while (true) {
            System.out.print("$ ");
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            if (line.isBlank()) {
                continue;
            }
            Input input = Parser.parseInitialInput(line);

            if (input == null) {
                continue;
            }

            Redirect redirect = Parser.parseRedirectInput(input);

            Output output = Builtin.executeInput(input, currentDirectory, redirect);
            currentDirectory = output.directory();
            if (output.stdOut() == null && output.stdErr() == null) {
                continue;
            }
            if (redirect != Redirect.NONE) {
                writeFile(redirect, output);
            }
            else {
                if (output.stdErr() != null) {
                    System.out.println(output.stdErr());
                }
                if (output.stdOut() != null) {
                    System.out.println((output.stdOut()));
                }
            }
        }
    }

    private static void writeFile(Redirect redirect, Output output) {
        try {
            String text;
            if (redirect.target() == Redirect.Target.STDERR) {
                text = output.stdErr();
            }
            else {
                text = output.stdOut();
            }
            if (text == null) {
                text = "";
            }
            if (!text.isEmpty() && !text.endsWith(System.lineSeparator())) {
                text += System.lineSeparator();
            }
            if (redirect.append()) {
                Files.writeString(Path.of(redirect.file()), text, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            }
            else {
                Files.writeString(Path.of(redirect.file()), text);
            }
            if (redirect.target() == Redirect.Target.STDERR) {
                if (output.stdOut() != null) {
                    System.out.println((output.stdOut()));
                }
            }
            else {
                if (output.stdErr() != null) {
                    System.out.println((output.stdErr()));
                }
            }
        }
        catch (IOException e) {
            System.out.println("error:" + e.getMessage());
        }
    }
}
