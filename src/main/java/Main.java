import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

            Redirect redirect = Redirect.NONE;
            String fileToRedirectTo = null;
            if (input.args().contains(">") || input.args().contains("1>")) {
                fileToRedirectTo = Parser.parseRedirectInput(input);
                redirect = Redirect.STDOUT;
            }
            else if (input.args().contains("2>")) {
                fileToRedirectTo = Parser.parseRedirectInput(input);
                redirect = Redirect.STDERR;
            }
            Output output = Builtin.executeInput(input, currentDirectory, redirect);
            currentDirectory = output.directory();
            if (output.stdOut() == null && output.stdErr() == null) {
                continue;
            }
            if (redirect == Redirect.STDOUT) {
                try {
                    String text = output.stdOut();
                    if (text == null) {
                        text = "";
                    }
                    if (!text.isEmpty() && !text.endsWith(System.lineSeparator())) {
                        text += System.lineSeparator();
                    }
                    Files.writeString(Path.of(fileToRedirectTo), text);
                    if (output.stdErr() != null) {
                        System.out.println(output.stdErr());
                    }
                }
                catch (IOException e) {
                    System.out.println("error:" + e.getMessage());
                }
            }
            else if (redirect == Redirect.STDERR) {
                try {
                    String text = output.stdErr();
                    if (text == null) {
                        text = "";
                    }
                    if (!text.isEmpty() && !text.endsWith(System.lineSeparator())) {
                        text += System.lineSeparator();
                    }
                    Files.writeString(Path.of(fileToRedirectTo), text);
                    if (output.stdOut() != null) {
                        System.out.println((output.stdOut()));
                    }
                }
                catch (IOException e) {
                    System.out.println("error:" + e.getMessage());
                }
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
}
