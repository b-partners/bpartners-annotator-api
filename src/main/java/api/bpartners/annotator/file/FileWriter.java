package api.bpartners.annotator.file;

import static java.util.UUID.randomUUID;

import java.io.File;
import java.nio.file.Files;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class FileWriter implements BiFunction<byte[], File, File> {
  private final ExtensionGuesser extensionGuesser;

  @Override
  @SneakyThrows
  public File apply(byte[] bytes, @Nullable File directory) {
    String name = randomUUID().toString();
    String suffix = "." + extensionGuesser.apply(bytes);
    File tempFile = File.createTempFile(name, suffix, directory);
    return Files.write(tempFile.toPath(), bytes).toFile();
  }

  @SneakyThrows
  public File write(byte[] bytes, @Nullable File directory, String filename) {
    String suffix = extensionGuesser.apply(bytes);
    File newFile = new File(directory, filename + suffix);
    Files.createDirectories(newFile.toPath().getParent());
    return Files.write(newFile.toPath(), bytes).toFile();
  }
}
