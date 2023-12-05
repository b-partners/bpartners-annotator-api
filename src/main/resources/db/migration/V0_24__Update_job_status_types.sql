DO
$$
    BEGIN
        IF EXISTS(SELECT FROM pg_type WHERE typname = 'job_status') THEN
            ALTER TYPE job_status
            ADD VALUE IF NOT EXISTS 'TO_CORRECT' BEFORE 'TO_REVIEW';
        END IF;
    END
$$;