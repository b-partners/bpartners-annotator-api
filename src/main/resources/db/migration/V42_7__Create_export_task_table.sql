CREATE TABLE if NOT EXISTS "export_task" (
    id varchar primary key,
    job_id varchar references "job"(id),
    submission_instant timestamp with time zone
);