package departamentoDefensa.Auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.dao.DataIntegrityViolationException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        // Captura el mensaje de error y envíalo en la respuesta
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    // Otros métodos para manejar diferentes excepciones pueden ir aquí
}
