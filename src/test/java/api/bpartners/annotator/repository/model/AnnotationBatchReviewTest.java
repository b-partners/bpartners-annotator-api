package api.bpartners.annotator.repository.model;

import static api.bpartners.annotator.repository.model.enums.ReviewStatus.ACCEPTED;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class AnnotationBatchReviewTest {
  @Test
  void annotation_batch_review_equals_hashcode() {
    var abr1 =
        AnnotationBatchReview.builder()
            .id("id")
            .status(ACCEPTED)
            .annotationBatchId("annotationBatchId")
            .creationDatetime(Instant.MAX)
            .build();
    var abr2 =
        AnnotationBatchReview.builder()
            .id("id")
            .status(ACCEPTED)
            .annotationBatchId("annotationBatchId")
            .creationDatetime(Instant.MAX)
            .build();
    var abr3 =
        AnnotationBatchReview.builder()
            .id("id")
            .status(ACCEPTED)
            .annotationBatchId("annotationBatchId")
            .creationDatetime(Instant.MAX)
            .build();
    var abr4 = AnnotationBatchReview.builder().id("id_fake").build();

    assertEquals(abr1, abr2);
    assertEquals(abr2, abr1);
    assertEquals(abr3, abr1);
    assertEquals(abr3, abr1);
    assertNotEquals(abr1, abr4);
    assertNotEquals(null, abr1);
    assertEquals(abr1.hashCode(), abr2.hashCode());
    assertEquals(abr2.hashCode(), abr1.hashCode());
    assertEquals(abr3.hashCode(), abr1.hashCode());
    assertEquals(abr3.hashCode(), abr1.hashCode());
    assertNotEquals(abr1.hashCode(), abr4.hashCode());
  }
}
