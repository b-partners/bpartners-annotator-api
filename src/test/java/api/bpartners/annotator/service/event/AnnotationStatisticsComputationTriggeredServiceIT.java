package api.bpartners.annotator.service.event;

import static api.bpartners.annotator.integration.conf.utils.TestMocks.TEST_MAIL;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.aTestAnnotationBatch;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.aTestJob;
import static api.bpartners.annotator.integration.conf.utils.TestUtils.getInternetAddress;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import api.bpartners.annotator.conf.FacadeIT;
import api.bpartners.annotator.endpoint.event.model.AnnotationStatisticsComputationTriggered;
import api.bpartners.annotator.file.FileWriter;
import api.bpartners.annotator.mail.Email;
import api.bpartners.annotator.mail.Mailer;
import api.bpartners.annotator.repository.model.Job;
import api.bpartners.annotator.service.AnnotationBatchService;
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

class AnnotationStatisticsComputationTriggeredServiceIT extends FacadeIT {
  @Autowired private AnnotationStatisticsComputationTriggeredService subject;
  private static final String MOCK_JOB_ID = "MOCK_JOB_ID";
  private static final Job TEST_JOB = aTestJob(MOCK_JOB_ID);
  @MockBean private Mailer mailerMock;
  @MockBean private AnnotationBatchService annotationBatchServiceMock;
  @MockBean private FileWriter fileWriter;
  @MockBean private JobService jobServiceMock;

  @BeforeEach
  void setup() {
    File mockFile = getMockFile();
    when(jobServiceMock.getById(MOCK_JOB_ID)).thenReturn(TEST_JOB);
    when(annotationBatchServiceMock.findLatestPerTaskByJobId(any(String.class)))
        .thenReturn(List.of(aTestAnnotationBatch()));
    when(fileWriter.write(any(), any(), any())).thenReturn(mockFile);
    when(fileWriter.apply(any(), any())).thenReturn(mockFile);
  }

  private @NotNull File getMockFile() {
    return new FileSystemResource(
            this.getClass().getClassLoader().getResource("files/Blank.jpeg").getFile())
        .getFile();
  }

  @Test
  void shouldComputeStatistics() {
    Job currentJob = TEST_JOB;
    InternetAddress cc = getInternetAddress(TEST_MAIL);
    var ownerEmail = getInternetAddress(currentJob.getOwnerEmail());
    String mailSubject = "[Bpartners-Annotator] Calcul de statistiques de job";
    subject.accept(
        AnnotationStatisticsComputationTriggered.builder().jobId(MOCK_JOB_ID).emailCC(cc).build());

    verify(mailerMock, times(1))
        .accept(
            new Email(
                ownerEmail,
                List.of(cc),
                List.of(),
                mailSubject,
                getHtmlBody(currentJob),
                List.of(getMockFile())));
  }

  private static String getHtmlBody(Job job) {
    return "<!DOCTYPE html>\n"
        + "<html lang=\"en\" xmlns=\"http://www.w3.org/1999/html\">\n"
        + "<head>\n"
        + "    <meta charset=\"UTF-8\">\n"
        + "    <title>Job Statistics Computation Finished</title>\n"
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
        + "<p>Les calculs des statistiques d'annotations du job \"<span>"
        + job.getName()
        + "</span>\" (id=<span>"
        + job.getId()
        + "</span>) ont été effectués et le résultat vous est disponible en pièces jointes</p>\n"
        + "<br/>\n"
        + "<p>BPartners, l'assistant intelligent Tout-en-Un, qui accélère la croissance des"
        + " artisans & indépendants\n"
        + "    français.</p>\n"
        + "</body>\n"
        + "</html>";
  }
}
