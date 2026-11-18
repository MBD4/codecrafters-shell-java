import java.io.IOException;
import java.io.PushbackInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class Completer {

    // TODO dont print from this method. the result should be return to main and printed from there
    public static boolean commandCompleter(StringBuilder buffer, PushbackInputStream in) throws IOException {
        List<String> externalExecutable;
        if (buffer.toString().equals("ech")) {
            buffer.append("o ");
            System.out.print("o ");
        }
        else if (buffer.toString().equals("exi")) {
            buffer.append("t ");
            System.out.print("t ");
        }
        else if ((externalExecutable = Environment.locateExecutableFileInPATHFromPrefix(buffer.toString())) != null) {
            if (externalExecutable.size() == 1) {
                String singleExternalExecutable = externalExecutable.getFirst();
                buffer.append(singleExternalExecutable).append(" ");
                System.out.print(singleExternalExecutable + " ");
                return false;
            }
            String currentBuffer = buffer.toString();
            int longestPrefix = buffer.toString().length();
            for (int i = 0; i < externalExecutable.getFirst().length(); i++) {
                if (i < currentBuffer.length()) {
                    continue;
                }
                int copy = i + 1;
                if (externalExecutable.stream().allMatch(s -> s.startsWith(externalExecutable.getFirst().substring(0, copy)))) {
                    longestPrefix = i + 1;
                }
                else {
                    break;
                }

            }
            if (longestPrefix > currentBuffer.length()) {
                String commonPrefix = externalExecutable.getFirst().substring(0, longestPrefix);
                String substring = commonPrefix.substring(buffer.toString().length());
                buffer.append(substring);
                System.out.print(substring);
                return false;
            }
            System.out.print("\u0007");
            int c = in.read();
            if (c == -1) {
                return true;
            }
            if (c == '\t') {
                System.out.println();
                for (String temp : externalExecutable) {
                    System.out.print(temp + "  ");
                }
                System.out.println();
                System.out.print("$ " + buffer);
            }
            else {
                in.unread(c);
            }

        }
        else {
            System.out.print("\u0007");
        }
        return false;
    }

    public static String fileCompleter(String prefix) {
        try {
            List<String> files;
            String directoryPath;
            String filePrefix;
            if (prefix.endsWith("/")) {
                directoryPath = prefix;
                filePrefix = "";
            }
            else if (prefix.contains("/")) {
                List<String> prefixSplit = new java.util.ArrayList<>(List.of(prefix.split("/")));
                if (prefixSplit.size() < 2) {
                    return null;
                }
                filePrefix = prefixSplit.removeLast();
                directoryPath = String.join("/", prefixSplit);
            }
            else {
                directoryPath = "";
                filePrefix = prefix;
            }
            //TODO make this try with resource
            files = Files.list(Path.of(directoryPath))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .filter(string -> string.startsWith(filePrefix))
                    .toList();
            Optional<String> file = files.stream().findFirst();
            if (file.isPresent()) {
                if (Files.isDirectory(Path.of(directoryPath + file.orElseThrow()))) {
                    return file.orElseThrow().substring(filePrefix.length()) + "/";
                }
                return file.orElseThrow().substring(filePrefix.length());
            }
        }

        catch (IOException e) {
            return null;
        }
        return null;
    }
}
