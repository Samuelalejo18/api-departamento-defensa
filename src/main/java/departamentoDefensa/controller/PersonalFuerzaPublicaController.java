package departamentoDefensa.controller;


import departamentoDefensa.Auth.GetByIdentificationRequest;
import departamentoDefensa.excepciones.IdentificationNotFoundException;
import departamentoDefensa.services.personalfuerzapublica.PersonalFuerzaPublicaService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
//Dar permiso a los clientes
@CrossOrigin(origins = "*")
@RequestMapping(path = "")
@RequiredArgsConstructor
public class PersonalFuerzaPublicaController {


    @Autowired
    private final PersonalFuerzaPublicaService personalFuerzaPublicaService;


    @GetMapping(value = "/getByIdentification")
    public ResponseEntity<?> getByID(@RequestBody GetByIdentificationRequest request) {
        try {
            return ResponseEntity.ok(personalFuerzaPublicaService.findById(request.getIdentificacion()));

        } catch (IdentificationNotFoundException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());

        }  catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Violación de integridad de datos.");

        } catch (Exception e) { // ← SIEMPRE EL ÚLTIMO
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al traer el personal de la fuerza publica " + e.getMessage());
        }
    }

}
