import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Parser {

    public static Input parseInitialInput(String line) {
        String[] inputArr = line.trim().split(" ", 2);
        String command = inputArr[0].trim();

        if (inputArr.length <= 1) {
            return new Input(command, new ArrayList<>());
        }

        String argString = inputArr[1].trim();
        List<String> args;

        if (argString.contains("'")) {
            long count = argString.chars().filter(ch -> ch == '\'').count();
            if (count % 2 != 0) {
                System.out.println("Error: uneven number of quotes not allowed");
                return null;
            }
            char[] argCharArr = argString.toCharArray();
            args = new ArrayList<>();
            StringBuilder argToAdd = new StringBuilder();
            boolean skipSpaces = false;
            for (char c : argCharArr) {
                if (c == '\'') {
                    skipSpaces = !skipSpaces;
                } else if (!skipSpaces && c == ' ') {
                    args.add(argToAdd.toString());
                    argToAdd = new StringBuilder();
                } else {
                    argToAdd.append(c);
                }
            }
            args.add(argToAdd.toString());
        }
        else {
            //replaces multiple repeated spaces with one space
            argString = argString.replaceAll(" +", " ");

            args = new ArrayList<>(Arrays.stream(argString.split(" ")).toList());
        }

        args.removeIf(String::isBlank);

        return new Input(command, args);
    }

    public static Path parseNewDirectory(String input, Path currentDirectory) {
        String[] inputSplit = input.split("");
        Path newDirectory = currentDirectory;
        StringBuilder buffer = new StringBuilder();
        for (String s : inputSplit) {
            buffer.append(s);
            if (!buffer.toString().equals(".") && !buffer.toString().equals("/") && !buffer.toString().equals("./")
                    && !buffer.toString().equals("..") && !buffer.toString().equals("../") && !buffer.toString().equals("~")) {
                newDirectory = Path.of(newDirectory.toString() + "/" + String.join("", inputSplit));
                break;
            }
            else if (buffer.toString().equals("/")) {
                newDirectory = Path.of(input);
                break;
            }
            else if (buffer.toString().equals("~")) {
                newDirectory = Path.of(Environment.getHomeEnvironmentPath());
            }
            else if (buffer.toString().equals("./")) {
                inputSplit = Arrays.stream(inputSplit).skip(2).toArray(String[]::new);
                buffer = new StringBuilder();
            }
            else if (buffer.toString().equals("../")) {
                Path parrentPath = newDirectory.getParent();
                if (parrentPath == null) {
                    System.out.println("cd: " + input + ": No such file or directory");
                    return currentDirectory;
                }
                inputSplit = Arrays.stream(inputSplit).skip(3).toArray(String[]::new);
                newDirectory = Path.of(parrentPath.toString());
                buffer = new StringBuilder();
            }
        }
        return newDirectory;
    }
}
