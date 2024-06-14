create table annotation_batch_export
(
    id       varchar primary key default uuid_generate_v4(),
    job_id   varchar references job (id)              not null,
    batch_id varchar references annotation_batch (id) not null,
    task_id  varchar references task (id)             not null
);
create table annotation_batch_export_status
(
    id                varchar primary key                  default uuid_generate_v4(),
    progression       progression_status          not null,
    health            health_status               not null,
    creation_datetime timestamp without time zone not null default now()::timestamp without time zone,
    job_id            varchar references annotation_batch_export ("id"),
    message           varchar
);
