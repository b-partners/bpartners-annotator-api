package api.bpartners.annotator.model.exception;

public class TooManyRequestsException extends ApiException {
  public TooManyRequestsException(String message) {
    super(ExceptionType.CLIENT_EXCEPTION, message);
  }
}
