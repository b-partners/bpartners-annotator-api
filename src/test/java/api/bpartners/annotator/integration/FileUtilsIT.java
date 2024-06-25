package api.bpartners.annotator.integration;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;

import api.bpartners.annotator.conf.FacadeIT;
import api.bpartners.annotator.file.ExtensionGuesser;
import api.bpartners.annotator.service.utils.ByteWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class FileUtilsIT extends FacadeIT {
  private static final String TXT_EXTENSION = ".txt";
  @Autowired ByteWriter writer;
  @Autowired ExtensionGuesser extensionGuesser;
  @Autowired ByteWriter byteWriter;

  @Test
  void write_file_and_guess_extension_ok() throws IOException {
    String expectedContent = "haha";
    byte[] expectedContentAsBytes = expectedContent.getBytes(UTF_8);
    String directoryName = randomUUID().toString();
    String filename = randomUUID() + ".txt";
    File directory = Files.createTempDirectory(directoryName).toFile();

    File newFile = writer.writeAsFile(expectedContentAsBytes, directory, filename);
    File newFile2 = writer.writeAsFile(expectedContentAsBytes, directory);
    var newFileContent = Files.readAllBytes(newFile.toPath());
    var newFile2Content = Files.readAllBytes(newFile2.toPath());
    var newFileExtension = extensionGuesser.apply(newFileContent);
    var newFile2Extension = extensionGuesser.apply(newFile2Content);

    assertEquals(expectedContent, new String(newFileContent));
    assertEquals(expectedContent, new String(newFile2Content));
    assertEquals(TXT_EXTENSION, newFile2Extension);
    assertEquals(TXT_EXTENSION, newFileExtension);
  }

  @Test
  void byte_writer_writes_bytes_ok() {
    String expectedContent = "hahaha";
    var actual = byteWriter.apply(expectedContent);

    // written as json
    assertEquals("\"" + expectedContent + "\"", new String(actual));
  }
}
