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
@RequestMapping(path = "/user")
@RequiredArgsConstructor
public class PersonalFuerzaPublicaController {


    @Autowired
    private final PersonalFuerzaPublicaService personalFuerzaPublicaService;




}
