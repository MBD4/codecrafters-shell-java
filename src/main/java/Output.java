import java.nio.file.Path;

public record Output(Path directory, String stdOut, String stdErr) {
}