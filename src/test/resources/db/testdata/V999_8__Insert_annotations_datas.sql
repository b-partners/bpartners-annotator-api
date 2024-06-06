INSERT INTO public.annotation (id, task_id, user_id, label_id, batch_id, polygon)
VALUES ('annotation_1_id', 'task_1_id', 'joe_doe_id', 'label_1_id', 'batch_1_id', '{
  "points": [
    {
      "x": 1.0,
      "y": 1.0
    }
  ]
}'),
       ('annotation_2_id', 'task_1_id', 'joe_doe_id', 'label_2_id', 'batch_1_id', '{
         "points": [
           {
             "x": 1.0,
             "y": 1.0
           },
           {
             "x": 1.0,
             "y": 2.0
           }
         ]
       }'),
       ('annotation_3_id', 'task_15_id', 'joe_doe_id', 'label_1_id', 'batch_3_id', '{
         "points": [
           {
             "x": 1.0,
             "y": 1.0
           }
         ]
       }'),
       ('annotation_4_id', 'task_11_id', 'joe_doe_id', 'label_1_id', 'batch_4_id', '{
         "points": [
           {
             "x": 1.0,
             "y": 1.0
           }
         ]
       }');