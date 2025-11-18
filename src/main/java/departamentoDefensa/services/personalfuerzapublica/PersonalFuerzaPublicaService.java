package departamentoDefensa.services.personalfuerzapublica;

import departamentoDefensa.Auth.AuthResponse;
import departamentoDefensa.Auth.LoginRequest;
import departamentoDefensa.Auth.RegisterRequest;
import departamentoDefensa.entities.FuerzaPublica;
import departamentoDefensa.entities.PersonalFuerzaPublica;
import departamentoDefensa.entities.RangoPolicia;
import departamentoDefensa.excepciones.*;
import departamentoDefensa.repositories.PersonalFuerzaPublicaRepository;
import departamentoDefensa.services.email.EmailService;
import departamentoDefensa.services.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class PersonalFuerzaPublicaService implements BaseServicePersonalFuerzaPublica {

    private final PersonalFuerzaPublicaRepository personalRepository;
    private final JwtService jwtService;
    @Autowired
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private final AuthenticationManager authenticationManager;

    private final EmailService emailService;

    @Override
    public AuthResponse login(LoginRequest request) throws InvalidCredentialsException {
        // Buscar el usuario por nombre de usuario
        UserDetails user = personalRepository.findByIdentificacion(request.getIdentificacion())
                .orElseThrow(() -> new IdentificationNotFoundException("El usuario no existe"));

        try {
            // Autenticar el usuario
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getIdentificacion(),
                            request.getPassword()
                    )
            );
        } catch (Exception e) {
            // Lanza excepción personalizada si las credenciales son incorrectas
            throw new InvalidCredentialsException(
                    "Credenciales incorrectas. Verifique su nombre de usuario y contraseña."
            );
        }

        // Generar el token JWT si la autenticación es exitosa
        String token = jwtService.getToken(user);

        return AuthResponse.builder()
                .token(token)
                .build();
    }

    @Override
    public ResponseEntity register(RegisterRequest request) {
        // Validar el formato del correo electrónico
        if (!isValidEmail(request.getEmail())) {
            throw new InvalidEmailFormatException("El formato del correo electrónico es inválido.");
        }

        // 2. Validar dominio MX
        if (!domainHasMXRecord(request.getEmail())) {
            throw new InvalidEmailDomainException("El dominio del correo no es válido o no tiene servidores de correo.");
        }
        //Verificar si el email ya existe

        if(personalRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("El correo ya esta registrado con otro usuario");
        }

        if (personalRepository.findByIdentificacion(request.getIdentificacion()).isPresent()) {
            throw new DocumentAlreadyExistsException("La identificación ya existe.");
        }


        if(personalRepository.findByIdFuerza(request.getIdFuerza()).isPresent()){
            throw  new IDFuerzaAlreadyExistsExepction(" El id de fuerza ya existe.");
        }

        String generatedPassword = generateRandomPassword();

        //Verificar si el correo ya existe
        if (!isValidGeneratedPassword(generatedPassword)) {
            throw new RuntimeException("Error al generar contraseña válida.");
        }

        //Verificar si el telefono ya existe

        // Crear el administrador si pasa todas las validaciones
        PersonalFuerzaPublica newUser = new PersonalFuerzaPublica();
        newUser.setIdentificacion(request.getIdentificacion());
        newUser.setNombres(request.getNombres());
        newUser.setApellidos(request.getApellidos());
        try {
            newUser.setFuerzaPublica(FuerzaPublica.valueOf(request.getFuerzaPublica().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("La fuerza pública no es válida. Opciones: POLICIA, EJERCITO, ARMADA, FUERZA_AEREA");
        }
        newUser.setRango(RangoPolicia.valueOf(request.getRango().toUpperCase()));

        newUser.setIdFuerza(request.getIdentificacion());
        newUser.setEmail(request.getEmail());
        newUser.setActivo(true);
        String passwordGenerated= generateRandomPassword();
        newUser.setPasswordHash(passwordEncoder.encode(passwordGenerated));

        // Enviar correo con la contraseña
        emailService.sendPasswordEmail(newUser.getEmail(), passwordGenerated);

        try {
            create(newUser);
            return ResponseEntity.ok("Usuario registrado exitosamente. Revisa tu correo con la contraseña.");
        } catch (Exception e) {
            throw new RuntimeException("Error al registrar al usuario .");
        }
    }

    // Métodos de validación auxiliares
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(emailRegex);
    }

    private String generateRandomPassword() {
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";

        String all = upper + lower + digits;

        StringBuilder password = new StringBuilder();
        Random random = new Random();

        // Garantizar reglas
        password.append(upper.charAt(random.nextInt(upper.length())));
        password.append(lower.charAt(random.nextInt(lower.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));

        // completar hasta 6
        for (int i = 3; i < 6; i++) {
            password.append(all.charAt(random.nextInt(all.length())));
        }

        return password.toString();
    }

    private boolean isValidGeneratedPassword(String password) {
        return password.length() == 6 &&
                password.matches(".*[A-Z].*") &&
                password.matches(".*[a-z].*") &&
                password.matches(".*\\d.*");
    }


    @Override
// GET-SELECT
    public List<PersonalFuerzaPublica> findAll() throws Exception {

        try {

            return personalRepository.findAll();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }


    }

    //CREATE-INSERT INTO
    @Override
    public PersonalFuerzaPublica create(PersonalFuerzaPublica personalFuerzaPublica) throws Exception {
        try {

            return personalRepository.save(personalFuerzaPublica);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }



    //GET-SELECT
    @Override
    public PersonalFuerzaPublica findById(long id) throws Exception {
        try {

            PersonalFuerzaPublica personal = personalRepository.findById(id).orElse(null);
            //System.out.println(personalFuerzaPublica.getId_administrador()+" "+ administrador.getPassword());
            return personal ;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

    }

    //UPDATE
    @Override
    public  PersonalFuerzaPublica update(long id,  PersonalFuerzaPublica  entity) throws Exception {
        //PDATE `railway`.`administrador` SET `nombre_administrador` = '?', `contacto_administrador` = '?', `numero_documento_admin` = '?', `correo_administrador` = '?', `password_administrador` = '?'
        // WHERE (`id_administrador` = '?');
        try {
            Optional<PersonalFuerzaPublica> entityOpcional = personalRepository.findById(id);
            PersonalFuerzaPublica personalFuerza = entityOpcional.get();
            personalFuerza.setPasswordHash( passwordEncoder.encode(personalFuerza.getPassword()));
            personalFuerza = personalRepository.save(entity);
            return personalFuerza;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

    }
  /*
    @Override
    public PersonalFuerzaPublica update(long id, PersonalFuerzaPublica entity) throws Exception {
        try {
            PersonalFuerzaPublica personalFuerza = personalRepository.findById(id)
                    .orElseThrow(() -> new Exception("El usuario con id " + id + " no existe"));

            // Actualizar campos (excepto los que no quieres que cambien)
            personalFuerza.setNombres(entity.getNombres());
            personalFuerza.setApellidos(entity.getApellidos());
            personalFuerza.setIdentificacion(entity.getIdentificacion());
            personalFuerza.setEmail(entity.getEmail());
            personalFuerza.setRango(entity.getRango());
            personalFuerza.setFuerzaPublica(entity.getFuerzaPublica());

            // Si viene una nueva contraseña → encriptarla
            if (entity.getPassword() != null && !entity.getPassword().isBlank()) {
                personalFuerza.setPasswordHash(passwordEncoder.encode(entity.getPassword()));
            }

            return personalRepository.save(personalFuerza);

        } catch (Exception e) {
            throw new Exception("Error al actualizar usuario: " + e.getMessage());
        }
    }

*/


    //delete
    @Override
    public boolean delete(long id) throws Exception {

///DELETE  FROM railway.administrador where id_administrador = ?
        try {
            if (personalRepository.existsById(id)) {
                personalRepository.deleteById(id);
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return false;
    }




    private boolean domainHasMXRecord(String email) {
        try {
            String domain = email.substring(email.indexOf("@") + 1);

            Hashtable<String, String> env = new Hashtable<>();
            env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");

            DirContext ctx = new InitialDirContext(env);
            Attributes attrs = ctx.getAttributes(domain, new String[]{"MX"});
            Attribute attr = attrs.get("MX");

            return (attr != null && attr.size() > 0);

        } catch (Exception e) {
            return false;
        }
    }


}
