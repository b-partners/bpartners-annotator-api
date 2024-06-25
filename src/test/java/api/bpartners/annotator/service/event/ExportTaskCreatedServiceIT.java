package api.bpartners.annotator.service.event;

import static api.bpartners.annotator.endpoint.rest.model.ExportFormat.COCO;
import static api.bpartners.annotator.endpoint.rest.model.ExportFormat.VGG;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.aTestAnnotationBatch;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.aTestJob;
import static api.bpartners.annotator.integration.conf.utils.TestUtils.getInternetAddress;
import static java.time.Instant.now;
import static java.util.UUID.randomUUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import api.bpartners.annotator.conf.FacadeIT;
import api.bpartners.annotator.endpoint.event.model.ExportTaskCreated;
import api.bpartners.annotator.endpoint.rest.model.ExportFormat;
import api.bpartners.annotator.mail.Email;
import api.bpartners.annotator.mail.Mailer;
import api.bpartners.annotator.repository.model.ExportTask;
import api.bpartners.annotator.repository.model.Job;
import api.bpartners.annotator.service.ExportTaskService;
import api.bpartners.annotator.service.ExportTaskStatusService;
import api.bpartners.annotator.service.JobExport.ExportService;
import api.bpartners.annotator.service.JobService;
import api.bpartners.annotator.service.utils.ByteWriter;
import jakarta.mail.internet.InternetAddress;
import java.io.File;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.FileSystemResource;

class ExportTaskCreatedServiceIT extends FacadeIT {
  private static final String COCO_JOB_ID = "COCO_JOB_ID";
  private static final String VGG_JOB_ID = "VGG_JOB_ID";
  private static final String EXPORT_TASK_ID = "export_task_id";
  private static final String TEST_MAIL = "test@example.com";
  private static final String MAIL_SUBJECT_TEMPLATE =
      "[Bpartners-Annotator] Exportation de job sous format ";

  @Autowired private ExportTaskCreatedService subject;

  @MockBean private ExportTaskService exportTaskServiceMock;
  @MockBean private ExportService exportServiceMock;
  @MockBean private ExportTaskStatusService exportTaskStatusService;
  @MockBean private JobService jobService;
  @MockBean private Mailer mailerMock;
  @MockBean private ByteWriter writer;

  @BeforeEach
  void setup() {
    File mockFile = getMockFile();
    when(writer.writeAsFile(any(), any(), any())).thenReturn(mockFile);
    when(writer.writeAsFile(any(), any())).thenReturn(mockFile);
    when(jobService.getById(COCO_JOB_ID)).thenReturn(aTestJob(COCO_JOB_ID));
    when(jobService.getById(VGG_JOB_ID)).thenReturn(aTestJob(VGG_JOB_ID));
    when(exportTaskServiceMock.getTaskById(any())).thenReturn(exportTask());
  }

  @Test
  void send_exported_coco_job_as_email_ok() {
    verifyEmailSending(COCO_JOB_ID, COCO);
  }

  @Test
  void send_exported_vgg_job_as_email_ok() {
    verifyEmailSending(VGG_JOB_ID, VGG);
  }

  private void verifyEmailSending(String jobId, ExportFormat exportFormat) {
    InternetAddress cc = getInternetAddress(TEST_MAIL);
    Job linkedJob = aTestJob(jobId);
    String mailSubject = MAIL_SUBJECT_TEMPLATE + exportFormat;

    subject.accept(new ExportTaskCreated(linkedJob.getId(), EXPORT_TASK_ID, exportFormat, cc));

    verify(mailerMock, times(1))
        .accept(
            new Email(
                getInternetAddress(linkedJob.getOwnerEmail()),
                List.of(cc),
                List.of(),
                mailSubject,
                getHtmlBody(linkedJob),
                List.of(getMockFile())));
  }

  private static @NotNull String getHtmlBody(Job job) {
    return "<!DOCTYPE html>\n"
        + "<html lang=\"en\" xmlns=\"http://www.w3.org/1999/html\">\n"
        + "<head>\n"
        + "    <meta charset=\"UTF-8\">\n"
        + "    <title>Job Export Finished</title>\n"
        + "    <style>\n"
        + "        * {\n"
        + "            font-family: Arial, Verdana, Georgia, and Courier, serif;\n"
        + "            color: black;\n"
        + "        }\n"
        + "    </style>\n"
        + "</head>\n"
        + "<body>\n"
        + "<p>\n"
        + "    Bonjour,\n"
        + "</p>\n"
        + "<p>L'export du job \"<span>"
        + job.getName()
        + "</span>\" (id=<span>"
        + job.getId()
        + "</span>) a été effectué et vous est disponible en pièces jointes</p>\n"
        + "<br/>\n"
        + "<p>BPartners, l'assistant intelligent Tout-en-Un, qui accélère la croissance des"
        + " artisans & indépendants\n"
        + "    français.</p>\n"
        + "</body>\n"
        + "</html>";
  }

  private ExportTask exportTask() {
    return ExportTask.builder()
        .id(randomUUID().toString())
        .statusHistory(List.of())
        .jobId(COCO_JOB_ID)
        .annotationBatches(List.of(aTestAnnotationBatch()))
        .submissionInstant(now())
        .build();
  }

  private @NotNull File getMockFile() {
    return new FileSystemResource(
            this.getClass().getClassLoader().getResource("files/Blank.jpeg").getFile())
        .getFile();
  }
}
