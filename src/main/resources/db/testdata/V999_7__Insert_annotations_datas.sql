INSERT INTO public.annotation (id, task_id, user_id, label_id, polygon)
VALUES ('annotation_1_id', 'task_1_id', 'joe_doe_id', 'label_1_id', '{
  "points": [
    {
      "x": 1.0,
      "y": 1.0
    }
  ]
}'),
       ('annotation_2_id', 'task_1_id', 'joe_doe_id', 'label_2_id', '{
         "points": []
       }');