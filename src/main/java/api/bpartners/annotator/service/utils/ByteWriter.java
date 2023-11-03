package api.bpartners.annotator.service.utils;

import static api.bpartners.annotator.model.exception.ApiException.ExceptionType.SERVER_EXCEPTION;

import api.bpartners.annotator.model.exception.ApiException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ByteWriter implements Function<Object, byte[]> {
  private final ObjectMapper objectMapper;

  @Override
  public byte[] apply(Object object) {
    try {
      return objectMapper.writeValueAsBytes(object);
    } catch (JsonProcessingException e) {
      throw new ApiException(SERVER_EXCEPTION, "error during object conversion to bytes");
    }
  }
}
