DO
$$
    BEGIN
        IF EXISTS(SELECT FROM pg_type WHERE typname = 'task_status') THEN
            ALTER TYPE task_status
            ADD VALUE IF NOT EXISTS 'TO_REVIEW' BEFORE 'COMPLETED';
        END IF;
    END
$$;