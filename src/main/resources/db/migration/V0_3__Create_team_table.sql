create extension if not exists "uuid-ossp";
CREATE TABLE IF NOT EXISTS "team"
(
    id varchar primary key default uuid_generate_v4()
);