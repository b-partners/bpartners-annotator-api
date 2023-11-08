CREATE TABLE IF NOT EXISTS "annotation"(
    id varchar primary key default uuid_generate_v4(),
    task_id varchar not null references task("id"),
    user_id varchar not null references "user"("id"),
    label_id varchar not null references label("id"),
    polygon jsonb
);