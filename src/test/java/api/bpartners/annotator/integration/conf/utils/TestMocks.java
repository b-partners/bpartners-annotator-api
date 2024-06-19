package api.bpartners.annotator.integration.conf.utils;

import static api.bpartners.annotator.endpoint.rest.model.JobStatus.STARTED;
import static api.bpartners.annotator.endpoint.rest.model.JobStatus.TO_CORRECT;
import static api.bpartners.annotator.endpoint.rest.model.JobType.LABELLING;
import static api.bpartners.annotator.endpoint.rest.model.JobType.REVIEWING;
import static api.bpartners.annotator.endpoint.rest.model.ReviewStatus.ACCEPTED;
import static api.bpartners.annotator.endpoint.rest.model.ReviewStatus.REJECTED;
import static api.bpartners.annotator.endpoint.rest.model.TaskStatus.PENDING;
import static api.bpartners.annotator.repository.model.enums.JobStatus.COMPLETED;

import api.bpartners.annotator.endpoint.rest.model.Annotation;
import api.bpartners.annotator.endpoint.rest.model.AnnotationBatch;
import api.bpartners.annotator.endpoint.rest.model.AnnotationBatchReview;
import api.bpartners.annotator.endpoint.rest.model.AnnotationReview;
import api.bpartners.annotator.endpoint.rest.model.Job;
import api.bpartners.annotator.endpoint.rest.model.Label;
import api.bpartners.annotator.endpoint.rest.model.Point;
import api.bpartners.annotator.endpoint.rest.model.Polygon;
import api.bpartners.annotator.endpoint.rest.model.Task;
import api.bpartners.annotator.endpoint.rest.model.TaskStatistics;
import api.bpartners.annotator.endpoint.rest.model.Team;
import api.bpartners.annotator.repository.model.AnnotationBatchSubset;
import api.bpartners.annotator.repository.model.enums.TaskStatus;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class TestMocks {
  public static final String ADMIN_API_KEY = "dummy";
  public static final String JOE_DOE_TOKEN = "joe_doe_token";
  public static final String JANE_DOE_TOKEN = "jane_doe_token";
  public static final String JOE_DOE_EMAIL = "joe@email.com";
  public static final String JANE_DOE_EMAIL = "jane@email.com";
  public static final String MOCK_SUBSET_ID = "mock_subset_id";

  public static final String TEAM_1_ID = "team_1_id";

  public static final String JOB_1_ID = "job_1_id";
  public static final String JOB_3_ID = "job_3_id";

  public static final String TASK_1_ID = "task_1_id";
  public static final String MOCK_PRESIGNED_URL = "https://wwww.example.com";
  public static final String TEAM_2_ID = "team_2_id";
  public static final String ANNOTATION_1_ID = "annotation_1_id";
  public static final String ANNOTATION_2_ID = "annotation_2_id";
  public static final String JOE_DOE_ID = "joe_doe_id";
  public static final String JANE_DOE_ID = "jane_doe_id";
  public static final String BATCH_1_ID = "batch_1_id";
  public static final String BATCH_REVIEW_2_ID = "batch_review_2_id";
  public static final String BATCH_REVIEW_1_ID = "batch_review_1_id";
  public static final String BATCH_2_ID = "batch_2_id";
  public static final String GEOJOBS_USER_ID = "geo-jobs_user_id";
  public static final String GEOJOBS_TEAM_ID = "geo_jobs_team_id";
  public static final String TASK_11_ID = "task_11_id";
  public static final String TASK_21_ID = "task_21_id";
  public static final String TASK_22_ID = "task_22_id";
  public static final String TASK_23_ID = "task_23_id";
  public static final String BATCH_4_ID = "batch_4_id";
  private static final String USER_ID = "userId";
  private static final String TASK_ID = "task_id";
  public static final String NOT_EXISTING_USER_ID = "NOT_EXISTING_USER_ID";
  public static final String NOT_EXISTING_JOB_ID = "NOT_EXISTING_JOB_ID";
  public static final String TEST_MAIL = "mail@test.com";

  public static Team team1() {
    return new Team().id(TEAM_1_ID).name("joe_team");
  }

  public static Job job1() {
    return new Job()
        .id(JOB_1_ID)
        .bucketName("bucket_1_name")
        .teamId(team1().getId())
        .status(STARTED)
        .folderPath("images/")
        .ownerEmail("admin@email.com")
        .name("job_1")
        .imagesHeight(1024)
        .imagesWidth(1024)
        .taskStatistics(
            new TaskStatistics()
                .remainingTasks(13L)
                .totalTasks(14L)
                .completedTasksByUserId(0L)
                .remainingTasksForUserId(11L))
        .type(LABELLING)
        .labels(List.of(label1(), label2()));
  }

  public static Job job9() {
    return new Job()
        .id("job_9_id")
        .bucketName("bucket_5_name")
        .teamId(team1().getId())
        .status(TO_CORRECT)
        .folderPath("images/5/")
        .ownerEmail("admin@email.com")
        .name("job_9")
        .imagesHeight(1024)
        .imagesWidth(1024)
        .taskStatistics(
            new TaskStatistics()
                .remainingTasks(0L)
                .totalTasks(0L)
                .completedTasksByUserId(0L)
                .remainingTasksForUserId(0L))
        .type(REVIEWING)
        .labels(List.of(label1(), label2()));
  }

  public static Label label1() {
    return new Label().id("label_1_id").name("POOL").color("#00ff00");
  }

  public static Label label2() {
    return new Label().id("label_2_id").name("VELUX").color("#00ff00");
  }

  public static Task task1() {
    return new Task()
        .id(TASK_1_ID)
        .status(PENDING)
        .userId(null)
        .imageUri(MOCK_PRESIGNED_URL)
        .filename("image_1");
  }

  public static Team team2() {
    return new Team().id(TEAM_2_ID).name("jane_team");
  }

  public static Annotation annotation1() {
    return new Annotation()
        .id(ANNOTATION_1_ID)
        .taskId(TASK_1_ID)
        .userId(JOE_DOE_ID)
        .label(label1())
        .polygon(polygon());
  }

  public static Polygon polygon() {
    return new Polygon().points(List.of(new Point().x(1.0).y(1.0)));
  }

  public static Annotation annotation2() {
    return new Annotation()
        .id(ANNOTATION_2_ID)
        .taskId(TASK_1_ID)
        .userId(JOE_DOE_ID)
        .label(label2())
        .polygon(
            new Polygon().points(List.of(new Point().x(1.0).y(1.0), new Point().x(1.0).y(2.0))));
  }

  public static AnnotationBatch annotationBatch1() {
    return new AnnotationBatch()
        .id(BATCH_1_ID)
        .annotations(List.of(annotation1(), annotation2()))
        .creationDatetime(
            Instant.parse("2023-12-30T21:01:55.907261Z").truncatedTo(ChronoUnit.MILLIS));
  }

  public static AnnotationBatch annotationBatch2() {
    return new AnnotationBatch()
        .id(BATCH_2_ID)
        .annotations(List.of())
        .creationDatetime(
            Instant.parse("2023-11-30T20:01:55.907261Z").truncatedTo(ChronoUnit.MILLIS));
  }

  public static AnnotationReview annotationReview1() {
    return new AnnotationReview()
        .id("review_1_id")
        .annotationId(ANNOTATION_1_ID)
        .comment("remove points");
  }

  public static AnnotationBatchReview batchReview1() {
    return new AnnotationBatchReview()
        .id(BATCH_REVIEW_1_ID)
        .annotationBatchId(BATCH_1_ID)
        .status(REJECTED)
        .reviews(List.of(annotationReview1()))
        .creationDatetime(Instant.parse("2023-11-30T06:37:51.384Z"));
  }

  public static AnnotationBatchReview batchReview2() {
    return new AnnotationBatchReview()
        .id(BATCH_REVIEW_2_ID)
        .annotationBatchId(BATCH_2_ID)
        .status(ACCEPTED)
        .reviews(List.of());
  }

  public static api.bpartners.annotator.repository.model.Job aTestJob(String id) {
    return api.bpartners.annotator.repository.model.Job.builder()
        .id(id)
        .bucketName("test_bucket")
        .folderPath("test_folder_path/")
        .name("test_name")
        .ownerEmail("test@test.com")
        .status(COMPLETED)
        .teamId("test_team_id")
        .type(LABELLING)
        .tasks(List.of(aTestTask()))
        .imagesHeight(256)
        .imagesWidth(256)
        .labels(List.of(aTestLabel()))
        .build();
  }

  public static api.bpartners.annotator.repository.model.Task aTestTask() {
    return api.bpartners.annotator.repository.model.Task.builder()
        .id(TASK_ID)
        .filename("filename.jpg")
        .job(null)
        .status(TaskStatus.COMPLETED)
        .userId(USER_ID)
        .build();
  }

  public static api.bpartners.annotator.repository.model.Label aTestLabel() {
    return api.bpartners.annotator.repository.model.Label.builder()
        .id("label_id")
        .name("label_name")
        .color("#123123")
        .build();
  }

  public static api.bpartners.annotator.repository.model.AnnotationBatch aTestAnnotationBatch() {
    String batchId = "batch_id";
    return api.bpartners.annotator.repository.model.AnnotationBatch.builder()
        .id(batchId)
        .annotations(List.of(aTestAnnotation(batchId)))
        .annotatorId(USER_ID)
        .creationTimestamp(Instant.MIN)
        .task(aTestTask())
        .build();
  }

  public static AnnotationBatchSubset mockSubset() {
    return new AnnotationBatchSubset(MOCK_SUBSET_ID, JOB_1_ID, List.of(aTestAnnotationBatch()));
  }

  public static api.bpartners.annotator.repository.model.Annotation aTestAnnotation(
      String batchId) {
    return api.bpartners.annotator.repository.model.Annotation.builder()
        .id("annotation_id")
        .label(aTestLabel())
        .taskId(TASK_ID)
        .userId(USER_ID)
        .batchId(batchId)
        .polygon(
            api.bpartners.annotator.repository.model.Annotation.Polygon.builder()
                .points(
                    List.of(
                        new api.bpartners.annotator.repository.model.Annotation.Point(1.0, 1.0)))
                .build())
        .build();
  }
}
