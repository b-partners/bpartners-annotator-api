INSERT INTO public.job(id, bucket_name, team_id, status, folder_path, owner_email, "name", type, images_height, images_width)
VALUES ('job_1_id', 'bucket_1_name', 'team_1_id', 'STARTED', 'images/', 'admin@email.com', 'job_1', 'LABELLING', 1024, 1024),
       ('job_2_id', 'bucket_2_name', 'team_1_id', 'READY', 'images/2/', 'admin@email.com', 'job_2', 'REVIEWING', 1024, 1024),
       ('job_3_id', 'bucket_3_name', 'team_1_id', 'COMPLETED', 'images/3/', 'admin@email.com', 'job_3', 'LABELLING', 1024, 1024),
       ('job_4_id', 'bucket_4_name', 'team_1_id', 'PENDING', 'images/4/', 'admin@email.com', 'job_4', 'LABELLING', 1024, 1024),
       ('job_5_id', 'bucket_5_name', 'team_1_id', 'STARTED', 'images/5/', 'admin@email.com', 'job_5', 'LABELLING', 1024, 1024),
       ('job_6_id', 'bucket_5_name', 'team_1_id', 'FAILED', 'images/5/', 'admin@email.com', 'job_6', 'LABELLING', 1024, 1024),
       ('job_7_id', 'bucket_5_name', 'team_1_id', 'TO_REVIEW', 'images/5/', 'admin@email.com', 'job_7', 'LABELLING', 1024, 1024),
       ('job_8_id', 'bucket_5_name', 'team_1_id', 'TO_CORRECT', 'images/5/', 'admin@email.com', 'job_8', 'LABELLING', 1024, 1024),
       ('job_9_id', 'bucket_5_name', 'team_1_id', 'TO_CORRECT', 'images/5/', 'admin@email.com', 'job_9', 'REVIEWING', 1024, 1024);