DO
$$
    BEGIN
        IF NOT EXISTS(SELECT FROM pg_type WHERE typname = 'task_status') THEN
            CREATE TYPE task_status AS ENUM ('PENDING', 'UNDER_COMPLETION', 'COMPLETED');
        end if;
    END
$$;
CREATE TABLE IF NOT EXISTS "task"(
    id varchar primary key default uuid_generate_v4(),
    job_id varchar not null references job("id"),
    image_uri varchar not null,
    status task_status not null,
    s3_image_key varchar not null,
    user_id varchar
);