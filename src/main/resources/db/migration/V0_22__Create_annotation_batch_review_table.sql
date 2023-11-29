DO
$$
    BEGIN
        IF NOT EXISTS(SELECT FROM pg_type WHERE typname = 'review_status') THEN
            CREATE TYPE review_status AS ENUM ('ACCEPTED','REJECTED');
        end if;
    END
$$;
CREATE TABLE IF NOT EXISTS "annotation_batch_review"
(
    id                varchar primary key                         default uuid_generate_v4(),
    annotation_batch_id     varchar references annotation_batch (id) not null,
    annotation_id varchar references annotation(id),
    status            review_status                      not null default 'REJECTED',
    comment           text,
    creation_datetime timestamp without time zone        not null default current_timestamp(0)::TIMESTAMP WITHOUT TIME ZONE
);