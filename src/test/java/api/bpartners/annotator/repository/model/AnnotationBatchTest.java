package api.bpartners.annotator.repository.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

// missed cases, hard to program, will be tested as unit
class AnnotationBatchTest {
  @Test
  void get_annotation_returns_empty_collection() {
    AnnotationBatch batch = new AnnotationBatch();
    assertTrue(batch.getAnnotations().isEmpty());
  }
}
