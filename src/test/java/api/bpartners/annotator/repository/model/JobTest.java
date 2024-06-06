package api.bpartners.annotator.repository.model;

import static api.bpartners.annotator.endpoint.rest.model.JobType.LABELLING;
import static api.bpartners.annotator.endpoint.rest.model.JobType.REVIEWING;
import static api.bpartners.annotator.repository.model.enums.JobStatus.COMPLETED;
import static api.bpartners.annotator.repository.model.enums.JobStatus.TO_REVIEW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;

class JobTest {
  @Test
  void get_task_statistics_ko() {
    var job = new Job();
    assertThrows(
        AssertionError.class,
        () -> job.getTaskStatistics(null, List.of()),
        "UserId value missing.");
    assertThrows(
        AssertionError.class,
        () -> job.getTaskStatistics("", null),
        "externalAnnotatorUserId value missing.");
  }

  @Test
  void job_equals_hashcode() {
    var job =
        Job.builder()
            .id("id")
            .imagesHeight(10)
            .imagesWidth(10)
            .name("name")
            .bucketName("bucketName")
            .folderPath("folderPath/")
            .ownerEmail("ownerEmail@hotmail.com")
            .status(COMPLETED)
            .teamId("teamId")
            .type(REVIEWING)
            .build();
    var job1 =
        Job.builder()
            .id("id")
            .imagesHeight(10)
            .imagesWidth(10)
            .name("name")
            .bucketName("bucketName")
            .folderPath("folderPath/")
            .ownerEmail("ownerEmail@hotmail.com")
            .status(COMPLETED)
            .teamId("teamId")
            .type(REVIEWING)
            .build();
    var job2 =
        Job.builder()
            .id("id")
            .imagesHeight(10)
            .imagesWidth(10)
            .name("name")
            .bucketName("bucketName")
            .folderPath("folderPath/")
            .ownerEmail("ownerEmail@hotmail.com")
            .status(COMPLETED)
            .teamId("teamId")
            .type(REVIEWING)
            .build();
    var job3 =
        Job.builder()
            .id("id_changed")
            .imagesHeight(100)
            .imagesWidth(100)
            .name("name_changed")
            .bucketName("bucketName_changed")
            .folderPath("new_folderPath/")
            .ownerEmail("newownerEmail@hotmail.com")
            .status(TO_REVIEW)
            .teamId("teamId_changed")
            .type(LABELLING)
            .build();

    assertEquals(job, job1);
    assertEquals(job1, job);
    assertEquals(job2, job);
    assertEquals(job2, job);
    assertNotEquals(job, job3);
    assertNotEquals(null, job);
    assertEquals(job.hashCode(), job1.hashCode());
    assertEquals(job1.hashCode(), job.hashCode());
    assertEquals(job2.hashCode(), job.hashCode());
    assertEquals(job2.hashCode(), job.hashCode());
    assertNotEquals(job.hashCode(), job3.hashCode());
  }
}
