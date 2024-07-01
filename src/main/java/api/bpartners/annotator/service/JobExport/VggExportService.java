package api.bpartners.annotator.service.JobExport;

import api.bpartners.annotator.model.VGG;
import api.bpartners.annotator.repository.model.Annotation;
import api.bpartners.annotator.repository.model.AnnotationBatch;
import api.bpartners.annotator.repository.model.Job;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@AllArgsConstructor
public class VggExportService {
  public VGG export(Job job, List<AnnotationBatch> batches) {
    VGG vgg = new VGG();
    batches.forEach(
        batch -> {
          vgg.put(batch.getTask().getFilename(), toVggAnnotation(batch));
        });
    return vgg;
  }

  private static VGG.Annotation toVggAnnotation(AnnotationBatch batch) {
    var vggAnnotation = new VGG.Annotation();
    // <-- UNUSED_DATA put at default value
    vggAnnotation.setFileAttributes(
        Map.of(
            "creation_datetime",
            String.valueOf(batch.getCreationTimestamp()),
            "batch_id",
            batch.getId(),
            "task_id",
            batch.getTask().getId()));
    vggAnnotation.setSize(null);
    vggAnnotation.setBase64ImageData(null);
    // UNUSED_DATA -->

    vggAnnotation.setFilename(batch.getTask().getFilename());
    var vggRegions = getVggRegions(batch);
    vggAnnotation.setRegions(vggRegions);
    return vggAnnotation;
  }

  private static Map<String, VGG.Annotation.Region> getVggRegions(AnnotationBatch batch) {
    var regions = new HashMap<String, VGG.Annotation.Region>();
    List<Annotation> annotations = batch.getAnnotations();
    for (int i = 0; i < annotations.size(); i++) {
      var annotation = annotations.get(i);
      var region = new VGG.Annotation.Region();

      region.setShapeAttribute(getShapeAttributes(annotation));
      region.setRegionAttribute(getRegionAttributes(annotation));

      regions.put(String.valueOf(i), region);
    }
    return regions;
  }

  private static VGG.Annotation.Region.RegionAttribute getRegionAttributes(Annotation annotation) {
    var regionAttributes = new VGG.Annotation.Region.RegionAttribute();
    regionAttributes.setLabel(annotation.getLabel().getName());
    return regionAttributes;
  }

  private static VGG.Annotation.Region.ShapeAttribute getShapeAttributes(Annotation annotation) {
    var shapeAttributes = new VGG.Annotation.Region.ShapeAttribute();
    var polygonPoints = annotation.getPolygon().getPoints();
    List<Double> allPointsX = new ArrayList<>();
    List<Double> allPointsY = new ArrayList<>();
    polygonPoints.forEach(
        point -> {
          allPointsX.add(point.getX());
          allPointsY.add(point.getY());
        });
    shapeAttributes.setName("polygon");
    shapeAttributes.setAllPointsX(allPointsX);
    shapeAttributes.setAllPointsY(allPointsY);
    return shapeAttributes;
  }
}
