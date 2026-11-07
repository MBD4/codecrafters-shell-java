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


        return new Input(command, parseArguments(argString));
    }

    private static List<String> parseArguments(String argString) {
        List<String> args;

        char[] argCharArr = argString.toCharArray();
        args = new ArrayList<>();
        StringBuilder argToAdd = new StringBuilder();
        boolean singleQuotesActivated = false;
        boolean doubleQuotesActivated = false;
        boolean escapeActivated = false;
        for (char c : argCharArr) {
            if(escapeActivated) {
                argToAdd.append(c);
                escapeActivated = false;
            }
            else if (c == '\\') {
                escapeActivated = true;
            }
            else if (c == '"') {
                doubleQuotesActivated = !doubleQuotesActivated;
            }
            else if (c == '\'' && !doubleQuotesActivated) {
                singleQuotesActivated = !singleQuotesActivated;
            }
            else if (!singleQuotesActivated && !doubleQuotesActivated && c == ' ') {
                args.add(argToAdd.toString());
                argToAdd = new StringBuilder();
            }
            else {
                argToAdd.append(c);
            }
        }
        args.add(argToAdd.toString());

        args.removeIf(String::isBlank);

        return args;
        
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
