package api.bpartners.annotator.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
public class VGG extends HashMap<String, VGG.Annotation> {
  @AllArgsConstructor
  @Data
  @Builder
  @NoArgsConstructor
  public static class Annotation {
    @JsonProperty("fileref")
    private String fileRef;

    private Long size;
    private String filename;

    @JsonProperty("base64_img_data")
    private String base64ImageData;

    @JsonProperty("file_attributes")
    private Map<String, String> fileAttributes;

    private Map<String, Region> regions;

    @AllArgsConstructor
    @Data
    @Builder
    @NoArgsConstructor
    public static class Region {
      @JsonProperty("shape_attributes")
      private ShapeAttribute shapeAttribute;

      @JsonProperty("region_attributes")
      private RegionAttribute regionAttribute;

      @AllArgsConstructor
      @Data
      @Builder
      @NoArgsConstructor
      public static class ShapeAttribute {
        private String name;

        @JsonProperty("all_points_x")
        private List<Double> allPointsX;

        @JsonProperty("all_points_y")
        private List<Double> allPointsY;
      }

      @AllArgsConstructor
      @Data
      @Builder
      @NoArgsConstructor
      public static class RegionAttribute {
        private String label;
      }
    }
  }
}
