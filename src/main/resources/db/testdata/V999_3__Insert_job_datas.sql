INSERT INTO public.job(id, bucket_name, team_id, status, folder_path, owner_email, "name", type)
VALUES ('job_1_id', 'bucket_1_name', 'team_1_id', 'STARTED', 'images/', 'admin@email.com', 'job_1', 'LABELLING'),
       ('job_2_id', 'bucket_2_name', 'team_1_id', 'READY', 'images/2/', 'admin@email.com', 'job_2', 'REVIEWING'),
       ('job_3_id', 'bucket_3_name', 'team_1_id', 'COMPLETED', 'images/3/', 'admin@email.com', 'job_3', 'LABELLING'),
       ('job_4_id', 'bucket_4_name', 'team_1_id', 'PENDING', 'images/4/', 'admin@email.com', 'job_4', 'LABELLING'),
       ('job_5_id', 'bucket_5_name', 'team_1_id', 'STARTED', 'images/5/', 'admin@email.com', 'job_5', 'LABELLING'),
       ('job_6_id', 'bucket_5_name', 'team_1_id', 'FAILED', 'images/5/', 'admin@email.com', 'job_6', 'LABELLING'),
       ('job_7_id', 'bucket_5_name', 'team_1_id', 'TO_REVIEW', 'images/5/', 'admin@email.com', 'job_7', 'LABELLING'),
       ('job_8_id', 'bucket_5_name', 'team_1_id', 'TO_CORRECT', 'images/5/', 'admin@email.com', 'job_8', 'LABELLING'),
       ('job_9_id', 'bucket_5_name', 'team_1_id', 'TO_CORRECT', 'images/5/', 'admin@email.com', 'job_9', 'REVIEWING');