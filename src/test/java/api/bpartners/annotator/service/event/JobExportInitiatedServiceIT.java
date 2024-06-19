package api.bpartners.annotator.service.event;

import static api.bpartners.annotator.endpoint.rest.model.ExportFormat.COCO;
import static api.bpartners.annotator.endpoint.rest.model.ExportFormat.VGG;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.MOCK_SUBSET_ID;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.TEST_MAIL;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.aTestAnnotationBatch;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.aTestJob;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.mockSubset;
import static api.bpartners.annotator.integration.conf.utils.TestUtils.getInternetAddress;
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
import api.bpartners.annotator.repository.model.Job;
import api.bpartners.annotator.service.AnnotationBatchService;
import api.bpartners.annotator.service.AnnotationBatchSubsetService;
import api.bpartners.annotator.service.JobService;
import jakarta.mail.internet.InternetAddress;
import java.io.File;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.FileSystemResource;

class JobExportInitiatedServiceIT extends FacadeIT {
  private static final String COCO_JOB_ID = "COCO_JOB_ID";
  private static final String VGG_JOB_ID = "VGG_JOB_ID";
  @Autowired private JobExportInitiatedService subject;
  @MockBean private Mailer mailerMock;
  @MockBean private AnnotationBatchService annotationBatchServiceMock;
  @MockBean AnnotationBatchSubsetService annotationBatchSubsetServiceMock;
  @MockBean private FileWriter fileWriter;
  @MockBean private JobService jobService;

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
        .thenReturn(List.of(aTestAnnotationBatch()));
    when(fileWriter.write(any(), any(), any())).thenReturn(mockFile);
    when(fileWriter.apply(any(), any())).thenReturn(mockFile);
    when(jobService.getById(COCO_JOB_ID)).thenReturn(aTestJob(COCO_JOB_ID));
    when(jobService.getById(VGG_JOB_ID)).thenReturn(aTestJob(VGG_JOB_ID));
    when(annotationBatchSubsetServiceMock.getSubSetById(any())).thenReturn(mockSubset());
    when(annotationBatchSubsetServiceMock.save(any())).thenReturn(mockSubset());
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
    Job linkedJob = aTestJob(COCO_JOB_ID);
    String mailSubject = "[Bpartners-Annotator] Exportation de job sous format " + exportFormat;

    subject.accept(new JobExportInitiated(linkedJob.getId(), MOCK_SUBSET_ID, exportFormat, cc));

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
    Job linkedJob = aTestJob(VGG_JOB_ID);
    String mailSubject = "[Bpartners-Annotator] Exportation de job sous format " + exportFormat;

    subject.accept(new JobExportInitiated(linkedJob.getId(), MOCK_SUBSET_ID, exportFormat, cc));

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
    Job linkedJob = aTestJob(VGG_JOB_ID);

    assertThrows(
        BadRequestException.class,
        () ->
            subject.accept(
                new JobExportInitiated(linkedJob.getId(), MOCK_SUBSET_ID, invalidExportFormat, cc)),
        "unknown export format " + invalidExportFormat);
  }
}
