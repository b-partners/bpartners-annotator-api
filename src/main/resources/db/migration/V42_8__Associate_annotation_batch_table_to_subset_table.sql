ALTER TABLE IF EXISTS "annotation_batch"
    ADD COLUMN subset_id varchar references annotation_batch_subset(id);