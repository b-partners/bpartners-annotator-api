create table annotation_batch_page_export
(
    id            varchar primary key default uuid_generate_v4(),
    job_id        varchar references job (id)              not null,
    batch_id      varchar references annotation_batch (id) not null,
    export_format export_format                            not null,
    job_export_id varchar references job_export (id)       not null,
    begin_page    int                                      not null check ( begin_page >= 0 ),
    page_size     int                                      not null check ( page_size >= 0 )
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
