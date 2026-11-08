import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Executer {

    /**
     * Runs an executable command with its arguments in the specified working directory.
     * The process standard error stream is merged with the standard output stream, and
     * all output produced by the process is printed to the standard output. This method
     * waits for the process to finish before returning. Any exception that occurs while
     * starting, reading from, or waiting for the process is caught and printed.
     *
     * @param input the command and its arguments, where the first element is the executable name or path
     * @param dir the working directory in which the executable process should run
     */
    public static String runExecutableFile(Input input, Path dir, boolean redirect) {
        try {
            List<String> pbArgs = new ArrayList<>(input.args());
            pbArgs.addFirst(input.command());
            ProcessBuilder pb = new ProcessBuilder(pbArgs);
            pb.directory(dir.toFile());
            pb.redirectError(ProcessBuilder.Redirect.INHERIT);
            if (!redirect) {
                pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            }
            try (Process process = pb.start()) {

                // Read the program's output
                BufferedReader readerStd = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                String rString = null;

                StringBuilder sb = new StringBuilder();
                while ((line = readerStd.readLine()) != null) {
                    sb.append(line).append(System.lineSeparator());
                }
                if (redirect) {
                    rString = sb.toString();
                }


                // Wait for the program to finish and get the exit code
                process.waitFor();

                return rString;
            }

        } catch (Exception e) {
            return e.toString();
        }
    }
}
