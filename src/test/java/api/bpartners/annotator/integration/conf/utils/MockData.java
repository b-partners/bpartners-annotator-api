package api.bpartners.annotator.integration.conf.utils;

import api.bpartners.annotator.endpoint.rest.model.Job;
import api.bpartners.annotator.endpoint.rest.model.Label;
import java.util.List;

import static api.bpartners.annotator.endpoint.rest.model.JobStatus.PENDING;

public class MockData {
  public static final String JOB_1_ID = "job_1_id";
  public static final String TEAM_1_ID = "team_1_id";
  public static final String LABEL_1_ID = "label_1_id";
  public static final String LABEL_2_ID = "label_2_id";

  public static Job job1() {
    return new Job()
        .id(JOB_1_ID)
        .bucketName("mock")
        .labels(List.of(label1(), label2()))
        .remainingTasks(1)
        .status(PENDING)
        .teamId(TEAM_1_ID)
        .folderPath("mock")
        .ownerEmail("mock@hotmail.com");
  }

  public static Label label1() {
    return new Label()
        .id(LABEL_1_ID)
        .name("POOL");
  }

  static Label label2() {
    return new Label()
        .id(LABEL_2_ID)
        .name("VELUX");
  }
}