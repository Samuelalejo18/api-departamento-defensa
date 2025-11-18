package departamentoDefensa.excepciones;

public class InvalidEmailDomainException extends RuntimeException {
  public InvalidEmailDomainException(String message) {
    super(message);
  }
}