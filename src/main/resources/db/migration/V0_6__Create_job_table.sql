DO
$$
    BEGIN
        IF NOT EXISTS(SELECT FROM pg_type WHERE typname = 'job_status') THEN
            CREATE TYPE job_status AS ENUM ('PENDING', 'READY', 'STARTED', 'FAILED');
        end if;
    END
$$;
CREATE TABLE IF NOT EXISTS "job"
(
    id          varchar primary key default uuid_generate_v4(),
    bucket_path varchar not null,
    team_id      varchar not null references "team" ("id"),
    status job_status not null
);