alter table task drop column "size_in_kb";
alter table task drop column "height";
alter table task drop column "width";

alter table job add column images_height integer;
alter table job add column images_width integer;

update job set images_height = 1024, images_width = 1024 where images_height is null or images_height is null;