ALTER TABLE IF EXISTS job
    RENAME COLUMN "bucket_path" to "bucket_name";
ALTER TABLE IF EXISTS job
    ADD COLUMN "folder_path" varchar;
ALTER TABLE IF EXISTS job
    ADD COLUMN "owner_email" varchar;