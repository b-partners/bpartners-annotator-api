package api.bpartners.annotator.service.event;

import static api.bpartners.annotator.endpoint.rest.model.ExportFormat.COCO;
import static api.bpartners.annotator.endpoint.rest.model.ExportFormat.VGG;
import static api.bpartners.annotator.endpoint.rest.model.JobType.LABELLING;
import static api.bpartners.annotator.repository.model.enums.JobStatus.COMPLETED;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import api.bpartners.annotator.conf.FacadeIT;
import api.bpartners.annotator.endpoint.event.model.JobExportInitiated;
import api.bpartners.annotator.endpoint.rest.model.ExportFormat;
import api.bpartners.annotator.file.FileWriter;
import api.bpartners.annotator.mail.Email;
import api.bpartners.annotator.mail.Mailer;
import api.bpartners.annotator.model.exception.BadRequestException;
import api.bpartners.annotator.repository.model.Annotation;
import api.bpartners.annotator.repository.model.AnnotationBatch;
import api.bpartners.annotator.repository.model.Job;
import api.bpartners.annotator.repository.model.Label;
import api.bpartners.annotator.repository.model.Task;
import api.bpartners.annotator.repository.model.enums.TaskStatus;
import api.bpartners.annotator.service.AnnotationBatchService;
import jakarta.mail.internet.InternetAddress;
import java.io.File;
import java.time.Instant;
import java.util.List;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.FileSystemResource;

class JobExportInitiatedServiceIT extends FacadeIT {
  private static final String COCO_JOB_ID = "COCO_JOB_ID";
  private static final String VGG_JOB_ID = "VGG_JOB_ID";
  private static final String USER_ID = "userId";
  private static final String TASK_ID = "task_id";
  private static final String TEST_MAIL = "mail@test.com";
  @Autowired private JobExportInitiatedService subject;
  @MockBean private Mailer mailerMock;
  @MockBean private AnnotationBatchService annotationBatchServiceMock;
  @MockBean private FileWriter fileWriter;

  private static Job aJob(String id) {
    return Job.builder()
        .id(id)
        .bucketName("test_bucket")
        .folderPath("test_folder_path/")
        .name("test_name")
        .ownerEmail("test@test.com")
        .status(COMPLETED)
        .teamId("test_team_id")
        .type(LABELLING)
        .tasks(List.of(aTask()))
        .imagesHeight(256)
        .imagesWidth(256)
        .labels(List.of(aLabel()))
        .build();
  }

  private static Task aTask() {
    return Task.builder()
        .id(TASK_ID)
        .filename("filename.jpg")
        .job(null)
        .status(TaskStatus.COMPLETED)
        .userId(USER_ID)
        .build();
  }

  private static Label aLabel() {
    return Label.builder().id("label_id").name("label_name").color("#123123").build();
  }

  private static AnnotationBatch anAnnotationBatch() {
    String batchId = "batch_id";
    return AnnotationBatch.builder()
        .id(batchId)
        .annotations(List.of(anAnnotation(batchId)))
        .annotatorId(USER_ID)
        .creationTimestamp(Instant.MIN)
        .task(aTask())
        .build();
  }

  private static Annotation anAnnotation(String batchId) {
    return Annotation.builder()
        .id("annotation_id")
        .label(aLabel())
        .taskId(TASK_ID)
        .userId(USER_ID)
        .batchId(batchId)
        .polygon(
            Annotation.Polygon.builder().points(List.of(new Annotation.Point(1.0, 1.0))).build())
        .build();
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

  @BeforeEach
  void setup() {
    File mockFile = getMockFile();
    when(annotationBatchServiceMock.findLatestPerTaskByJobId(any(String.class)))
        .thenReturn(List.of(anAnnotationBatch()));
    when(fileWriter.write(any(), any(), any())).thenReturn(mockFile);
    when(fileWriter.apply(any(), any())).thenReturn(mockFile);
  }

  private @NotNull File getMockFile() {
    return new FileSystemResource(
            this.getClass().getClassLoader().getResource("files/Blank.jpeg").getFile())
        .getFile();
  }

  @Test
  void send_exported_coco_job_as_email_ok() {
    InternetAddress cc = getInternetAddress(TEST_MAIL);
    ExportFormat exportFormat = COCO;
    Job linkedJob = aJob(COCO_JOB_ID);
    String mailSubject = "[Bpartners-Annotator] Exportation de job sous format " + exportFormat;

    subject.accept(new JobExportInitiated(linkedJob, exportFormat, cc));

    // verify(byteWriter.apply());
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

  @Test
  void send_exported_vgg_job_as_email_ok() {
    InternetAddress cc = getInternetAddress(TEST_MAIL);
    ExportFormat exportFormat = VGG;
    Job linkedJob = aJob(VGG_JOB_ID);
    String mailSubject = "[Bpartners-Annotator] Exportation de job sous format " + exportFormat;

    subject.accept(new JobExportInitiated(linkedJob, exportFormat, cc));

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

  @Test
  void send_exported_job_as_email_ko() {
    InternetAddress cc = getInternetAddress(TEST_MAIL);
    ExportFormat invalidExportFormat = null;
    Job linkedJob = aJob(VGG_JOB_ID);

    assertThrows(
        BadRequestException.class,
        () -> subject.accept(new JobExportInitiated(linkedJob, invalidExportFormat, cc)),
        "unknown export format " + invalidExportFormat);
  }

  @SneakyThrows
  private static @NotNull InternetAddress getInternetAddress(String email) {
    return new InternetAddress(email);
  }
}
