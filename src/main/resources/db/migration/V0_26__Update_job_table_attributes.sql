-- Step 1: Identify and handle existing duplicates
WITH duplicates AS (SELECT id,
                           name,
                           ROW_NUMBER() OVER (PARTITION BY name ORDER BY id) AS row_num
                    FROM job)
UPDATE job j
SET name = j.name || '_' || row_num
FROM duplicates
WHERE j.id = duplicates.id
  AND row_num > 1;

-- Step 2: Add unique constraint on the "name" column
ALTER TABLE job
    ADD CONSTRAINT unique_name_constraint UNIQUE (name);