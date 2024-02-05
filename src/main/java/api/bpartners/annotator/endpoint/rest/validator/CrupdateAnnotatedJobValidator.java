package api.bpartners.annotator.endpoint.rest.validator;

import api.bpartners.annotator.endpoint.rest.model.CrupdateAnnotatedJob;
import api.bpartners.annotator.model.exception.BadRequestException;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CrupdateAnnotatedJobValidator implements Consumer<CrupdateAnnotatedJob> {
  private final AnnotatedTasksValidator annotatedTasksValidator;

  @Override
  public void accept(CrupdateAnnotatedJob crupdateAnnotatedJob) {
    if (crupdateAnnotatedJob.getAnnotatedTasks() == null) {
      throw new BadRequestException("annotatedJob.annotatedTasks are mandatory");
    } else {
      annotatedTasksValidator.accept(crupdateAnnotatedJob.getAnnotatedTasks());
    }
  }
}
