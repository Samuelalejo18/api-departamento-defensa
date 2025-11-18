package departamentoDefensa.services.personalfuerzapublica;

import departamentoDefensa.Auth.AuthResponse;
import departamentoDefensa.Auth.LoginRequest;
import departamentoDefensa.Auth.RegisterRequest;
import departamentoDefensa.entities.PersonalFuerzaPublica;
import departamentoDefensa.excepciones.InvalidCredentialsException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface BaseServicePersonalFuerzaPublica{
   //GET-SELECT-READ
    public List<PersonalFuerzaPublica> findAll() throws Exception;
    public PersonalFuerzaPublica findById(long id) throws Exception;

    //CREATE
    public PersonalFuerzaPublica create(PersonalFuerzaPublica personalfuerzapublica) throws Exception;
    //update
    public PersonalFuerzaPublica update(long id, PersonalFuerzaPublica personalfuerzapublica) throws Exception;
    //Detele
    public boolean delete(long id) throws Exception;

    public AuthResponse login(LoginRequest request) throws Exception, InvalidCredentialsException;
    public ResponseEntity register(RegisterRequest request) ;
}

