import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Parser {

    public static Input parseInitialInput(String line) {
        List<String> lineParsed;

        char[] argCharArr = line.toCharArray();
        lineParsed = new ArrayList<>();
        StringBuilder argToAdd = new StringBuilder();
        boolean singleQuotesActivated = false;
        boolean doubleQuotesActivated = false;
        boolean escapeActivated = false;
        for (char c : argCharArr) {
            if(escapeActivated) {
                argToAdd.append(c);
                escapeActivated = false;
            }
            else if (c == '\\' && !singleQuotesActivated) {
                escapeActivated = true;
            }
            else if (c == '"' && !singleQuotesActivated) {
                doubleQuotesActivated = !doubleQuotesActivated;
            }
            else if (c == '\'' && !doubleQuotesActivated) {
                singleQuotesActivated = !singleQuotesActivated;
            }
            else if (!singleQuotesActivated && !doubleQuotesActivated && c == ' ') {
                lineParsed.add(argToAdd.toString());
                argToAdd = new StringBuilder();
            }
            else {
                argToAdd.append(c);
            }
        }
        lineParsed.add(argToAdd.toString());

        lineParsed.removeIf(String::isBlank);

        String command = lineParsed.removeFirst();;

        return new Input(command, lineParsed);
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
