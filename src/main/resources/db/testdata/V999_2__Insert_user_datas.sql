INSERT INTO public."user"(id, team_id, email, roles)
VALUES ('joe_doe_id', 'team_1_id', 'joe@email.com', '{ANNOTATOR}'),
       ('jane_doe_id', 'team_2_id', 'jane@email.com', '{ANNOTATOR}'),
       ('geo-jobs_user_id', 'geo_jobs_team_id', 'geojobs@email.com', '{ANNOTATOR}');

--ADMIN
INSERT INTO public."user"(id, team_id, email, roles)
VALUES ('dummy_admin_id', 'team_admin_id', 'dummy@email.com', '{ADMIN}');