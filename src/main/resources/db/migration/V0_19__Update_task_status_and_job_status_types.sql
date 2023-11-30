DO
$$
    BEGIN
        IF EXISTS(SELECT FROM pg_type WHERE typname = 'task_status') THEN
            ALTER TYPE task_status
            ADD VALUE 'TO_CORRECT' BEFORE 'COMPLETED';
        END IF;
        IF EXISTS(SELECT FROM pg_type WHERE typname = 'job_status') THEN
            ALTER TYPE job_status
                ADD VALUE 'TO_REVIEW' AFTER 'FAILED';
            ALTER TYPE job_status
                ADD VALUE 'COMPLETED' AFTER 'TO_REVIEW';
        END IF;
    END
$$;