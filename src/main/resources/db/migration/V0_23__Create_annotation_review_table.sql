CREATE TABLE IF NOT EXISTS "annotation_review"
(
    id                varchar primary key                         default uuid_generate_v4(),
    annotation_batch_review_id     varchar references annotation_batch_review (id) not null,
    annotation_id varchar references annotation(id),
    comment           text,
    creation_datetime timestamp without time zone        not null default current_timestamp(0)::TIMESTAMP WITHOUT TIME ZONE
);

INSERT INTO annotation_review(annotation_batch_review_id, annotation_id, comment)
SELECT id, annotation_id, comment from annotation_batch_review;

ALTER TABLE annotation_batch_review DROP COLUMN comment;
ALTER TABLE annotation_batch_review DROP COLUMN annotation_id;