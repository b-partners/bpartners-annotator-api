package api.bpartners.annotator.conf;

import api.bpartners.annotator.PojaGenerated;
import org.springframework.test.context.DynamicPropertyRegistry;

@PojaGenerated
@SuppressWarnings("all")
public class EmailConf {

  void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("aws.ses.source", () -> "dummy-ses-source");
  }
}
