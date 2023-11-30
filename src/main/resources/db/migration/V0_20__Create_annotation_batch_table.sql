CREATE TABLE IF NOT EXISTS "annotation_batch"(
    id varchar primary key default uuid_generate_v4(),
    task_id varchar references task(id) not null,
    annotator_id varchar references "user"(id),
    creation_timestamp timestamp without time zone not null default now()::timestamp without time zone
);
CREATE INDEX IF NOT EXISTS batch_task_idx ON annotation_batch(task_id);
