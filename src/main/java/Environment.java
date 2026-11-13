import javax.imageio.IIOException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Environment {
    /**
     Checks if file is in PATH and is executable
     @param file the file to check
     @return the executable files full path or null if the file doesn't exist in path or isn't executable
     */
    public static String locateExecutableFileInPATH(String file) {
        String PATH = System.getenv("PATH");
        if (PATH == null) {
            return null;
        }
        String[] PATHList = PATH.split(":");
        for (String s: PATHList) {
            if (Files.isExecutable(Path.of(s, file))) {
                return s + "/" + file;
            }
        }
        return null;
    }

    public static String locateExecutableFileInPATHFromPrefix(String prefix) {
        String PATH = System.getenv("PATH");
        if (PATH == null) {
            return null;
        }
        String[] PATHArr = PATH.split(":");
        List<Path> PATHList = Arrays.stream(PATHArr).map(Path::of).toList();
        for (Path p: PATHList) {
            Optional<Path> resultOptional;
            try {
                resultOptional = Files.list(p)
                        .map(String::valueOf)
                        .filter(s -> s.startsWith(String.valueOf(p) + "/" + prefix))
                        .map(Path::of)
                        .findFirst();
            } catch (IOException e) {
                return null;
            }
            if (resultOptional.isPresent()) {
                Path result = resultOptional.orElseThrow();
                if (!Files.isExecutable(result)) {
                    continue;
                }
                String[] resultArr = String.valueOf(result).split("/");
                if (resultArr.length < 1) {
                    throw new RuntimeException("This should never happen");
                }
                return resultArr[resultArr.length - 1].split(prefix)[1];
            }
        }

        return null;
    }

    public static String getHomeEnvironmentPath() {
        return System.getenv("HOME");
    }
}
