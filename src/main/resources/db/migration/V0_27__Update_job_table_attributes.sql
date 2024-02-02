DO
$$
    BEGIN
        IF NOT EXISTS(SELECT FROM pg_type WHERE typname = 'job_type') THEN
            CREATE TYPE job_type AS ENUM('LABELLING', 'REVIEWING');
        END IF;
    END
$$;

alter table if exists "job"
add column type job_type not null default 'LABELLING';