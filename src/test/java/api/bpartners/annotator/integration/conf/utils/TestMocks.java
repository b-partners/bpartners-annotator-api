package api.bpartners.annotator.integration.conf.utils;

import static api.bpartners.annotator.endpoint.rest.model.JobStatus.STARTED;
import static api.bpartners.annotator.endpoint.rest.model.ReviewStatus.ACCEPTED;
import static api.bpartners.annotator.endpoint.rest.model.ReviewStatus.REJECTED;
import static api.bpartners.annotator.endpoint.rest.model.TaskStatus.PENDING;

import api.bpartners.annotator.endpoint.rest.model.Annotation;
import api.bpartners.annotator.endpoint.rest.model.AnnotationBatch;
import api.bpartners.annotator.endpoint.rest.model.AnnotationBatchReview;
import api.bpartners.annotator.endpoint.rest.model.Job;
import api.bpartners.annotator.endpoint.rest.model.Label;
import api.bpartners.annotator.endpoint.rest.model.Point;
import api.bpartners.annotator.endpoint.rest.model.Polygon;
import api.bpartners.annotator.endpoint.rest.model.Task;
import api.bpartners.annotator.endpoint.rest.model.TaskStatistics;
import api.bpartners.annotator.endpoint.rest.model.Team;
import java.util.List;

public class TestMocks {
  public static final String ADMIN_API_KEY = "dummy";
  public static final String JOE_DOE_TOKEN = "joe_doe_token";
  public static final String JOE_DOE_EMAIL = "joe@email.com";

  public static final String TEAM_1_ID = "team_1_id";

  public static final String JOB_1_ID = "job_1_id";

  public static final String TASK_1_ID = "task_1_id";
  public static final String MOCK_PRESIGNED_URL = "https://wwww.example.com";
  public static final String TEAM_2_ID = "team_2_id";
  public static final String ANNOTATION_1_ID = "annotation_1_id";
  public static final String ANNOTATION_2_ID = "annotation_2_id";
  public static final String JOE_DOE_ID = "joe_doe_id";
  public static final String BATCH_1_ID = "batch_1_id";
  public static final String REVIEW_2_ID = "review_2_id";
  public static final String REVIEW_1_ID = "review_1_id";

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
        .taskStatistics(
            new TaskStatistics().remainingTasks(2L).totalTasks(2L).completedTasksByUserId(0L))
        .labels(List.of(label1(), label2()));
  }

  public static Label label1() {
    return new Label().id("label_1_id").name("POOL").color("#00ff00");
  }

  public static Label label2() {
    return new Label().id("label_2_id").name("VELUX").color("#00ff00");
  }

  public static Task task1() {
    return new Task().id(TASK_1_ID).status(PENDING).userId(null).imageUri(MOCK_PRESIGNED_URL);
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
        .polygon(new Polygon().points(List.of(new Point().x(1.0).y(1.0))));
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
    return new AnnotationBatch().id(BATCH_1_ID).annotations(List.of(annotation1(), annotation2()));
  }

  public static AnnotationBatch annotationBatch2() {
    return new AnnotationBatch().id("batch_2_id").annotations(List.of());
  }

  public static AnnotationBatchReview annotationReview1() {
    return new AnnotationBatchReview()
        .id(REVIEW_1_ID)
        .annotationBatchId(BATCH_1_ID)
        .annotationId(ANNOTATION_1_ID)
        .status(REJECTED)
        .comment("remove points");
  }

  public static AnnotationBatchReview annotationReview2() {
    return new AnnotationBatchReview()
        .id(REVIEW_2_ID)
        .annotationBatchId(BATCH_1_ID)
        .annotationId(ANNOTATION_1_ID)
        .status(ACCEPTED)
        .comment(null);
  }
}
