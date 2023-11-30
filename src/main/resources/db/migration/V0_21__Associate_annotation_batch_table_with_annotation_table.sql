ALTER TABLE IF EXISTS "annotation"
    ADD COLUMN batch_id varchar references annotation_batch (id);
-- insert a batch for all unique couples of task_id and annotator_id because it means this task was annotated by the same user during a period where a task could only be annotated once
INSERT INTO "annotation_batch"(task_id, annotator_id)
SELECT DISTINCT task_id, user_id
from annotation;

--update existing annotations in order to group them by batch
UPDATE annotation a SET batch_id = batch.id
FROM annotation_batch batch
WHERE a.task_id = batch.task_id and a.user_id = batch.annotator_id;

-- delete all annotations without points because now empty annotations refer to empty batch
DELETE FROM annotation a WHERE a.polygon = '{"points": []}'::jsonb;