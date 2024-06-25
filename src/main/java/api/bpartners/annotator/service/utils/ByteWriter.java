package api.bpartners.annotator.service.utils;

import static api.bpartners.annotator.model.exception.ApiException.ExceptionType.SERVER_EXCEPTION;
import static java.util.UUID.randomUUID;

import api.bpartners.annotator.file.ExtensionGuesser;
import api.bpartners.annotator.model.exception.ApiException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.nio.file.Files;
import java.util.function.Function;
import javax.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ByteWriter implements Function<Object, byte[]> {
  private final ObjectMapper objectMapper;
  private final ExtensionGuesser extensionGuesser;

  @Override
  public byte[] apply(Object object) {
    try {
      return objectMapper.writeValueAsBytes(object);
    } catch (JsonProcessingException e) {
      throw new ApiException(SERVER_EXCEPTION, "error during object conversion to bytes");
    }
  }

  @SneakyThrows
  public File writeAsFile(byte[] bytes, @Nullable File directory) {
    String name = randomUUID().toString();
    String suffix = "." + extensionGuesser.apply(bytes);
    File tempFile = File.createTempFile(name, suffix, directory);
    return Files.write(tempFile.toPath(), bytes).toFile();
  }

  @SneakyThrows
  public File writeAsFile(byte[] bytes, @Nullable File directory, String filename) {
    String suffix = extensionGuesser.apply(bytes);
    File newFile = new File(directory, filename + suffix);
    Files.createDirectories(newFile.toPath().getParent());
    return Files.write(newFile.toPath(), bytes).toFile();
  }
}
