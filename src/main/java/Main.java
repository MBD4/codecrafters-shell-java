import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
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

            boolean redirect = false;
            int redirectSymbolIndex = -1;
            String fileToRedirectTo = "";
            if (input.args().contains(">") || input.args().contains("1>")) {
                if (input.args().contains(">")) {
                    redirectSymbolIndex = input.args().indexOf(">");
                    if (redirectSymbolIndex == input.args().size() - 1) {
                        System.out.println("Error: no file to redirect to specified");
                        continue;
                    }
                    input.args().remove(">");
                    fileToRedirectTo = input.args().get(redirectSymbolIndex);
                    input.args().remove(redirectSymbolIndex);
                }
                else {
                    redirectSymbolIndex = input.args().indexOf("1>");
                    if (redirectSymbolIndex >= input.args().size() - 1) {
                        System.out.println("Error: no file to redirect to specified");
                        continue;
                    }
                    input.args().remove("1>");
                    fileToRedirectTo = input.args().get(redirectSymbolIndex);
                    input.args().remove(redirectSymbolIndex);
                }
                redirect = true;
            }
            Output output = Builtin.executeInput(input, currentDirectory, redirect);
            currentDirectory = output.directory();
            if (output.outputString() == null) {
                continue;
            }
            if (redirect) {
                try {
                    String text = output.outputString();
                    if (!text.isEmpty() && !text.endsWith(System.lineSeparator())) {
                        text += System.lineSeparator();
                    }
                    Files.writeString(Path.of(fileToRedirectTo), text);
                }
                catch (IOException e) {
                    System.out.println("error:" + e.getMessage());
                }
            }
            else {
                System.out.println(output.outputString());
            }
        }
    }
}
