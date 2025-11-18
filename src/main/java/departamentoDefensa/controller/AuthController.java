package departamentoDefensa.controller;

import departamentoDefensa.Auth.LoginRequest;
import departamentoDefensa.Auth.RegisterRequest;
import departamentoDefensa.excepciones.*;
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
@RequestMapping(path = "api/auth")
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private final PersonalFuerzaPublicaService personalFuerzaPublicaService;

    //endPoint login

    @PostMapping(value = "/login")
    public ResponseEntity<?> loginAdmin(@RequestBody LoginRequest request) {
        try {
            return ResponseEntity.ok(personalFuerzaPublicaService.login(request));
        }  catch (InvalidCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (IdentificationNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al iniciar sesión.");
        }
    }

    @PostMapping(value = "/register")
    public ResponseEntity<?> registerAdmin(@RequestBody RegisterRequest request) {
        try {
            return ResponseEntity.ok(personalFuerzaPublicaService.register(request));

        } catch (EmailAlreadyExistsException | DocumentAlreadyExistsException | IDFuerzaAlreadyExistsExepction e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());

        } catch (InvalidEmailFormatException | WeakPasswordException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

        } catch (InvalidEmailDomainException e) { // ← DEBE IR ANTES DE Exception
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Violación de integridad de datos.");

        } catch (Exception e) { // ← SIEMPRE EL ÚLTIMO
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al registrar al personal. " + e.getMessage());
        }
    }

/*
    //endPoint login
    @PostMapping(value = "/user/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest request) {
        try {
            return ResponseEntity.ok(usuarioService.login(request));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (InvalidCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al iniciar sesión.");
        }
    }
    */





}
