ALTER TABLE "user"
    ADD COLUMN "email" varchar;
DO
$$
    BEGIN
        IF NOT EXISTS(SELECT FROM pg_type WHERE typname = 'user_role') THEN
            CREATE TYPE user_role
            AS ENUM ('ADMIN', 'ANNOTATOR');
        END IF;
    END
$$;
ALTER TABLE "user"
    ADD COLUMN IF NOT EXISTS roles user_role[] DEFAULT '{}';
ALTER TABLE "user"
    RENAME COLUMN "teamid" to "team_id";