CREATE TABLE IF NOT EXISTS "label"
(
    id varchar primary key default uuid_generate_v4(),
    name varchar not null unique
);