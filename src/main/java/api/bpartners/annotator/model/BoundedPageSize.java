package api.bpartners.annotator.model;

import api.bpartners.annotator.model.exception.BadRequestException;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
public class BoundedPageSize {
  @Getter private final int value;

  public static final int MAX_SIZE = 500;

  public BoundedPageSize(int value) {
    if (value < 1) {
      throw new BadRequestException("page size must be >=1");
    }
    if (value > MAX_SIZE) {
      throw new BadRequestException("page size must be <" + MAX_SIZE);
    }
    this.value = value;
  }
}
