package api.bpartners.annotator.model.exception;

public class BadRequestException extends ApiException {
  public BadRequestException(Exception e) {
    super(ExceptionType.CLIENT_EXCEPTION, e);
  }

  public BadRequestException(String message) {
    super(ExceptionType.CLIENT_EXCEPTION, message);
  }
}
