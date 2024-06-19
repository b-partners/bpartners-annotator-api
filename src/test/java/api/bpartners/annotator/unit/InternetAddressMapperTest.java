package api.bpartners.annotator.unit;

import static org.junit.jupiter.api.Assertions.assertThrows;

import api.bpartners.annotator.endpoint.rest.controller.mapper.InternetAddressMapper;
import api.bpartners.annotator.model.exception.BadRequestException;
import org.junit.jupiter.api.Test;

class InternetAddressMapperTest {
  @Test
  void invalid_internet_address_ko() {
    assertThrows(BadRequestException.class, () -> InternetAddressMapper.from(""));
  }
}
