CREATE TABLE IF NOT EXISTS "has_label"
(
    id       varchar primary key default uuid_generate_v4(),
    job_id   varchar references job ("id")   not null,
    label_id varchar references label ("id") not null,
    constraint unique_job_label unique (job_id, label_id)
);