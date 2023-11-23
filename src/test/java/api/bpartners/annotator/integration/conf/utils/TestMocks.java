package api.bpartners.annotator.integration.conf.utils;

import static api.bpartners.annotator.endpoint.rest.model.JobStatus.READY;

import api.bpartners.annotator.endpoint.rest.model.Job;
import api.bpartners.annotator.endpoint.rest.model.TaskStatistics;
import api.bpartners.annotator.endpoint.rest.model.Team;

public class TestMocks {
  public static final String ADMIN_API_KEY = "dummy";
  public static final String JOE_DOE_TOKEN = "joe_doe_token";
  public static final String JOE_DOE_EMAIL = "joe@email.com";

  public static final String TEAM_1_ID = "team_1_id";

  public static final String JOB_1_ID = "job_1_id";

  public static Team team1() {
    return new Team().id(TEAM_1_ID).name("joe_team");
  }

  public static Job job1() {
    return new Job()
        .id(JOB_1_ID)
        .bucketName("bucket_1_name")
        .teamId(team1().getId())
        .status(READY)
        .folderPath("images/")
        .ownerEmail("admin@email.com")
        .name("job_1")
        .taskStatistics(
            new TaskStatistics().completedTasksByUserId(0L).totalTasks(2L).remainingTasks(2L));
  }
}
