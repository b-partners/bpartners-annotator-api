package api.bpartners.annotator.unit;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.util.UUID.randomUUID;

import api.bpartners.annotator.model.VGG;
import api.bpartners.annotator.repository.model.Annotation;
import api.bpartners.annotator.repository.model.Job;
import api.bpartners.annotator.repository.model.Label;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

@Disabled
class VGGtoSQL {
    private static final String BATCH_6_NANTES_VGG_FILENAME = "batch-6-Nantes-VGG.json";
    private static final String DIJON_VGG_VALIDATION_FILENAME = "Dijon-VGG-validation2.json";
    private static final String GLOBAL_ANNOTATION_SIX_REGION = "annotation-globale-six-region-train.json";
    private static final String OWNER_EMAIL = "hei.mahefa@gmail.com";
    private static final String ANNOTATOR_TEAM_ID = "25c2052d-705f-4ab4-8eb1-17fefe8c182b";
    private static final Map<String, String> DIJON_METADATA = Map.of(
            "bucketName", "annotations-images-6-regions",
            "teamId", ANNOTATOR_TEAM_ID,
            "folderPath", "val_dijon/all_images/",
            "jobName", "val_dijon",
            "vgg_filename", DIJON_VGG_VALIDATION_FILENAME
    );
    //IKRAM AND SOFIANE
    private static final String[] ANNOTATOR_IDS = {"29d4c060-77e6-4e19-b2cb-86a052047ed8", "76e23de5-a8dc-402d-a137-230309745227"};
    private static final ObjectMapper OM = new ObjectMapper().findAndRegisterModules();
    private static final Map<String, String> NANTES_METADATA = Map.of(
            "bucketName", "annotations-images-6-regions",
            "teamId", ANNOTATOR_TEAM_ID,
            "folderPath", "val_nantes/images_val/",
            "jobName", "val_nantes",
            "vgg_filename", BATCH_6_NANTES_VGG_FILENAME
    );
    private static final Map<String, String> ALL_REGION_METADATA = Map.of(
            "bucketName", "annotations-images-6-regions",
            "teamId", ANNOTATOR_TEAM_ID,
            "folderPath", "all-images-train/images/",
            "jobName", "all_images_train",
            "vgg_filename", GLOBAL_ANNOTATION_SIX_REGION
    );

    private static Path writeToFile(String resultFileName, String content) {
        try {
            return Files.writeString(new File(resultFileName).toPath(), content, new StandardOpenOption[]{CREATE, TRUNCATE_EXISTING});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void generate_sql() {
        var metadata = ALL_REGION_METADATA;
        VGG vggAnnotation = getVGGAnnotation(metadata.get("vgg_filename"));

        Job job = jobFromMetadata(metadata);
        List<Label> labels = extractLabels(vggAnnotation);

        System.out.println(writeToFile(job.getName() + "/V1_job.sql", sqlFromJob(job)));
        System.out.println(Arrays.toString(generate_labels_and_has_labels_sql(labels, metadata, job)));
        System.out.println(Arrays.toString(generate_tasks_and_annotation_batch_sql(vggAnnotation, metadata, job, labels)));
    }

    private VGG getVGGAnnotation(String vggFilename) {
        ClassPathResource vggAnnotationResource = new ClassPathResource("mock/annotations/" + vggFilename);
        try {
            return OM.readValue(vggAnnotationResource.getInputStream(), VGG.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    String sqlFromJob(Job job) {
        String jobInsertSqlTemplate = """
                INSERT INTO job(id, bucket_name, team_id, status, folder_path, owner_email, "name") VALUES %s
                """;
        String jobInsertValuesTemplate = "('{ID}', '{BUCKET_NAME}', '{TEAM_ID}', 'TO_REVIEW', '{FOLDER_PATH}', '{OWNER_EMAIL}', '{NAME}')";

        String jobInsertValues = jobInsertValuesTemplate
                .replace("{ID}", job.getId())
                .replace("{BUCKET_NAME}", job.getBucketName())
                .replace("{TEAM_ID}", job.getTeamId())
                .replace("{FOLDER_PATH}", job.getFolderPath())
                .replace("{OWNER_EMAIL}", job.getOwnerEmail())
                .replace("{NAME}", job.getName());
        jobInsertValues += ";";

        return String.format(jobInsertSqlTemplate, jobInsertValues);
    }

    Job jobFromMetadata(Map<String, String> metadata) {
        return Job.builder()
                .id(randomUUID().toString())
                .bucketName(metadata.get("bucketName"))
                .teamId(metadata.get("teamId"))
                .folderPath(metadata.get("folderPath"))
                .ownerEmail(OWNER_EMAIL)
                .name(metadata.get("jobName"))
                .build();
    }

    List<Label> extractLabels(VGG vgg) {
        Set<VGG.Annotation.Region.RegionAttribute> sortedAndUniquedRegionAttributes = new TreeSet<>(Comparator.comparing(VGG.Annotation.Region.RegionAttribute::getLabel));
        vgg.forEach((filename, annotation) -> annotation.getRegions().forEach((regionId, region) -> sortedAndUniquedRegionAttributes.add(region.getRegionAttribute())));
        return sortedAndUniquedRegionAttributes.stream()
                .map(regionAttribute ->
                        Label.builder()
                                .id(randomUUID().toString())
                                .name(regionAttribute.getLabel())
                                .color(generateRandomHexHtmlColorCode())
                                .build()
                )
                .toList();
    }

    Path[] generate_labels_and_has_labels_sql(List<Label> labels, Map<String, String> regionSpecificMetadata, Job job) {
        String labelInsertSqlTemplate = "INSERT INTO label(id, name, color) VALUES %s";
        String labelValuesToInsertTemplate = "('{ID}', '{NAME}', '{COLOR}')";
        String hasLabelInsertSqlTemplate = "INSERT INTO has_label(id, job_id, label_id) VALUES %s";
        String hasLabelValuesToInsertTemplate = "('{ID}', '{JOB_ID}', '{LABEL_ID}')";

        int max = labels.size();
        for (Label label : labels) {
            String labelId = label.getId();
            String labelValuesToInsert = labelValuesToInsertTemplate
                    .replace("{ID}", labelId)
                    .replace("{NAME}", label.getName())
                    .replace("{COLOR}", label.getColor());
            labelInsertSqlTemplate = String.format(labelInsertSqlTemplate, labelValuesToInsert);

            String hasLabelValuesToInsert = hasLabelValuesToInsertTemplate
                    .replace("{ID}", randomUUID().toString())
                    .replace("{JOB_ID}", job.getId())
                    .replace("{LABEL_ID}", labelId);
            hasLabelInsertSqlTemplate = String.format(hasLabelInsertSqlTemplate, hasLabelValuesToInsert);

            if (--max != 0) {
                labelInsertSqlTemplate += ", %s";
                hasLabelInsertSqlTemplate += ", %s";
            } else {
                labelInsertSqlTemplate += ";";
                hasLabelInsertSqlTemplate += ";";
            }
        }

        String jobName = regionSpecificMetadata.get("jobName");
        return new Path[]{
                writeToFile(jobName + "/V2_labels.sql", labelInsertSqlTemplate),
                writeToFile(jobName + "/V3_has_labels.sql", hasLabelInsertSqlTemplate)
        };
    }


    private static String generateRandomHexHtmlColorCode() {
        Random obj = new Random();
        int rand_num = obj.nextInt(0xffffff + 1);
        return String.format("#%06x", rand_num);
    }

    Path[] generate_tasks_and_annotation_batch_sql(
            VGG vgg,
            Map<String, String> metadata,
            Job job,
            List<Label> labels) {
        String taskInsertSqlTemplate = """
                INSERT INTO task (id, job_id, status, filename, user_id)
                VALUES %s
                """;
        String taskInsertValuesTemplate = "('{ID}', '{JOB_ID}', 'TO_REVIEW', '{FILENAME}', '{USER_ID}')";

        String annotationBatchSqlTemplate = """
                INSERT INTO annotation_batch (id, task_id, annotator_id)
                VALUES %s
                """;
        String annotationBatchInsertValuesTemplate = "('{ID}', '{TASK_ID}', '{ANNOTATOR_ID}')";

        String annotationSqlTemplate = """
                INSERT INTO annotation (id, task_id, user_id, label_id, batch_id, polygon)
                VALUES %s
                """;
        String annotationSqlInsertValuesTemplate = "('{ID}', '{TASK_ID}', '{ANNOTATOR_ID}', '{LABEL_ID}','{BATCH_ID}', '{POLYGON}')";
        Random random = new Random();

        Set<Map.Entry<String, VGG.Annotation>> entries = vgg.entrySet();
        int numberOfAnnotationEntriesLeft = entries.size();
        for (Map.Entry<String, VGG.Annotation> annotationEntry : entries) {
            String taskId = randomUUID().toString();
            String randomAnnotatorId = getRandomAnnotatorId(random);
            String taskInsertValues = taskInsertValuesTemplate
                    .replace("{ID}", taskId)
                    .replace("{JOB_ID}", job.getId())
                    .replace("{FILENAME}", annotationEntry.getKey())
                    .replace("{USER_ID}", randomAnnotatorId);
            taskInsertSqlTemplate = String.format(taskInsertSqlTemplate, taskInsertValues);

            String annotationBatchId = randomUUID().toString();
            String annotationBatchInsertValues = annotationBatchInsertValuesTemplate
                    .replace("{ID}", annotationBatchId)
                    .replace("{TASK_ID}", taskId)
                    .replace("{ANNOTATOR_ID}", randomAnnotatorId);
            annotationBatchSqlTemplate = String.format(annotationBatchSqlTemplate, annotationBatchInsertValues);

            annotationSqlTemplate = getAnnotationSqlString(labels, annotationEntry, annotationSqlInsertValuesTemplate, taskId, randomAnnotatorId, annotationBatchId, annotationSqlTemplate);

            if (--numberOfAnnotationEntriesLeft != 0) {
                taskInsertSqlTemplate += ", %s";
                annotationBatchSqlTemplate += ", %s";
                annotationSqlTemplate += ", %s";
            } else {
                taskInsertSqlTemplate += ";";
                annotationBatchSqlTemplate += ";";
                annotationSqlTemplate += ";";
            }
        }

        String jobName = metadata.get("jobName");
        return new Path[]{
                writeToFile(jobName + "/V4_tasks.sql", taskInsertSqlTemplate),
                writeToFile(jobName + "/V5_annotation_batch.sql", annotationBatchSqlTemplate),
                writeToFile(jobName + "/V6_annotation.sql", annotationSqlTemplate)
        };
    }

    private String getAnnotationSqlString(List<Label> labels,
                                          Map.Entry<String, VGG.Annotation> annotationEntry,
                                          String annotationSqlInsertValuesTemplate,
                                          String taskId,
                                          String randomAnnotatorId,
                                          String annotationBatchId,
                                          String annotationSqlTemplate) {
        Set<Map.Entry<String, VGG.Annotation.Region>> annotationRegionEntries = annotationEntry.getValue().getRegions().entrySet();
        int numberOfAnnotationRegionEntriesLeft = annotationRegionEntries.size();
        for (Map.Entry<String, VGG.Annotation.Region> regionEntry : annotationRegionEntries) {
            String annotationSqlInsertValues = annotationSqlInsertValuesTemplate
                    .replace("{ID}", randomUUID().toString())
                    .replace("{TASK_ID}", taskId)
                    .replace("{ANNOTATOR_ID}", randomAnnotatorId)
                    .replace("{LABEL_ID}", filterLabel(labels, regionEntry.getValue().getRegionAttribute()).getId())
                    .replace("{BATCH_ID}", annotationBatchId)
                    .replace("{POLYGON}", writeValueAsString(toDomainPolygon(regionEntry.getValue().getShapeAttribute())));
            annotationSqlTemplate = String.format(annotationSqlTemplate, annotationSqlInsertValues);
            if (--numberOfAnnotationRegionEntriesLeft != 0) {
                annotationSqlTemplate += ", %s";
            }
        }
        return annotationSqlTemplate;
    }

    private static Annotation.Polygon toDomainPolygon(VGG.Annotation.Region.ShapeAttribute shapeAttribute) {
        List<Double> allPointsX = shapeAttribute.getAllPointsX();
        List<Double> allPointsY = shapeAttribute.getAllPointsY();
        assert (allPointsY.size() == allPointsX.size()) : "y and x points number mismatch";

        List<Annotation.Point> polygonPoints = new ArrayList<>();
        for (int i = 0; i < allPointsX.size(); i++) {
            polygonPoints.add(
                    Annotation.Point.builder()
                            .x(allPointsX.get(i))
                            .y(allPointsY.get(i))
                            .build()
            );
        }

        return Annotation.Polygon.builder()
                .points(polygonPoints)
                .build();
    }

    @NotNull
    private static Label filterLabel(List<Label> labels, VGG.Annotation.Region.RegionAttribute regionAttribute) {
        return labels.stream().filter(l -> l.getName().equals(regionAttribute.getLabel())).findFirst().orElseThrow(() -> new RuntimeException("Could not find label " + regionAttribute.getLabel()));
    }

    public static String getRandomAnnotatorId(Random random) {
        int rnd = random.nextInt(ANNOTATOR_IDS.length);
        return ANNOTATOR_IDS[rnd];
    }

    private String writeValueAsString(Object object) {
        try {
            return OM.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
