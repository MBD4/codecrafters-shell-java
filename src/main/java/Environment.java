import javax.imageio.IIOException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
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

    public static List<String> locateExecutableFileInPATHFromPrefix(String prefix) {
        String PATH = System.getenv("PATH");
        if (PATH == null) {
            return null;
        }
        String[] PATHArr = PATH.split(":");
        List<Path> PATHList = Arrays.stream(PATHArr).map(Path::of).toList();
        List<Path> resultList = new ArrayList<>();
        for (Path p: PATHList) {
            List<Path> tempResultList;
            try {
                tempResultList = Files.list(p)
                        .map(String::valueOf)
                        .filter(s -> s.startsWith(String.valueOf(p) + "/" + prefix))
                        .map(Path::of)
                        .toList();
            } catch (IOException e) {
                continue;
            }
            resultList.addAll(tempResultList);
        }
        if (!resultList.isEmpty()) {
            resultList = resultList.stream().filter(Files::isExecutable).toList();
            if (resultList.size() == 1) {
                String[] resultArr = String.valueOf(resultList.getFirst()).split("/");
                return List.of(resultArr[resultArr.length - 1].split(prefix)[1]);
            }
            List<String> returnList = new ArrayList<>();
            for (Path temp: resultList) {
                String[] tempArr = String.valueOf(temp).split("/");
                returnList.add(tempArr[tempArr.length - 1]);
            }
            returnList = returnList.stream().sorted().toList();
            return returnList;
        }

        return null;
    }

    public static String getHomeEnvironmentPath() {
        return System.getenv("HOME");
    }
}
