DO
$$
    begin
        if not exists (select from pg_type where typname = 'export_format') then
            create type export_format as ENUM ('VGG', 'COCO');
        end if;
        if not exists (select from pg_type where typname = 'progression_status') then
            create type progression_status as ENUM ('PENDING', 'PROCESSING', 'FINISHED');
        end if;
        if not exists (select from pg_type where typname = 'health_status') then
            create type health_status as ENUM ('UNKNOWN', 'SUCCEEDED', 'FAILED');
        end if;
    end
$$;
create table job_export
(
    id          varchar primary key default uuid_generate_v4(),
    job_id      varchar references job (id) not null,
    email_owner varchar                     not null,
    email_cc    varchar
);
create table job_export_status
(
    id                varchar primary key                  default uuid_generate_v4(),
    progression       progression_status          not null,
    health            health_status               not null,
    creation_datetime timestamp without time zone not null default now()::timestamp without time zone,
    job_id            varchar references job_export ("id"),
    message           varchar
);
