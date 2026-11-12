import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {

        // gets the absolute PATH of the current directory the program runs in
        Path currentDirectory = Paths.get(System.getProperty("user.dir")).toAbsolutePath();

        while (true) {
            stty("-icanon", "-echo");
            System.out.print("$ ");
            String line = null;
            try {
                line = readLine();
            } finally {
                stty("icanon", "echo");
                if (line == null) {
                    System.exit(0);
                }
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

    private static void stty(String... args) throws Exception{
        String[] cmd = new String[args.length + 1];
        cmd[0] = "stty";
        System.arraycopy(args, 0, cmd, 1, args.length);
        new ProcessBuilder(cmd).redirectInput(ProcessBuilder.Redirect.INHERIT).start().waitFor();
    }

    private static String readLine() throws  IOException {
        StringBuilder buffer = new StringBuilder();
        while (true) {
            int c = System.in.read();
            if (c == -1) {
                return null;
            }
            if (c == '\n' || c == '\r') {
                System.out.print("\n");
                System.out.flush();
                return buffer.toString();
            }
            if (c == '\t') {
                if (buffer.toString().equals("ech")) {
                    buffer.append("o ");
                    System.out.print("o ");
                }
                else if (buffer.toString().equals("exi")) {
                    buffer.append("t ");
                    System.out.print("t ");
                }
            }
            else if (c == 127 || c == 8) {
                if (!buffer.isEmpty()) {
                    buffer.deleteCharAt(buffer.length() - 1);
                    System.out.print("\b \b");
                }
            }
            else {
                buffer.append((char) c);
                System.out.print((char) c);
            }
            System.out.flush();
        }
    }
}
