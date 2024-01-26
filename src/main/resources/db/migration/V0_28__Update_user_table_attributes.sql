-- Step 1: Identify and handle existing duplicates
WITH duplicates AS (SELECT id,
                           email,
                           ROW_NUMBER() OVER (PARTITION BY email ORDER BY id) AS row_num
                    FROM "user")
UPDATE "user" u
SET email = row_num || '_' || u.email
FROM duplicates
WHERE u.id = duplicates.id
AND row_num > 1;

-- Step 2: Add unique constraint on the "email" column
ALTER TABLE "user"
    ADD CONSTRAINT unique_email_constraint UNIQUE (email);