package api.bpartners.annotator.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import api.bpartners.annotator.model.exception.BadRequestException;
import org.junit.jupiter.api.Test;

class BoundedPageSizeTest {
  @Test
  void create_ok() {
    var actual = new BoundedPageSize(10);

    assertEquals(new BoundedPageSize(10), actual);
  }

  @Test
  void create_ko() {
    assertThrows(BadRequestException.class, () -> new BoundedPageSize(-1), "page size must be >=1");
    assertThrows(
        BadRequestException.class, () -> new BoundedPageSize(1000), "page size must be < 500");
  }
}
