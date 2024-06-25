DO
$$
    BEGIN
        IF NOT EXISTS(SELECT FROM pg_type WHERE typname = 'progression_status') THEN
            CREATE TYPE progression_status AS ENUM ('PENDING', 'PROCESSING', 'FINISHED');
        END IF;
    END
$$;

DO
$$
    BEGIN
        IF NOT EXISTS(SELECT FROM pg_type WHERE typname = 'health_status') THEN
            CREATE TYPE health_status AS ENUM ('UNKNOWN', 'RETRYING', 'SUCCEEDED', 'FAILED');
        END IF;
    END
$$;

create table if not exists "export_task_status" (
    id varchar primary key default uuid_generate_v4(),
    task_id varchar references "export_task"(id),
    message varchar,
    creation_datetime timestamp with time zone,
    progression progression_status,
    health health_status
);