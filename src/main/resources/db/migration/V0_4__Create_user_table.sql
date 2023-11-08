CREATE TABLE IF NOT EXISTS "user"
(
    id     varchar primary key default uuid_generate_v4(),
    teamId varchar not null
);