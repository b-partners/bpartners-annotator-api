package api.bpartners.annotator.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class COCO {
  private Map<String, String> info;
  private List<ImageDetail> images;
  private List<Annotation> annotations;
  private List<Category> categories;

  @AllArgsConstructor
  @Data
  @Builder
  public static class ImageDetail {
    private String id;
    private int height;
    private int width;

    @JsonProperty("file_name")
    private String filename;
  }

  @AllArgsConstructor
  @Data
  @Builder
  public static class Annotation {
    private String id;

    @JsonProperty("iscrowd")
    @Getter(AccessLevel.NONE)
    private boolean isCrowd;

    public int isCrowd() {
      return isCrowd ? 1 : 0;
    }

    @JsonProperty("image_id")
    private String imageId;

    @JsonProperty("category_id")
    private String categoryId;

    private List<List<Double>> segmentation;

    @JsonProperty("bbox")
    private List<Double> boundingBox;

    private BigDecimal area;
  }

  @AllArgsConstructor
  @Data
  @Builder
  public static class Category {
    private String id;
    private String name;
  }
}
