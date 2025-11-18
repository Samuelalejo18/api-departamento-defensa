package departamentoDefensa.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;


@Entity
@Table(
        name = "personal_fuerza_publica",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"identificacion"}),
                @UniqueConstraint(columnNames = {"email"}),
                @UniqueConstraint(columnNames = {"id_fuerza"})
        }
)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class PersonalFuerzaPublica implements UserDetails, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id; // PK auto-incremental

    @Column(name = "identificacion", nullable = false, unique = true, length = 15)
    @NotBlank
    private String identificacion;

    @Column(name = "nombres", nullable = false)
    @NotBlank
    private String nombres;

    @Column(name = "apellidos", nullable = false)
    @NotBlank
    private String apellidos;

    @Enumerated(EnumType.STRING)
    @Column(name = "fuerza_publica", nullable = false)
    @NotNull
    private FuerzaPublica fuerzaPublica;

    @Enumerated(EnumType.STRING)
    @Column(name = "rango", nullable = false)
    @NotNull
    private RangoPolicia rango;

    @Column(name = "id_fuerza", nullable = false)
    @NotBlank
    private String idFuerza; // ID dentro de la fuerza

    @Column(name = "email", nullable = false, unique = true)
    @Email
    @NotBlank
    private String email;

    @Column(name = "password_hash", nullable = false)
    @NotBlank
    @JsonIgnore
    @Size(min = 60, max = 100) // longitud típica de BCrypt
    private String passwordHash;

    @Column(name = "activo", nullable = true)
    private boolean activo;

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return passwordHash;
    }
    @JsonIgnore
    @Override
    public String getUsername() {
        return this.identificacion;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }


}