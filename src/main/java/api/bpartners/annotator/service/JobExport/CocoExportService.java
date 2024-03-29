package api.bpartners.annotator.service.JobExport;

import api.bpartners.annotator.model.COCO;
import api.bpartners.annotator.repository.model.Annotation;
import api.bpartners.annotator.repository.model.AnnotationBatch;
import api.bpartners.annotator.repository.model.Job;
import api.bpartners.annotator.repository.model.Label;
import api.bpartners.annotator.repository.model.Task;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional
public class CocoExportService {
  public COCO export(Job job, List<AnnotationBatch> batches) {
    COCO coco = new COCO();

    coco.setInfo(getCocoInfos(job));
    coco.setImages(extractImageDetails(job));
    coco.setCategories(extractCategories(job));

    List<COCO.Annotation> cocoAnnotations = extractCocoAnnotations(batches);
    coco.setAnnotations(cocoAnnotations);

    return coco;
  }

  private List<COCO.Annotation> extractCocoAnnotations(List<AnnotationBatch> latestPerTaskByJobId) {
    return latestPerTaskByJobId.stream()
        .map(AnnotationBatch::getAnnotations)
        .map(a -> a.stream().map(this::getCocoAnnotation).toList())
        .reduce(
            new ArrayList<>(),
            (acc, cocoAnnotationList) -> {
              acc.addAll(cocoAnnotationList);
              return acc;
            });
  }

  private List<COCO.Category> extractCategories(Job job) {
    return job.getLabels().stream().map(this::toCocoCategory).toList();
  }

  private static Map<String, String> getCocoInfos(Job job) {
    return Map.of("description", job.getName());
  }

  private List<COCO.ImageDetail> extractImageDetails(Job job) {
    return job.getTasks().stream()
        .map(t -> getImageDetail(job.getImagesWidth(), job.getImagesHeight(), t))
        .toList();
  }

  private COCO.ImageDetail getImageDetail(int imageWidth, int imageHeight, Task task) {
    return COCO.ImageDetail.builder()
        .id(task.getId())
        .width(imageWidth)
        .height(imageHeight)
        .filename(task.getFilename())
        .build();
  }

  private COCO.Annotation getCocoAnnotation(Annotation annotation) {
    //	/!\ NOTE: needs transactional to work /!\
    Annotation.Polygon polygon = annotation.getPolygon();
    return COCO.Annotation.builder()
        .id(annotation.getId())
        .isCrowd(false)
        .imageId(annotation.getTaskId())
        .segmentation(getSegmentation(annotation))
        .categoryId(annotation.getLabel().getId())
        .boundingBox(polygon.getBoundingBox())
        .area(BigDecimal.valueOf(polygon.getArea()))
        .build();
  }

  private COCO.Category toCocoCategory(Label label) {
    return COCO.Category.builder().id(label.getId()).name(label.getName()).build();
  }

  public List<List<Double>> getSegmentation(Annotation annotation) {
    List<Double> uniqueSegmentation = new ArrayList<>();
    annotation
        .getPolygon()
        .getPoints()
        .forEach(
            point -> {
              uniqueSegmentation.add(point.getX());
              uniqueSegmentation.add(point.getY());
            });
    return List.of(uniqueSegmentation);
  }
}
