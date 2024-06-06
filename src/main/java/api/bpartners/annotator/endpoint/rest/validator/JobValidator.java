package api.bpartners.annotator.endpoint.rest.validator;

import api.bpartners.annotator.endpoint.rest.model.CrupdateJob;
import api.bpartners.annotator.model.exception.BadRequestException;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class JobValidator implements Consumer<CrupdateJob> {
  private final LabelValidator labelValidator;
  private static final Pattern VALID_FOLDER_PATH_PATTERN = Pattern.compile("^(?!/).+/$");
  private static final Pattern VALID_EMAIL_PATTERN =
      Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

  @Override
  public void accept(CrupdateJob crupdateJob) {
    StringBuilder exceptionMessageBuilder = new StringBuilder();
    if (!isValidFolderPath(crupdateJob.getFolderPath())) {
      exceptionMessageBuilder
          .append("folder path: ")
          .append(crupdateJob.getFolderPath())
          .append(" does not follow regex ")
          .append(VALID_FOLDER_PATH_PATTERN.pattern())
          .append(".");
    }
    if (crupdateJob.getOwnerEmail() == null) {
      exceptionMessageBuilder.append("Owner Email is mandatory.");
    } else if (!isValidEmailAddress(crupdateJob.getOwnerEmail())) {
      exceptionMessageBuilder
          .append("email address : ")
          .append(crupdateJob.getOwnerEmail())
          .append("does not follow regex ")
          .append(VALID_EMAIL_PATTERN.pattern())
          .append(".");
    }
    if (crupdateJob.getImagesHeight() == null) {
      exceptionMessageBuilder.append("Images Height is mandatory.");
    }
    if (crupdateJob.getImagesWidth() == null) {
      exceptionMessageBuilder.append("Images Width is mandatory.");
    }
    if (crupdateJob.getLabels() == null || crupdateJob.getLabels().isEmpty()) {
      exceptionMessageBuilder.append("Labels are mandatory.");
    } else if (crupdateJob.getLabels() != null) {
      labelValidator.accept(crupdateJob.getLabels());
    }
    String exceptionMessage = exceptionMessageBuilder.toString();
    if (!exceptionMessage.isBlank()) {
      throw new BadRequestException(exceptionMessage);
    }
  }

  private boolean isValidFolderPath(String folderPath) {
    if (folderPath == null) {
      return true;
    }
    return VALID_FOLDER_PATH_PATTERN.matcher(folderPath).matches();
  }

  private boolean isValidEmailAddress(String email) {
    return VALID_EMAIL_PATTERN.matcher(email).matches();
  }
}
