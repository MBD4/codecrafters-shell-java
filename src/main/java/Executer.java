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
     * @param currentDirectory the working directory in which the executable process should run
     */
    public static Output runExecutableFile(Input input, Path currentDirectory, Redirect redirect) {
        try {
            List<String> pbArgs = new ArrayList<>(input.args());
            pbArgs.addFirst(input.command());
            ProcessBuilder pb = new ProcessBuilder(pbArgs);
            pb.directory(currentDirectory.toFile());
            if (redirect != Redirect.STDERR) {
                pb.redirectError(ProcessBuilder.Redirect.INHERIT);
            }
            if (redirect != Redirect.STDOUT) {
                pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            }
            try (Process process = pb.start()) {

                String stdOut = null;
                String stdErr = null;
                String rString = null;
                if (redirect == Redirect.STDOUT) {
                    BufferedReader readerStd = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line;

                    StringBuilder sb = new StringBuilder();
                    while ((line = readerStd.readLine()) != null) {
                        sb.append(line).append(System.lineSeparator());
                    }
                    stdOut = sb.toString();

                    process.waitFor();

                    return new Output(currentDirectory, stdOut, stdErr);
                }
                else if (redirect == Redirect.STDERR) {
                    BufferedReader readerStd = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                    String line;

                    StringBuilder sb = new StringBuilder();
                    while ((line = readerStd.readLine()) != null) {
                        sb.append(line).append(System.lineSeparator());
                    }
                    stdErr = sb.toString();

                    process.waitFor();

                    return new Output(currentDirectory, stdOut, stdErr);
                }
                else {
                    process.waitFor();
                    return new Output(currentDirectory, stdOut, stdErr);
                }

            }

        } catch (Exception e) {
            throw new RuntimeException("this line in Executer should never be reached");
        }
    }
}
