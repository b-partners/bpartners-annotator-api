package api.bpartners.annotator.endpoint.rest.validator;

import api.bpartners.annotator.endpoint.rest.model.CrupdateJob;
import api.bpartners.annotator.model.exception.BadRequestException;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class JobValidator implements Consumer<CrupdateJob> {
  private static final Pattern VALID_FOLDER_PATH_PATTERN = Pattern.compile("^(?!/).+/$");
  private static final Pattern VALID_EMAIL_PATTERN =
      Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

  @Override
  public void accept(CrupdateJob crupdateJob) {
    StringBuilder exceptionMessageBuilder = new StringBuilder();
    if (crupdateJob.getId() == null) {
      exceptionMessageBuilder.append("ID is mandatory.");
    }
    if (!isValidFolderPath(crupdateJob.getFolderPath())) {
      exceptionMessageBuilder
          .append("folder path: ")
          .append(crupdateJob.getFolderPath())
          .append("does not follow regex ")
          .append(VALID_FOLDER_PATH_PATTERN.pattern());
    }
    if (!isValidEmailAddress(crupdateJob.getOwnerEmail())) {
      exceptionMessageBuilder
          .append("email address : ")
          .append(crupdateJob.getOwnerEmail())
          .append("does not follow regex ")
          .append(VALID_EMAIL_PATTERN.pattern());
    }
    String exceptionMessage = exceptionMessageBuilder.toString();
    if (!exceptionMessage.isBlank()) {
      throw new BadRequestException(exceptionMessage);
    }
  }

  private boolean isValidFolderPath(String folderPath) {
    return VALID_FOLDER_PATH_PATTERN.matcher(folderPath).matches();
  }

  private boolean isValidEmailAddress(String email) {
    return VALID_EMAIL_PATTERN.matcher(email).matches();
  }
}
