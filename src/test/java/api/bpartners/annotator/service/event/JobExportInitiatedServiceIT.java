package api.bpartners.annotator.service.event;

import static api.bpartners.annotator.endpoint.rest.model.ExportFormat.COCO;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.TEST_MAIL;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.aTestAnnotationBatch;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.aTestJob;
import static api.bpartners.annotator.integration.conf.utils.TestUtils.getInternetAddress;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import api.bpartners.annotator.conf.FacadeIT;
import api.bpartners.annotator.endpoint.event.EventProducer;
import api.bpartners.annotator.endpoint.event.model.JobExportInitiated;
import api.bpartners.annotator.endpoint.rest.model.ExportFormat;
import api.bpartners.annotator.repository.model.ExportTask;
import api.bpartners.annotator.repository.model.Job;
import api.bpartners.annotator.service.AnnotationBatchService;
import api.bpartners.annotator.service.ExportTaskService;
import jakarta.mail.internet.InternetAddress;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

class JobExportInitiatedServiceIT extends FacadeIT {
  private static final String COCO_JOB_ID = "COCO_JOB_ID";
  @Autowired private JobExportInitiatedService subject;
  @MockBean private AnnotationBatchService annotationBatchServiceMock;
  @MockBean private ExportTaskService exportTaskService;
  @MockBean private EventProducer eventProducer;

  @BeforeEach
  void setup() {
    when(annotationBatchServiceMock.findLatestPerTaskByJobId(any(String.class)))
        .thenReturn(List.of(aTestAnnotationBatch()));
    when(exportTaskService.saveAll(anyList())).thenReturn(List.of(new ExportTask()));
  }

  @Test
  void split_export_job_into_multiple_tasks_ok() {
    InternetAddress cc = getInternetAddress(TEST_MAIL);
    ExportFormat exportFormat = COCO;
    Job linkedJob = aTestJob(COCO_JOB_ID);

    subject.accept(new JobExportInitiated(linkedJob.getId(), exportFormat, cc));

    verify(eventProducer, times(1)).accept(anyList());
  }
}
