package departamentoDefensa.Auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @NotBlank
    private String identificacion;
    @NotBlank
    private String nombres;
    @NotBlank
    private String apellidos;

    @NotBlank
    private String idFuerza;
    @NotBlank
    private String fuerzaPublica; // recibimos como String y convertimos al Enum
    @NotBlank
    private String rango;
    @NotBlank


    @Email
    @NotBlank
    private String email;
}
