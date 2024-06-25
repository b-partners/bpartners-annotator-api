package api.bpartners.annotator.service;

import static api.bpartners.annotator.endpoint.rest.model.ExportFormat.COCO;
import static api.bpartners.annotator.endpoint.rest.model.ExportFormat.VGG;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.aTestAnnotationBatch;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.aTestJob;
import static api.bpartners.annotator.integration.conf.utils.TestUtils.getInternetAddress;
import static java.time.Instant.now;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import api.bpartners.annotator.conf.FacadeIT;
import api.bpartners.annotator.endpoint.event.EventProducer;
import api.bpartners.annotator.endpoint.event.model.JobExportInitiated;
import api.bpartners.annotator.endpoint.rest.model.ExportFormat;
import api.bpartners.annotator.model.exception.ApiException;
import api.bpartners.annotator.repository.model.ExportTask;
import api.bpartners.annotator.repository.model.Job;
import api.bpartners.annotator.service.JobExport.ExportService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

class ExportServiceIT extends FacadeIT {
  private static final String MOCK_JOB_ID = "mock_job_id";
  private static final Job TEST_JOB = aTestJob(MOCK_JOB_ID);
  @MockBean EventProducer eventProducerMock;
  @MockBean JobService jobServiceMock;
  @MockBean AnnotationBatchService annotationBatchServiceMock;
  @MockBean ExportTaskService exportTaskServiceMock;
  @Autowired ObjectMapper objectMapper;
  @Autowired private ExportService subject;

  @BeforeEach
  void setUp() {
    when(jobServiceMock.getById(MOCK_JOB_ID)).thenReturn(TEST_JOB);
    when(annotationBatchServiceMock.findLatestPerTaskByJobId(MOCK_JOB_ID))
        .thenReturn(List.of(aTestAnnotationBatch()));
    when(exportTaskServiceMock.getTaskById(any())).thenReturn(exportTask());
  }

  ExportTask exportTask() {
    return ExportTask.builder()
        .id(randomUUID().toString())
        .jobId(MOCK_JOB_ID)
        .submissionInstant(now())
        .statusHistory(new ArrayList<>())
        .annotationBatches(List.of(aTestAnnotationBatch()))
        .build();
  }

  @Test
  void initiate_export_ok() {
    String testMail = "test@gmail.com";
    ExportFormat exportFormat = VGG;
    subject.initiateJobExport(MOCK_JOB_ID, exportFormat, testMail);

    verify(eventProducerMock)
        .accept(
            List.of(
                new JobExportInitiated(
                    TEST_JOB.getId(), exportFormat, getInternetAddress(testMail))));
  }

  @Test
  void export_vgg_job_ok() {
    Job testJob = TEST_JOB;
    var actual = subject.export(testJob, exportTask().getId(), VGG);

    assertEquals(getVggTestFile(testJob), actual);
  }

  @Test
  void export_coco_job_ok() {
    Job testJob = TEST_JOB;
    var actual = subject.export(testJob, exportTask().getJobId(), COCO);

    assertEquals(getCocoTestFile(testJob), actual);
  }

  @SneakyThrows
  private api.bpartners.annotator.model.VGG getVggTestFile(Job job) {
    var is = getResourceInputStream("files/vgg_" + job.getId() + ".json");
    return objectMapper.readValue(is, api.bpartners.annotator.model.VGG.class);
  }

  @SneakyThrows
  private api.bpartners.annotator.model.COCO getCocoTestFile(Job job) {
    var is = getResourceInputStream("files/coco_" + job.getId() + ".json");
    return objectMapper.readValue(is, api.bpartners.annotator.model.COCO.class);
  }

  private InputStream getResourceInputStream(String filename) {
    return this.getClass().getClassLoader().getResourceAsStream(filename);
  }

  @Test
  void export_job_ko() {
    ExportFormat invalidExportFormat = null;
    assertThrows(
        ApiException.class,
        () -> subject.export(TEST_JOB, exportTask().getId(), invalidExportFormat),
        "unknown export format " + invalidExportFormat);
  }
}
