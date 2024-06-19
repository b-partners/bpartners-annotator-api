package api.bpartners.annotator.endpoint.rest.controller.mapper;

import api.bpartners.annotator.model.exception.BadRequestException;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;

public class InternetAddressMapper {
  private InternetAddressMapper() {}

  public static InternetAddress from(String address) {
    try {
      return new InternetAddress(address);
    } catch (AddressException e) {
      throw new BadRequestException("invalid email " + address);
    }
  }
}
