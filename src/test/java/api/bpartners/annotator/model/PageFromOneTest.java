package api.bpartners.annotator.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import api.bpartners.annotator.model.exception.BadRequestException;
import org.junit.jupiter.api.Test;

class PageFromOneTest {
  @Test
  void create_ok() {
    var actual = new PageFromOne(1);

    assertEquals(new PageFromOne(1), actual);
  }

  @Test
  void create_ko() {
    assertThrows(BadRequestException.class, () -> new PageFromOne(-1), "page size must be >=1");
  }
}
