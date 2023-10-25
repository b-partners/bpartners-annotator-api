CREATE TABLE IF NOT EXISTS "dummy_table"(
    id varchar constraint dummy_pk primary key
);

INSERT INTO "dummy_table" VALUES ('dummy_table_id') ON conflict ON CONSTRAINT dummy_pk do nothing;